package org.integratedmodelling.corescience.interfaces;

import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * A IContext is a named template to build an IObservationContext for an observation 
 * structure. The useful class that implements this is in the modelling plugin, which binds it to
 * the "defcontext" clojure form and allows it to contain models that make it start with known
 * states besides the extents.
 * 
 * @author ferdinando.villa
 *
 */
public interface IContext  {

	/**
	 * Models must have an ID
	 * @return
	 */
	public abstract String getId();

	/**
	 * Return all the states that describe topology extents.
	 * @return
	 */
	public abstract Collection<IExtent> getExtents();
	
	/**
	 * Return a newly initialized IObservationContext to be used to contextualize the passed
	 * observation. This will build the common context and contextualization strategy for the
	 * obs, so it may be a long operation.
	 * 
	 * @param observation
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract IObservationContext getObservationContext(IObservation observation) throws ThinklabException;


}
