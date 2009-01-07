package org.integratedmodelling.thinklab.operators;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.value.NumberValue;

public class Minus extends Operator {

	@Override
	public IValue eval(Object... arg) throws ThinklabException {
		return new NumberValue(asDouble(arg[0])-asDouble(arg[1]));
	}
}
