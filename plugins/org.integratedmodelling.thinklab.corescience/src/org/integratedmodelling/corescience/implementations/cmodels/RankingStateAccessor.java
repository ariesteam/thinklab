package org.integratedmodelling.corescience.implementations.cmodels;

import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
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
