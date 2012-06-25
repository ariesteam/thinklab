package org.integratedmodelling.thinklab.modelling.span;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IComputingAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IParallelAccessor;
import org.integratedmodelling.thinklab.api.modelling.IState;

public class SPANAccessor implements IParallelAccessor, IComputingAccessor {

	@Override
	public IConcept getStateType() {
		return Thinklab.NUMBER;
	}

	@Override
	public Object getValue(int contextIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notifyDependency(ISemanticObject<?> observable, String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void process(int stateIndex) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(String inputKey, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getValue(String outputKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notifyDependency(IState state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyExpectedOutput(ISemanticObject<?> observable, String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void process(IContext context) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IState getState(String outputKey) {
		// TODO Auto-generated method stub
		return null;
	}

}
