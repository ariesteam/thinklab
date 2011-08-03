package org.integratedmodelling.thinklab.implementations.operators;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;

/**
 * TODO this is a topology operator (like contains) so it should end up in Corescience, not here
 * @author Ferdinando Villa
 *
 */
public class Intersection extends Operator {

	@Override
	public IValue eval(Object... arg) throws ThinklabException {
		return null;
	}

	@Override
	public String getName() {
		return "intersection";
	}

}
