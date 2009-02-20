package org.integratedmodelling.corescience.implementations.cmodels;

import javax.measure.converter.UnitConverter;

import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.literals.UnitValue;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public class MeasurementStateMediator implements IStateAccessor {

	private UnitValue uFrom;
	private UnitValue uTo;
	private UnitConverter converter;
	private int reg = 0;

	public MeasurementStateMediator(UnitValue unitFrom, UnitValue unitTo) {
		this.uFrom = unitFrom;
		this.uTo = unitTo;
		this.converter = uFrom.getUnit().getConverterTo(uTo.getUnit());
	}
	
	@Override
	public boolean notifyDependencyObservable(IConcept observable)
			throws ThinklabValidationException {
		return true;
	}

	@Override
	public void notifyDependencyRegister(IConcept observable, int register,
			IConcept stateType) throws ThinklabValidationException {
		this.reg = register;
	}

	@Override
	public Object getValue(Object[] registers) {
		return converter.convert((Double)registers[reg]);
	}

	@Override
	public boolean hasInitialState() {
		return false;
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
