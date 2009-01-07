package org.integratedmodelling.thinklab.operators;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.value.BooleanValue;

public class Between extends Operator {

	@Override
	public IValue eval(Object... arg) throws ThinklabException {

		boolean ret = false;
		
		if (isNumeric(arg[0])) {
			ret =
				asDouble(arg[0]) >= asDouble(arg[1]) &&
				asDouble(arg[0]) <= asDouble(arg[2]);
		}
		
		return new BooleanValue(ret);
		
	}
}
