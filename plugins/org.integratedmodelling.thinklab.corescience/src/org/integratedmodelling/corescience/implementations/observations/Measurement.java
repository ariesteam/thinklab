package org.integratedmodelling.corescience.implementations.observations;

import javax.measure.converter.UnitConverter;
import javax.measure.unit.Unit;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.implementations.datasources.MemDoubleContextualizedDatasource;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.interfaces.internal.MediatingObservation;
import org.integratedmodelling.corescience.interfaces.literals.IRandomValue;
import org.integratedmodelling.corescience.literals.DistributionValue;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Polylist;

/**
 * Implementation for instances of measurements. Admits definition of simple "value unit" cases
 * through a single observation:value property.
 *  
 * @author Ferdinando
 *
 */
@InstanceImplementation(concept="measurement:Measurement")
public class Measurement extends Observation implements MediatingObservation {
	
	protected String unitSpecs = null;
	String valueSpecs = null;
	private IRandomValue randomValue = null;
	double inlineValue = 0;
	private double value = 0.0;
    Unit<?> unit;
    
	private boolean isConstant = false;
	
	public enum PhysicalNature {
		EXTENSIVE,
		INTENSIVE
	}
	
	PhysicalNature physicalNature = null;
	
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
		public Object getValue(Object[] registers) {
			return isConstant ? (randomValue == null ? value : randomValue) : getNextValue(registers);
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
			return "[MeasurementAccessor]";
		}
	}

	public class MeasurementMediator implements IStateAccessor {
		
	    protected Unit<?> otherUnit;
		private UnitConverter converter;
		private int reg = 0;

		public MeasurementMediator(Measurement other) {

			this.otherUnit = other.unit;
			this.converter = 
				unit.equals(otherUnit) ? 
					null :
					unit.getConverterTo(otherUnit);
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
		public Object getValue(Object[] registers) {
			return converter == null ? registers[reg] : converter.convert((Double)registers[reg]);
		}

		@Override
		public boolean isConstant() {
			return false;
		}

		@Override
		public String toString() {
			return "[MeasurementMediator {"+ unit + " ->" + otherUnit + "}]";
		}

	}
	@Override
	public void initialize(IInstance i) throws ThinklabException {

		// lookup defs - either unit and value or textual definition of both
		IValue v = i.get("observation:value");
		
		if (v != null) {
			
			String s = v.toString();
			int idx = s.indexOf(' ');
			
			if (idx >= 0) {
				valueSpecs = s.substring(0, idx).trim();
				unitSpecs = s.substring(idx+1).trim();
			} else {
				throw new ThinklabValidationException(
						"measurement value must contain numeric value and units: " + s);
			}
		} else {
			
			// may just have unit and link to datasource or mediated obs
			v = i.get("measurement:unit");
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
		
		this.unit = Unit.valueOf(unitSpecs);
		
		super.initialize(i);

		/*
		 * validate observable. This should be redundant if OWL validation is
		 * working, but no big deal to add it.
		 */
		if (!observable.is(CoreScience.PHYSICAL_PROPERTY)) {
			throw new ThinklabValidationException("measurements can only be of physical properties: " + observable);
		}

		/*
		 * determine if intensive or extensive from the semantics of the observable,
		 * and communicate to the conceptual model. 
		 */
		physicalNature = 
			observable.is(CoreScience.EXTENSIVE_PHYSICAL_PROPERTY) ?
			PhysicalNature.EXTENSIVE :
			PhysicalNature.INTENSIVE;
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
	public IStateAccessor getMediator(IndirectObservation observation)
			throws ThinklabException {

		if ( ! (observation instanceof Measurement))
			throw new ThinklabValidationException("measurements can only mediate other measurements");
		return new MeasurementMediator(((Measurement)observation));
	}

	@Override
	public IStateAccessor getAccessor() {
		return new MeasurementAccessor();
	}

	@Override
	public IConcept getStateType() {
		return randomValue == null ? KnowledgeManager.Double() : CoreScience.RandomValue();
	}

	@Override
	public IState createState(int size) throws ThinklabException {
		return new MemDoubleContextualizedDatasource(getObservableClass(), size);
	}

}
