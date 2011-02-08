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
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.MemDoubleContextualizedDatasource;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.interfaces.internal.MediatingObservation;
import org.integratedmodelling.corescience.literals.DistributionValue;
import org.integratedmodelling.corescience.metadata.Metadata;
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
@InstanceImplementation(concept="measurement:Ranking,measurement:NumericCoding,measurement:BinaryCoding")
public class Ranking extends Observation implements MediatingObservation {

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

	protected boolean isConstant = false;
	protected boolean isBinary = false;
	protected double value = 0.0;
	
	// set through reflection
	public DistributionValue distribution = null;


	@Override
	public String toString() {
		return ("ranking(" + getObservableClass() + ")");
	}
	
	public class RankingMediator implements IStateAccessor {

		private int reg;
		private double conversion = 1.0;
		private double offset = 0.0;
		private boolean integer = false;
		private boolean noConv = true;
		
		public RankingMediator() {
			// this one won't mediate much
		}

		public RankingMediator(IndirectObservation o) throws ThinklabValidationException {

			if (! (o instanceof Ranking))
				throw new ThinklabValidationException("a ranking can only mediate another ranking");
			
			double ownMin = minV;
			double ownMax = maxV;
			double othMin = ((Ranking)o).minV;
			double othMax = ((Ranking)o).maxV;
			
			this.conversion = (ownMax - ownMin)/(othMax - othMin);
			this.offset = ownMin;
		}
		
		@Override
		public Object getValue(int idx, Object[] registers) {
			return noConv? registers[reg] : convert((Double)(registers[reg]));
		}

		public double convert(double d) {
		
			double ret = d*conversion;
			ret += offset;
		
			if (integer)
				ret = Math.rint(ret);
			return ret;
		}
		
		@Override
		public boolean isConstant() {
			return isConstant;
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
			this.reg = register;
		}

		@Override
		public void notifyState(IState dds, IObservationContext overallContext,
				IObservationContext ownContext) throws ThinklabException  {

		}
		
	}

	@Override
	public IConcept getStateType() {
		return KnowledgeManager.Double();
	}
	
	private boolean bounded() {
		return leftBounded && rightBounded;
	}	
	
	public class RankingStateAccessor implements IStateAccessor {
		
		private int index = 0;
		
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
		public Object getValue(int idx, Object[] registers) {
			Object ret = null;
			if (distribution != null)
				ret = distribution.draw();
			else
				ret = isConstant ? value : getNextValue(registers);
			
			if (isBinary && ret != null && ret instanceof Number) {
				ret = (((Number)ret).doubleValue() == 0.0 || 
						(ret instanceof Double && ((Double)ret).isNaN()) ||
						(ret instanceof Float && ((Float)ret).isNaN())) ? 0.0 : 1.0;
			}
			
			return ret;
		}

		private Object getNextValue(Object[] registers) {
			return getDataSource().getValue(index++, registers);
		}

		@Override
		public boolean isConstant() {
			return isConstant;
		}
		
		@Override
		public String toString() {
			return "[RankingAccessor]";
		}

		@Override
		public void notifyState(IState dds, IObservationContext overallContext,
				IObservationContext ownContext) throws ThinklabException  {

		}

	}
	
//	/**
//	 * TODO move to the validator and pass it boundaries.
//	 * 
//	 * @param val
//	 * @throws ThinklabValidationException
//	 */
//	private void checkBoundaries(double val) throws ThinklabValidationException {
//
//		// TODO need a smart way to define IDs for observations, conceptual models etc so we can
//		// generate appropriate error messages.
//		if ((leftBounded && val < minV) || rightBounded && (val > maxV))
//			throw new ThinklabValidationException("value " + val + " out of boundaries");
//		if (integer && Double.compare(val - Math.floor(val), 0.0) != 0)
//			throw new ThinklabValidationException("value " + val + " is not an integer as requested");
//	}
//

	@Override
	public IState createState(int size, IObservationContext context) throws ThinklabException {
		IState ret= new MemDoubleContextualizedDatasource(
						getObservableClass(), size, (ObservationContext)context);
		ret.getMetadata().merge(this.metadata);
		return ret;
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		super.initialize(i);
		
		// read in scale attributes and pass to CM
		IValue min = i.get(MINVALUE_PROPERTY);
		IValue max = i.get(MAXVALUE_PROPERTY);
		IValue isi = i.get(ISINTEGER_PROPERTY);
		IValue iss = i.get(ISSCALE_PROPERTY);
		IValue iva = i.get(CoreScience.HAS_VALUE);

		if (min != null)
			minV = min.asNumber().asDouble();
		if (max != null) 
			maxV = max.asNumber().asDouble();
		if (isi != null)
			integer = BooleanValue.parseBoolean(isi.toString());
		if (iss != null)
			isScale = BooleanValue.parseBoolean(iss.toString());

		if (iva != null) {
			value = Double.parseDouble(iva.toString());
			isConstant = true;
		}
		
		if (i.getDirectType().is(CoreScience.BINARY_CODING)) {
			this.isBinary  = true;
		}
		// TODO add min-max etc
		metadata.put(Metadata.CONTINUOUS, Boolean.TRUE);

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


	@Override
	public IStateAccessor getMediator(IndirectObservation observation, IObservationContext context)
			throws ThinklabException {

		RankingMediator ret = null;
		
		if ( !(observation instanceof Ranking)) {
				throw new ThinklabValidationException("can't mediate between " + this.getClass() +
					" and " + observation.getClass());
		}	

		Ranking other = (Ranking)observation;
			
		if ((isScale && !other.isScale) || 
				(!isScale && other.isScale))
			throw new ThinklabValidationException("scale ranking can't be mediated with non-scale");

	
		/**
		 * if rankings aren't fully bounded left and right, we just pass them along, and the
		 * conformance of the observable is our guarantee of compatibility. CM validation will
		 * catch values out of bounds.
		 */
		if (!bounded() || !other.bounded()) {
			return new RankingMediator();
		}
	
		/*
		 * we only need to mediate ranking models that are different.
		 */
		if (minV != other.minV || maxV != other.maxV || integer != other.integer) {
			ret = new RankingMediator(other);
		}
		
		return ret;
	}


	@Override
	public IStateAccessor getAccessor(IObservationContext context) {
		return new RankingStateAccessor();
	}

}
