package org.integratedmodelling.corescience.implementations.cmodels;

import javax.measure.converter.UnitConverter;

import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.corescience.literals.UnitValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public class MeasurementStateMediator implements IStateAccessor {

	private UnitValue uFrom;
	private UnitValue uTo;
	private UnitConverter converter;
	private int reg = 0;

	public MeasurementStateMediator(UnitValue unitFrom, UnitValue unitTo) {
		this.uFrom = unitFrom;
		this.uTo = unitTo;
		this.converter = 
			uFrom.equals(uTo) ? 
				null :
				uFrom.getUnit().getConverterTo(uTo.getUnit());
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
		return "[MeasurementMediator {"+ uFrom + " ->" + uTo + "}]";
	}

}
