package org.integratedmodelling.thinklab.implementations.operators;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.literals.BooleanValue;

/**
 * TODO this is a topology operator (like contains) so it should end up in Corescience, not here
 * @author Ferdinando Villa
 *
 */
@InstanceImplementation(concept="thinklab-core:Intersection")
public class Intersects extends Operator {

	@Override
	public IValue eval(Object... arg) throws ThinklabException {
		/*
		 * TODO 
		 * FIXME
		 * this is BS, it's meant for strings, but the op should test
		 * topologies.
		 */
		return new BooleanValue(asText(arg[0]).contains(asText(arg[1])));
	}

	
}
