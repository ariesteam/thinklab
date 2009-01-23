/**
 * RankingModel.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCoreSciencePlugin.
 * 
 * ThinklabCoreSciencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCoreSciencePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.corescience.observation.ranking;

import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IValueAggregator;
import org.integratedmodelling.corescience.interfaces.cmodel.IValueMediator;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.observation.ConceptualModel;
import org.integratedmodelling.corescience.observation.IObservation;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.value.NumberValue;
import org.integratedmodelling.utils.Pair;
import org.jscience.mathematics.number.Rational;

/**
 * Conceptual model for a discrete ranking - a classification bound to actual numeric ranges. 
 * @author Ferdinando Villa
 *
 *
 * TODO identical to Ranking (continuous) for now - to be written.
 */
public class DiscretizedRankingModel extends ConceptualModel {

	boolean leftBounded = false;
	boolean rightBounded = false;
	boolean integer = false;
	boolean isScale = false;
	double min = 0.0;
	double max = 0.0;
	
	/**  
	 * simple aggregator for ranks, considered as a quality measure and therefore treated
	 * like an intensive property.
	 */ 
	public class RankingAggregator implements IValueAggregator {

		double val = 0.0;
		boolean isnew = true;
		IUncertainty unc = null;
		
		// FIXME uncertainty is thrown in without even thinking.
		public void addValue(IValue value, IUncertainty uncertainty, IObservationContextState contextState) throws ThinklabException {

			if (!value.isNumber()) 
				throw new ThinklabValidationException("non-numerical ranking cannot be aggregated");
			
			if (uncertainty != null) {
				if (unc != null)
					unc.compound(uncertainty);
				else unc = uncertainty;
			}
			
			val = isnew ? value.asNumber().asDouble() : (val + value.asNumber().asDouble())/2;
			
			isnew = false;
		}

		public Pair<IValue, IUncertainty> aggregateAndReset() throws ThinklabException {
			
			Pair<IValue,IUncertainty> ret = 
				new Pair<IValue, IUncertainty>(new NumberValue(val), null);
			val = 0.0;
			unc = null;
			return ret;
		}
		
	}
	
	/**
	 * A simple mediator to compare bounded rankings with different scales.
	 * @author Ferdinando Villa
	 *
	 */
	public class RankingMediator implements IValueMediator {

		// TODO we can just store a normalization factor
		double conversion = 0.0;
		double offset = 0.0;
		boolean integer;
		
		private double convert(double d) {
			
			double ret = d*conversion;
			ret += offset;
			
			if (integer)
				ret = Math.rint(ret);
			return ret;
		}
		
		public RankingMediator(double ownMin, double ownMax, boolean integer, double othMin, double othMax) {

			this.conversion = (ownMax - ownMin)/(othMax - othMin);
			this.offset = ownMin;
			this.integer = integer;
		}
		
		public IValue getMediatedValue(IValue value, IObservationContextState context) throws ThinklabException {
			return new NumberValue(convert(value.asNumber().asDouble()));
		}

		public Pair<IValue, IUncertainty> getMediatedValue(IValue value, IUncertainty uncertainty, IObservationContextState context) {
			// FIXME not called for now; we will need it later
			return null;
		}

		/**
		 * TODO
		 * Sort of - actually we lose precision if any ranking is integer, but we know no
		 * uncertainty model for now.
		 */
		public boolean isExact() {
			return true;
		}
		
	}
	
	public DiscretizedRankingModel(
			double minV, double maxV, boolean integer, 
			boolean leftBounded, boolean rightBounded,
			boolean isScale) {
		this.min = minV;
		this.max = maxV;
		this.integer = integer;
		this.leftBounded = leftBounded;
		this.rightBounded = rightBounded;
		this.isScale = isScale;
	}

	protected boolean bounded() {
		return leftBounded && rightBounded;
	}
	
	public IValueAggregator getAggregator(IObservationContext ownContext,
			IObservationContext overallContext) {
		return new RankingAggregator();
	}

	public IValueMediator getMediator(IConceptualModel conceptualModel,
			IObservationContext ctx) throws ThinklabException {
		
		RankingMediator ret = null;
		
		if (!(conceptualModel instanceof DiscretizedRankingModel)) {
			throw new ThinklabValidationException("can't mediate between " + this.getClass() +
					" and " + conceptualModel.getClass());
		}
		
		if ((isScale && !((DiscretizedRankingModel)conceptualModel).isScale) || 
			(!isScale && ((DiscretizedRankingModel)conceptualModel).isScale))
			throw new ThinklabValidationException("scale ranking can't be mediated with non-scale");

		
		/**
		 * if rankings aren't fully bounded left and right, we just pass them along, and the
		 * conformance of the observable is our guarantee of compatibility. CM validation will
		 * catch values out of bounds.
		 */
		if (!bounded() || !((DiscretizedRankingModel)conceptualModel).bounded()) {
			return null;
		}
		
		/*
		 * we only need to mediate ranking models that are different.
		 */
		if (min != ((DiscretizedRankingModel)conceptualModel).min || 
			max != ((DiscretizedRankingModel)conceptualModel).max ||
			integer != ((DiscretizedRankingModel)conceptualModel).integer) {
			
			ret = new RankingMediator(
					min, max, integer,
					((DiscretizedRankingModel)conceptualModel).min,
					((DiscretizedRankingModel)conceptualModel).max);
		}
			
		return ret;
	}

	public IConcept getStateType() {
		return KnowledgeManager.Double();
	}

	/** it's an assessment and shouldn't be distributed, so the partition is the value
	 	itself
	*/
	public IValue partition(IValue originalValue, Rational ratio) {
		return originalValue;
	}

	public void validate(IObservation observation)
			throws ThinklabValidationException {
	
		if (isScale && !bounded())
			throw new ThinklabValidationException("scaled ranking must be bounded: provide minimum and maximum value");
	}

	public IValue validateLiteral(String value,
			IObservationContextState contextState)
			throws ThinklabValidationException {

		double val = Double.parseDouble(value);
		checkBoundaries(val);
		return new NumberValue(val);
	}

	private void checkBoundaries(double val) throws ThinklabValidationException {

		// TODO need a smart way to define IDs for observations, conceptual models etc so we can
		// generate appropriate error messages.
		if ((leftBounded && val < min) || rightBounded && (val > max))
			throw new ThinklabValidationException("value " + val + " out of boundaries");
		if (integer && Double.compare(val - Math.floor(val), 0.0) != 0)
			throw new ThinklabValidationException("value " + val + " is not an integer as requested");

	}

	public IValue validateValue(IValue value,
			IObservationContextState contextState)
			throws ThinklabValidationException {
		
		checkBoundaries(value.asNumber().asDouble());
		return value;
	}

	public IValue validateData(byte b) throws ThinklabValidationException {

		double dv = (double)b;
		checkBoundaries(b);
		return new NumberValue(dv);
	}

	public IValue validateData(int b) throws ThinklabValidationException {

		double dv = (double)b;
		checkBoundaries(b);
		return new NumberValue(dv);
	}

	public IValue validateData(long b) throws ThinklabValidationException {

		double dv = (double)b;
		checkBoundaries(b);
		return new NumberValue(dv);
	}

	public IValue validateData(float b) throws ThinklabValidationException {

		double dv = (double)b;
		checkBoundaries(b);
		return new NumberValue(dv);
	}

	public IValue validateData(double b) throws ThinklabValidationException {

		double dv = (double)b;
		checkBoundaries(b);
		return new NumberValue(dv);
	}

	@Override
	public IConcept getUncertaintyType() {
		// TODO Auto-generated method stub
		return KnowledgeManager.Nothing();
	}


}
