package org.integratedmodelling.corescience.implementations.cmodels;

import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

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
	public boolean notifyDependencyObservable(IConcept observable)
			throws ThinklabValidationException {
		// we don't need anything
		return false;
	}

	@Override
	public void notifyDependencyRegister(IConcept observable, int register,
			IConcept stateType) throws ThinklabValidationException {
		// won't be called
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
		return false;
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
