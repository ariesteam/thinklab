package org.integratedmodelling.corescience.interfaces;

import java.io.PrintWriter;
import java.util.Collection;

import org.integratedmodelling.corescience.listeners.IContextualizationListener;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

public interface IObservationContext {

	/**
	 * Get the observation we represent
	 * @return
	 */
	public IObservation getObservation();
	
	/**
	 * Return the total number of states in the context.
	 * @return
	 */
	public int getMultiplicity();
	
	/**
	 * Return the total number of states along one specific dimension.
	 * @param dimension
	 * @return
	 * @throws ThinklabException 
	 */
	public int getMultiplicity(IConcept dimension) throws ThinklabException;
	
	/**
	 * Return the least general concept of each separate dimension of the overall context,
	 * in the appropriate order for contextualization.
	 * 
	 * @return
	 */
	public Collection<IConcept> getDimensions();
	
	/**
	 * Return the number of dimensions along the extent
	 * @return
	 */
	public int getNumberOfDimensions();
	
	/**
	 * Return the specific concept of the dimension that is the passed concept, 
	 * or null if not there. Throw an exception if more than one dimensions exist for that
	 * concept.
	 * @param concept
	 * @return
	 */
	public IConcept getDimension(IConcept concept) throws ThinklabException;
	
	/**
	 * Get the extent along the specified dimension. Should simply return null if the extent isn't there.
	 * @param c
	 * @return
	 * @throws ThinklabException
	 */
	public IExtent getExtent(IConcept c);
	
	/**
	 * Return an array with the size of each extent in order of contextualization.
	 * @return
	 */
	public int[] getDimensionSizes();
	
	/**
	 * Produce the contextualized observation corresponding to the state of
	 * the merged contexts we represent.
	 * @param session
	 * @param listeners
	 * @return
	 * @throws ThinklabException
	 */
	public IInstance run(
			ISession session, 
			Collection<IContextualizationListener> listeners) throws ThinklabException;
	
	/**
	 * Produce a listing of the contextualization strategy
	 * @param out
	 */
	public void dump(PrintWriter out);

}
