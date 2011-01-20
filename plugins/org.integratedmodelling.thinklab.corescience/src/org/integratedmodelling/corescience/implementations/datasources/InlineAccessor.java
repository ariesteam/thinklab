package org.integratedmodelling.corescience.implementations.datasources;

import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public class InlineAccessor extends DefaultAbstractAccessor {

	IState _inlineState;
	
	public InlineAccessor(IState state) {
		_inlineState = state;
	}
	
	@Override
	public boolean notifyDependencyObservable(IObservation o,
			IConcept observable, String formalName) throws ThinklabException {
		return false;
	}

	@Override
	public void notifyDependencyRegister(IObservation observation,
			IConcept observable, int register, IConcept stateType)
			throws ThinklabException {
	}

	@Override
	public Object getValue(int overallContextIndex, Object[] registers) {
		return this._inlineState.getValue(overallContextIndex);
	}

	@Override
	public boolean isConstant() {
		return false;
	}

}
