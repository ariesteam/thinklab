package org.integratedmodelling.corescience.interfaces.literals;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;

public interface IModifiableUnit {

	public abstract void validateModifier(String modifier) 
		throws ThinklabValidationException;
	
	public abstract double convert(IModifiableUnit to, String modFrom, String modTo)
		throws ThinklabValidationException;
	
}
