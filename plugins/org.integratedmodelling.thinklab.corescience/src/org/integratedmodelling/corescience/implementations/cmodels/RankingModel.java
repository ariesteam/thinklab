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
package org.integratedmodelling.corescience.implementations.cmodels;

import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtentMediator;
import org.integratedmodelling.corescience.interfaces.cmodel.IStateValidator;
import org.integratedmodelling.corescience.interfaces.cmodel.IValueAggregator;
import org.integratedmodelling.corescience.interfaces.cmodel.ScalingConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.ValidatingConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.NumberValue;
import org.integratedmodelling.utils.Pair;
import org.jscience.mathematics.number.Rational;

/**
 * Conceptual model for a simple numeric ranking. 
 * @author Ferdinando Villa
 *
 */
public class RankingModel implements IConceptualModel, ValidatingConceptualModel, ScalingConceptualModel {

	boolean leftBounded = false;
	boolean rightBounded = false;
	boolean integer = false;
	boolean isScale = false;
	double min = 0.0;
	double max = 0.0;
	
	IDataSource<?> datasource = null;
	Double inlineValue = null;
	
	/**  
	 * simple aggregator for ranks, considered as a quality measure and therefore treated
	 * like an intensive property.
	 */ 
	public class RankingAggregator implements IValueAggregator<Double> {

		double val = 0.0;
		boolean isnew = true;

		public void addValue(Double value, IObservationContextState contextState) throws ThinklabException {

			val = isnew ? value : (val + value)/2;
			isnew = false;
		}

		public Double aggregateAndReset() throws ThinklabException {
			
			double ret = val;
			val = 0.0;
			return ret;
		}

		@Override
		public Double partition(Double originalValue, Rational ratio) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	/**
	 * A simple mediator to compare bounded rankings with different scales.
	 * TODO make this a IStateAccessor and return it when the dependency is
	 * another ranking.
	 * 
	 * @author Ferdinando Villa
	 *
	 */
//	public class RankingMediator implements IMediator {
//
//		// TODO we can just store a normalization factor
//		double conversion = 0.0;
//		double offset = 0.0;
//		boolean integer;
//		
//		private double convert(double d) {
//			
//			double ret = d*conversion;
//			ret += offset;
//			
//			if (integer)
//				ret = Math.rint(ret);
//			return ret;
//		}
//		
//		public RankingMediator(double ownMin, double ownMax, boolean integer, double othMin, double othMax) {
//
//			this.conversion = (ownMax - ownMin)/(othMax - othMin);
//			this.offset = ownMin;
//			this.integer = integer;
//		}
//		
//		public IValue getMediatedValue(IValue value, IObservationContextState context) throws ThinklabException {
//			return new NumberValue(convert(value.asNumber().asDouble()));
//		}
//
//		public Pair<IValue, IUncertainty> getMediatedValue(IValue value, IUncertainty uncertainty, IObservationContextState context) {
//			// FIXME not called for now; we will need it later
//			return null;
//		}
//
//		/**
//		 * TODO
//		 * Sort of - actually we lose precision if any ranking is integer, but we know no
//		 * uncertainty model for now.
//		 */
//		public boolean isExact() {
//			return true;
//		}
//		
//	}
	
	public RankingModel(
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


//	public IMediator getMediator(IConceptualModel conceptualModel,
//			IObservationContext ctx) throws ThinklabException {
//		
//		RankingMediator ret = null;
//		
//		if (!(conceptualModel instanceof RankingModel)) {
//			throw new ThinklabValidationException("can't mediate between " + this.getClass() +
//					" and " + conceptualModel.getClass());
//		}
//		
//		if ((isScale && !((RankingModel)conceptualModel).isScale) || 
//			(!isScale && ((RankingModel)conceptualModel).isScale))
//			throw new ThinklabValidationException("scale ranking can't be mediated with non-scale");
//
//		
//		/**
//		 * if rankings aren't fully bounded left and right, we just pass them along, and the
//		 * conformance of the observable is our guarantee of compatibility. CM validation will
//		 * catch values out of bounds.
//		 */
//		if (!bounded() || !((RankingModel)conceptualModel).bounded()) {
//			return null;
//		}
//		
//		/*
//		 * we only need to mediate ranking models that are different.
//		 */
//		if (min != ((RankingModel)conceptualModel).min || 
//			max != ((RankingModel)conceptualModel).max ||
//			integer != ((RankingModel)conceptualModel).integer) {
//			
//			ret = new RankingMediator(
//					min, max, integer,
//					((RankingModel)conceptualModel).min,
//					((RankingModel)conceptualModel).max);
//		}
//			
//		return ret;
//	}

	public IConcept getStateType() {
		return KnowledgeManager.Double();
	}

	public void validate(IObservation observation)
			throws ThinklabValidationException {
	
		if (isScale && !bounded())
			throw new ThinklabValidationException("scaled ranking must be bounded: provide minimum and maximum value");
	}


	/**
	 * TODO move to the validator and pass it boundaries.
	 * 
	 * @param val
	 * @throws ThinklabValidationException
	 */
	private void checkBoundaries(double val) throws ThinklabValidationException {

		// TODO need a smart way to define IDs for observations, conceptual models etc so we can
		// generate appropriate error messages.
		if ((leftBounded && val < min) || rightBounded && (val > max))
			throw new ThinklabValidationException("value " + val + " out of boundaries");
		if (integer && Double.compare(val - Math.floor(val), 0.0) != 0)
			throw new ThinklabValidationException("value " + val + " is not an integer as requested");

	}

	@Override
	public IStateAccessor getStateAccessor(IConcept stateType, IObservationContext context) {

		if (inlineValue != null)
			return new RankingStateAccessor(inlineValue);
		else if (datasource != null) 
			return new RankingStateAccessor(datasource);
		
		return null;
	}

	@Override
	public IStateValidator getValidator(IConcept valueType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handshake(IDataSource<?> dataSource,
			IObservationContext observationContext,
			IObservationContext overallContext) throws ThinklabException {
		
		this.datasource = dataSource;
	}

	@Override
	public IValueAggregator<?> getAggregator(IObservationContext ownContext,
			IObservationContext overallContext, IExtentMediator[] mediators) {
		return new RankingAggregator();
	}


}
