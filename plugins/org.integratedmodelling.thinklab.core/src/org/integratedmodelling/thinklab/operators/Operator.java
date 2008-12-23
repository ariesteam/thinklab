package org.integratedmodelling.thinklab.operators;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IOperator;

public abstract class Operator implements IOperator {

	String id = null;
	
	@Override
	public String getOperatorId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {
		this.id = i.getLocalName();
	}

	@Override
	public void validate(IInstance i) throws ThinklabException {
		// TODO Auto-generated method stub

	}

}
