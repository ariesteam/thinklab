/**
 * Ranking.java
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
package org.integratedmodelling.corescience.implementations.observations;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtentMediator;
import org.integratedmodelling.corescience.interfaces.cmodel.IStateValidator;
import org.integratedmodelling.corescience.interfaces.cmodel.IValueAggregator;
import org.integratedmodelling.corescience.interfaces.cmodel.MediatingConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.ScalingConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.ValidatingConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.integratedmodelling.utils.Polylist;
import org.jscience.mathematics.number.Rational;

/**
 * A ranking is the simplest of quantifications, defining the observable through
 * a numeric state that may or may not be bounded. Bounded ranks of different
 * scales can be mediated if they have been defined to represent scales. 
 * 
 * Ranks are double by default but can be constrained to
 * integers. Rankings are useful in providing an immediate translation for
 * non-semantic "variables", e.g. in legacy models seen as observation
 * structures.
 * 
 * For ease of specification, rankings contain all their conceptual model
 * parameters in their own properties, and create and configure the conceptual
 * model automatically during validation.
 * 
 * @author Ferdinando Villa
 *
 */
@InstanceImplementation(concept="measurement:Ranking")
public class Ranking extends Observation implements IConceptualizable {

	private static final String MINVALUE_PROPERTY = "measurement:minValue";
	private static final String MAXVALUE_PROPERTY = "measurement:maxValue";
	private static final String ISINTEGER_PROPERTY = "measurement:isInteger";
	private static final String ISSCALE_PROPERTY = "measurement:isScale";
	
	double minV = 0.0;
	double maxV = -1.0;
	boolean integer = false;
	boolean isScale = false;
	boolean leftBounded = false;
	boolean rightBounded = false;
	
	@Override
	public String toString() {
		return ("ranking(" + getObservableClass() + ")");
	}
	
	/**
	 * Conceptual model for a simple numeric ranking. 
	 * @author Ferdinando Villa
	 *
	 */
	public class RankingModel implements IConceptualModel, MediatingConceptualModel, ValidatingConceptualModel, ScalingConceptualModel {
		
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
		
		public class RankingMediator implements IStateAccessor {

			private int reg;
			private double conversion = 1.0;
			private double offset = 0.0;
			private boolean integer = false;
			private boolean noConv = true;

			public RankingMediator(double ownMin, double ownMax, boolean integer, double othMin, double othMax) {
				this.conversion = (ownMax - ownMin)/(othMax - othMin);
				this.offset = ownMin;
				this.integer = integer;
			}
			
			public RankingMediator() {
			}

			@Override
			public Object getValue(Object[] registers) {
				return noConv? registers[reg] : convert((Double)(registers[reg]));
			}

			private double convert(double d) {
			
				double ret = d*conversion;
				ret += offset;
			
				if (integer)
					ret = Math.rint(ret);
				return ret;
			}
			

			@Override
			public boolean isConstant() {
				return false;
			}

			@Override
			public boolean notifyDependencyObservable(IObservation o,
					IConcept observable, String formalName)
					throws ThinklabException {
				return true;
			}

			@Override
			public void notifyDependencyRegister(IObservation observation,
					IConcept observable, int register, IConcept stateType)
					throws ThinklabException {
				// TODO Auto-generated method stub
				this.reg = register;
			}
			
		}

		protected boolean bounded() {
			return leftBounded && rightBounded;
		}

		protected double getMin() {
			return minV;
		}
		
		protected double getMax() {
			return maxV;
		}

		protected boolean isInteger() {
			return integer;
		}
		
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
			if ((leftBounded && val < minV) || rightBounded && (val > maxV))
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

		@Override
		public IContextualizedState createContextualizedStorage(int size)
				throws ThinklabException {
			// fine as is, we create POD.
			return null;
		}

		@Override
		public IStateAccessor getMediator(IConceptualModel conceptualModel,
				IConcept stateType, IObservationContext context)
				throws ThinklabException {
			
			RankingMediator ret = null;
			
			if (!(conceptualModel instanceof RankingModel)) {
				throw new ThinklabValidationException("can't mediate between " + this.getClass() +
					" and " + conceptualModel.getClass());
			}
		
			if ((isScale && !isScale) || 
					(!isScale && isScale))
				throw new ThinklabValidationException("scale ranking can't be mediated with non-scale");

		
			/**
			 * if rankings aren't fully bounded left and right, we just pass them along, and the
			 * conformance of the observable is our guarantee of compatibility. CM validation will
			 * catch values out of bounds.
			 */
			if (!bounded() || !((RankingModel)conceptualModel).bounded()) {
				return new RankingMediator();
			}
		
			/*
			 * we only need to mediate ranking models that are different.
			 */
			if (minV != ((RankingModel)conceptualModel).getMin() || 
					maxV != ((RankingModel)conceptualModel).getMax() ||
					integer != ((RankingModel)conceptualModel).isInteger()) {
			
				ret = new RankingMediator(
						minV, maxV, integer,
						((RankingModel)conceptualModel).getMin(),
						((RankingModel)conceptualModel).getMax());
			}
			
			return ret;
		}
	}
	
	public class RankingStateAccessor implements IStateAccessor {

		private boolean isConstant = false;
		private double value = 0.0;
		private int index = 0;
		private IDataSource<?> ds = null;

		public RankingStateAccessor(double value) {
			this.isConstant = true;
			this.value = value;
		}
		
		public RankingStateAccessor(IDataSource<?> src) {
			this.ds = src;
		}
		
		@Override
		public boolean notifyDependencyObservable(IObservation o, IConcept observable, String formalName)
				throws ThinklabException {
			// we don't need anything
			return false;
		}

		@Override
		public void notifyDependencyRegister(IObservation observation, IConcept observable,
				int register, IConcept stateType) throws ThinklabException {
			// won't be called
		}

		@Override
		public Object getValue(Object[] registers) {
			return isConstant ? value : getNextValue(registers);
		}

		private Object getNextValue(Object[] registers) {
			return ds.getValue(index++, registers);
		}

		@Override
		public boolean isConstant() {
			return isConstant;
		}
		
		@Override
		public String toString() {
			return "[RankingAccessor]";
		}

	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		super.initialize(i);
		
		// read in scale attributes and pass to CM
		IValue min = i.get(MINVALUE_PROPERTY);
		IValue max = i.get(MAXVALUE_PROPERTY);
		IValue isi = i.get(ISINTEGER_PROPERTY);
		IValue iss = i.get(ISSCALE_PROPERTY);

		if (min != null)
			minV = min.asNumber().asDouble();
		if (max != null) 
			maxV = max.asNumber().asDouble();
		if (isi != null)
			integer = BooleanValue.parseBoolean(isi.toString());
		if (iss != null)
			isScale = BooleanValue.parseBoolean(iss.toString());
	}

	@Override
	public IConceptualModel createMissingConceptualModel() throws ThinklabException {
		return new RankingModel();
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {

		return Polylist.list(
				CoreScience.RANKING,
				Polylist.list(CoreScience.HAS_OBSERVABLE,
						(getObservable() instanceof IConceptualizable) ? 
								((IConceptualizable)getObservable()).conceptualize() :
								getObservable().toList(null)),
						Polylist.list(MINVALUE_PROPERTY, minV+""),
						Polylist.list(MAXVALUE_PROPERTY, maxV+""),
						Polylist.list(ISINTEGER_PROPERTY, integer ? "true" : "false"),
						Polylist.list(ISSCALE_PROPERTY, isScale ? "true" : "false"));
	}

}
