package org.integratedmodelling.thinklab.modelling.interfaces;

import org.integratedmodelling.thinklab.api.modelling.IAccessor;

/**
 * Accessors defined using the 'with' idiom should be chaining accessors, capable of 
 * receiving the "natural" accessor for the observer they were defined on, and using it
 * to resolve any data before doing their own computation.
 * 
 * @author Ferd
 *
 */
public interface IChainingAccessor extends IAccessor {

	public void chain(IAccessor naturalAccessor);
}
