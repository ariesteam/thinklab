package org.integratedmodelling.thinklab.implementations.operators;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.literals.NumberValue;

@InstanceImplementation(concept="thinklab-core:Multiplication")
public class Mult extends Operator {

	@Override
	public IValue eval(Object... arg) throws ThinklabException {
		return new NumberValue(asDouble(arg[0])*asDouble(arg[1]));
	}

}
