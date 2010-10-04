package org.integratedmodelling.modelling.interfaces;

import org.integratedmodelling.corescience.interfaces.IObservationContext;

/**
 * What comes out of a defcontext statement. Basically a named template to build
 * an array of topologies that can generate an initial ObservationContext for a 
 * model.
 * 
 * @author ferdinando.villa
 *
 */
public interface IContext {

	/**
	 * Models must have an ID
	 * @return
	 */
	public abstract String getId();

	/**
	 * Get the observation context correspondent to this.
	 * 
	 * @return
	 */
	public abstract IObservationContext getContext();
}
