package org.integratedmodelling.thinklab.implementations.operators;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;

/**
 * TODO this is a topology operator (like contains) so it should end up in Corescience, not here
 * @author Ferdinando Villa
 *
 */
@InstanceImplementation(concept="thinklab-core:Intersection")
public class Union extends Operator {

	@Override
	public IValue eval(Object... arg) throws ThinklabException {
		return null;
	}

	@Override
	public String getName() {
		return "union";
	}

}
