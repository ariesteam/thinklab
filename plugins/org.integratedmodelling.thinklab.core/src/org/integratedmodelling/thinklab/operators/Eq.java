package org.integratedmodelling.thinklab.operators;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.value.BooleanValue;

public class Eq extends Operator {

	@Override
	public IValue eval(Object... arg) throws ThinklabException {
		return new BooleanValue(arg[0].equals(arg[1]));
	}

}
