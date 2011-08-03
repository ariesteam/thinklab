package org.integratedmodelling.thinklab.implementations.operators;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.literals.BooleanValue;

@InstanceImplementation(concept="thinklab-core:Containment")
public class Contains extends Operator {

	@Override
	public IValue eval(Object... arg) throws ThinklabException {
		return new BooleanValue(asText(arg[0]).contains(asText(arg[1])));
	}

	@Override
	public String getName() {
		return "contains";
	}

}
