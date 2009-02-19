package org.integratedmodelling.corescience.interfaces.context;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

/**
 * Testing new approach to contextualization. Used only for development at the 
 * moment.
 * 
 * It should not be passed anything but optional runtime parameters or listeners. All
 * the info to run a contextualization should be compiled in.
 * 
 * TODO there should be a serializable subclass that can store the contextualization as code or
 * other persistent object that can be run multiple times.
 * 
 * @author Ferdinando
 *
 */
public interface IContextualizer {
	
	/**
	 * This must return a new observation made up of datasets - i.e., all the observations we tagged as being
	 * part of the result (all by default) with the context passed to contextualize() and the calculated states
	 * as the datasource. 
	 * @throws ThinklabValidationException 
	 * @throws ThinklabException 
	 */
	public IInstance run(ISession session) throws ThinklabValidationException, ThinklabException;
	
}
