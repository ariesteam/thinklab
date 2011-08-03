package org.integratedmodelling.thinklab.implementations.operators;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;

@InstanceImplementation(concept="thinklab-core:GreaterThan")
public class Gt extends Operator {

	@Override
	public IValue eval(Object... arg) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return ">";
	}

}
