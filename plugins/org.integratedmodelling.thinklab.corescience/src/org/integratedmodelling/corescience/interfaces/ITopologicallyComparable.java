package org.integratedmodelling.corescience.interfaces;

import org.integratedmodelling.exceptions.ThinklabException;

/**
 * A topological object can be compared with topological operators to another of a 
 * compatible class.
 * 
 * TODO implement remaining contract from SFS or other
 * 
 * @author Ferdinando Villa
 *
 */
public interface ITopologicallyComparable {
	
	/**
	 * 
	 * @param o
	 * @return
	 * @throws ThinklabException
	 */
	public abstract boolean contains(ITopologicallyComparable o) throws ThinklabException;
	
	/**
	 * 
	 * @param o
	 * @return
	 * @throws ThinklabException
	 */
	public abstract boolean overlaps(ITopologicallyComparable o) throws ThinklabException;
	
	/**
	 * 
	 * @param o
	 * @return
	 * @throws ThinklabException
	 */
	public abstract boolean intersects(ITopologicallyComparable o) throws ThinklabException;

}
