package org.integratedmodelling.corescience.implementations.cmodels;

import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.literals.IRandomValue;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public class RandomStateAccessor implements IStateAccessor {

	private boolean isConstant = false;
	private IRandomValue value = null;
	private int index = 0;
	private IDataSource<?> ds = null;
	
	public RandomStateAccessor(IDataSource<?> dataSource) {
		this.ds = dataSource;
	}

	public RandomStateAccessor(IRandomValue value) {
		this.isConstant = true;
		this.value = value;
	}

	@Override
	public Object getValue(Object[] registers) {
		return isConstant ? value : getNextValue();
	}
	
	private Object getNextValue() {
		return ds.getValue(index++);
	}
	
	@Override
	public boolean hasInitialState() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConstant() {
		return isConstant;
	}

	@Override
	public boolean notifyDependencyObservable(IConcept observable)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void notifyDependencyRegister(IConcept observable, int register,
			IConcept stateType) throws ThinklabValidationException {
		// TODO Auto-generated method stub

	}
	
	@Override
	public String toString() {
		return "[RandomAccessor]";
	}

}
