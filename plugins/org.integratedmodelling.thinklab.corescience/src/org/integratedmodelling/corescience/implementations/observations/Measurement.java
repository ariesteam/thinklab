package org.integratedmodelling.corescience.implementations.observations;

import javax.measure.converter.UnitConverter;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.MemDoubleContextualizedDatasource;
import org.integratedmodelling.corescience.interfaces.IMergingObservation;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.interfaces.internal.MediatingObservation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.interfaces.literals.IRandomValue;
import org.integratedmodelling.corescience.literals.DistributionValue;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.corescience.units.Unit;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;

/**
 * Implementation for instances of measurements. Admits definition of simple "value unit" cases
 * through a single observation:value property.
 *  
 * @author Ferdinando
 *
 */
@InstanceImplementation(concept="measurement:Measurement,measurement:Count")
public class Measurement extends Observation implements MediatingObservation {
	
	protected String unitSpecs = null;
	String valueSpecs = null;
	protected IRandomValue randomValue = null;
	protected double inlineValue = 0;
	protected double value = 0.0;
    public Unit unit;
    
	// set through reflection
	public DistributionValue distribution = null;
	public    boolean isCount = false;

	protected boolean isConstant = false;
	protected boolean isUnitless = false;
	
	CoreScience.PhysicalNature physicalNature = null;
	
	@Override
	public String toString() {
		return ("measurement(" + getObservableClass() + "): " + unitSpecs);
	}

	public class MeasurementAccessor implements IStateAccessor {

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
			return getNextValue(registers);
		}

		private Object getNextValue(Object[] registers) {
			
			if (distribution != null)
				return distribution.draw();
			
			return 
				isConstant ? 
					(randomValue == null ? value : randomValue) : 
					 getDataSource().getValue(index++, registers);
		}

		@Override
		public boolean isConstant() {
			return isConstant;
		}
		
		@Override
		public String toString() {
			return "[MeasurementAccessor]";
		}

		@Override
		public void notifyState(IState dds, IObservationContext overallContext,
				IObservationContext ownContext)  throws ThinklabException {
		}
	}

	public class MeasurementMediator implements IStateAccessor {
		
	    protected javax.measure.unit.Unit<?> otherUnit;
		private UnitConverter converter;
		private int reg = 0;

		public MeasurementMediator(Measurement other) {

			this.otherUnit = other.unit.getUnit();
			this.converter = 
				unit.equals(otherUnit) ? 
					null :
					otherUnit.getConverterTo(unit.getUnit());
		}
		
		@Override
		public boolean notifyDependencyObservable(IObservation o, IConcept observable, String formalName)
				throws ThinklabException {
			return true;
		}

		@Override
		public void notifyDependencyRegister(IObservation observation, IConcept observable,
				int register, IConcept stateType) throws ThinklabException {
			this.reg = register;
		}

		@Override
		public Object getValue(int idx, Object[] registers) {
			return converter == null ? 
					registers[reg] : 
					((registers[reg] == null || 
							(registers[reg] instanceof Number && Double.isNaN(((Number)registers[reg]).doubleValue()))) ? 
							Double.NaN : 
							converter.convert(((Number)(registers[reg])).doubleValue()));
		}

		@Override
		public boolean isConstant() {
			return false;
		}

		@Override
		public String toString() {
			return "[MeasurementMediator {"+ unit + " ->" + otherUnit + "}]";
		}

		@Override
		public void notifyState(IState dds, IObservationContext overallContext,
				IObservationContext ownContext)  throws ThinklabException  {

		}

	}
	
	@Override
	public void initialize(IInstance i) throws ThinklabException {

		// only check if not preset to true by reflection
		if (!isCount) {
			isCount = i.getDirectType().is(CoreScience.COUNT);
		}
		
		// lookup defs - either unit and value or textual definition of both
		IValue v = i.get(CoreScience.HAS_VALUE);
		
		if (v != null) {
			
			String s = v.toString();
			int idx = s.indexOf(' ');
			
			if (idx >= 0) {
				valueSpecs = s.substring(0, idx).trim();
				unitSpecs = s.substring(idx+1).trim();
			} else {
				valueSpecs = s;
			}
			isConstant = true;
			value = Double.parseDouble(valueSpecs);
			
		} 
		
		if (unitSpecs == null) {
			v = i.get(CoreScience.HAS_UNIT);
			if (v != null)
				unitSpecs = v.toString().trim();
		}
		
		v = i.get("measurement:distribution");
		if (v != null) {
			if (valueSpecs != null)
				throw new ThinklabValidationException(
						"measurement value can contain either random or numeric values, not both");
			randomValue = new DistributionValue(v.toString());
		}
		
		if (unitSpecs == null) {
			throw new ThinklabValidationException("measurement: units not specified");
		}
		
		this.unit = new Unit(unitSpecs);
		this.isUnitless = this.unit.isUnitless();
		
		super.initialize(i);

		/*
		 * validate observable. Must be physical property or a count with unitless units.
		 */
		if (!observable.is(CoreScience.PHYSICAL_PROPERTY) && !(isCount || isUnitless)) {
				throw new ThinklabValidationException(
					"measurements can only be of physical properties: " + 
						observable.getDirectType());
		}

		/*
		 * determine if intensive or extensive from the semantics of the observable,
		 * and communicate to the conceptual model. 
		 */
		physicalNature = 
			(
				isCount || 
				observable.is(CoreScience.EXTENSIVE_PHYSICAL_PROPERTY) ||
				observable.is(CoreScience.EXTENSIVE_QUANTITY)
			)?
			CoreScience.PhysicalNature.EXTENSIVE :
			CoreScience.PhysicalNature.INTENSIVE;
		
		metadata.put(Metadata.CONTINUOUS, Boolean.TRUE);
		metadata.put(Metadata.PHYSICAL_NATURE, physicalNature);
		metadata.put(Metadata.UNIT, this.unit);
		
		/*
		 * validate units against extents
		 */
		validateExtents();
		
	}

	private void validateExtents() throws ThinklabValidationException {

		// the unit must reflect all topologies if we have an extensive observable. We don't check
		// the other way around; that is done when the overall context is defined, so we can provide
		// what's missing from the outside.
		if (isUnitless || physicalNature.equals(CoreScience.PhysicalNature.EXTENSIVE)) {
			for (Topology t : getTopologies()) {
				t.checkUnitConformance(getObservableClass(), unit);
			}
		}
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		
		return Polylist.list(
				CoreScience.MEASUREMENT,
				Polylist.list(CoreScience.HAS_OBSERVABLE,
						(getObservable() instanceof IConceptualizable) ? 
								((IConceptualizable)getObservable()).conceptualize() :
								getObservable().toList(null)),
				(randomValue == null ?
						Polylist.list("measurement:unit", unitSpecs):
						Polylist.list("measurement:distribution", unitSpecs)));
	}

	@Override
	public IStateAccessor getMediator(IndirectObservation observation, IObservationContext context)
			throws ThinklabException {

		/*
		 * if we're mediating an observation that merges others, we assume that its first dependency will
		 * describe all of them. 
		 */
		if (observation instanceof IMergingObservation)
			observation = (IndirectObservation) observation.getDependencies()[0];
		
		if ( ! (observation instanceof Measurement))
			throw new ThinklabValidationException("measurements can only mediate other measurements");
		return new MeasurementMediator(((Measurement)observation));
	}

	@Override
	public IStateAccessor getAccessor(IObservationContext context) {
		return new MeasurementAccessor();
	}

	@Override
	public IConcept getStateType() {
		return randomValue == null ? KnowledgeManager.Double() : CoreScience.RandomValue();
	}

	@Override
	public IState createState(int size, IObservationContext context) throws ThinklabException {
		IState ret = new MemDoubleContextualizedDatasource(
				getObservableClass(), size, (ObservationContext)context);
		ret.getMetadata().merge(this.metadata);
		return ret;
	}
	
	@Override
	public void validateOverallContext(IObservationContext ctx) {
		
		// TODO perform unit validation and conversion of extensive values
		
	}

}
