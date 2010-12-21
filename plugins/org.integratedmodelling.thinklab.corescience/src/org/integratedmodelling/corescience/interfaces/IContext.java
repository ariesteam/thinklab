package org.integratedmodelling.corescience.interfaces;

import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

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
	 * Return the extent for a specific topology observable, or null if not there.
	 * 
	 * @param observable
	 * @return
	 */
	public abstract IExtent getExtent(IConcept observable);
	
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

	/**
	 * Return true if all the extents in this context are either absent in the passed one, or if present
	 * don't determine a null context when intersected.
	 * 
	 * @param context
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract boolean intersects(IContext context) throws ThinklabException;

	/**
	 * 
	 * @return
	 */
	public IExtent getTime();

	/**
	 * 
	 * @return
	 */
	public IExtent getSpace();


}
