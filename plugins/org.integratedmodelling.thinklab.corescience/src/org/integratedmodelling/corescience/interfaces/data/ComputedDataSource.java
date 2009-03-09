package org.integratedmodelling.corescience.interfaces.data;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * Computed datasources are notified of dependencies at handshake.
 * 
 * @author Ferdinando Villa
 *
 */
public interface ComputedDataSource {

	/**
	 * Called for each stateful observable declared as a dependency in the observation. Must prepare
	 * to use it according to the passed concept, or refuse it by throwing exceptions.
	 * 
	 * @param observable
	 * @param type
	 * @param register the index of the state in the register array passed to getValue()
	 * @throws ThinklabValidationException
	 */
	public void notifyDependency(IConcept observable, IConcept type, int register)  throws ThinklabValidationException;
	
	/**
	 * Called once after all dependencies have been notified. Should ensure that all the needed 
	 * dependencies have been notified. Has a chance to return a different
	 * datasource (e.g. optimized) that will be used instead. Otherwise return self - never null.
	 * 
	 * @throws ThinklabValidationException
	 */
	public IDataSource<?> validateDependencies() throws ThinklabValidationException;
	
}
