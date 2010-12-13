package org.integratedmodelling.modelling.literals;

import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.literals.Value;

/**
 * Just a shuttle object for a context so it can be returned easily by semantically aware
 * functions, and eventually parsed from a literal. Takes its type from the observation it
 * describes.
 * 
 * @author Ferdinando
 *
 */
public class ContextValue extends Value {
	
	IObservationContext c;

	public ContextValue(IObservationContext c) {
		this.c = c;
	}
	
	@Override
	public String toString() {
		return c.toString();
	}

	@Override
	public boolean isPODType() {
		return false;
	}

	@Override
	public IConcept getConcept() {
		return c.getObservation().getObservableClass();
	}

	public IObservationContext getObservationContext() {
		return c;
	}
	
}
