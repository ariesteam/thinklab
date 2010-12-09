package org.integratedmodelling.modelling.interfaces;

import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;

/**
 * What comes out of a defcontext statement. Basically a named template to build
 * an array of topologies that can generate an initial ObservationContext for a 
 * model.
 * 
 * Conceptualizes to a new observation structure based on the info in the states.
 * 
 * @author ferdinando.villa
 *
 */
public interface IContext extends IConceptualizable {

	/**
	 * Models must have an ID
	 * @return
	 */
	public abstract String getId();

	/**
	 * Get the spatial scale if any.
	 * @return
	 */
	public abstract IState getSpace();

	/**
	 * Get the temporal scale if any.
	 * @return
	 */
	public abstract IState getTime();

	/**
	 * Get the state of the given concept.
	 * 
	 * @param concept
	 * @return
	 */
	public abstract IState getState(IConcept concept);

}
