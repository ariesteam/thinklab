package org.integratedmodelling.modelling.interfaces;

import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * A dataset is a persistent ObservationContext. Should be persistent and be able to
 * reconstruct the context it came from.
 * 
 * @author Ferdinando Villa
 */
public interface IDataset {

	/**
	 * Set the context for the dataset. If there is one, ensure compatibility of extents
	 * and merge states from it.
	 * 
	 * @param context
	 * @throws ThinklabException
	 */
	public abstract void setContext(IObservationContext context) throws ThinklabException;
	
	/**
	 * Return the context we represent, creating it if we were loaded from persistent
	 * storage.
	 * 
	 * @param context
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IObservationContext getContext(IObservationContext context) 
		throws ThinklabException;

	/**
	 * Ensure we can get GC's without losing data.
	 * 
	 * @throws ThinklabException
	 */
	public abstract void persist() throws ThinklabException;
	
	/**
	 * Read the dataset from assigned storage.
	 * @throws ThinklabException
	 */
	public abstract void restore() throws ThinklabException;
	
}
