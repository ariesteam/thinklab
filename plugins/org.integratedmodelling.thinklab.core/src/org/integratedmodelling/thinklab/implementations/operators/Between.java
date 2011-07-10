package org.integratedmodelling.thinklab.implementations.operators;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.literals.BooleanValue;

/**
 * 
 * @author Ferdinando
 *
 */
@InstanceImplementation(concept="thinklab-core:WithinInterval")
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
