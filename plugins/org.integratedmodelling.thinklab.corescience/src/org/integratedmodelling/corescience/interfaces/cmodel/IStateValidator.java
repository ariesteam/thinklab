package org.integratedmodelling.corescience.interfaces.cmodel;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;

/**
 * TODO interface must be entirely reconsidered - the object knows the state type, and will have
 * to extract it from the contextualizer.
 * 
 * @author Ferdinando
 *
 */
public interface IStateValidator {

	/**
	 * Can be called by the datasource when it does not know what to do with a byte value extracted. Should
	 * return an IValue that we know how to do something with, or raise a validation exception.
	 *  
	 * @param b
	 * @return
	 */
	public abstract Object validateData(Object b) throws ThinklabValidationException;

}
