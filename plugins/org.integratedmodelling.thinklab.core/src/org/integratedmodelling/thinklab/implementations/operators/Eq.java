package org.integratedmodelling.thinklab.implementations.operators;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.BooleanValue;

@InstanceImplementation(concept="thinklab-core:Equality")
public class Eq extends Operator {

	@Override
	public IValue eval(Object... arg) throws ThinklabException {
		return new BooleanValue(arg[0].equals(arg[1]));
	}

}
