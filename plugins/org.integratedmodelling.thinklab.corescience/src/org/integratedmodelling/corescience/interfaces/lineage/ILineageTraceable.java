package org.integratedmodelling.corescience.interfaces.lineage;

import java.util.Collection;


/**
 * Implemented by any object that derives from another. Must be at the very least capable of 
 * producing the object it comes from. It can be complicated at infinitum using semantics, 
 * serialization etc, but for now it's a simple thing to evolve as needs arise.
 * 
 * @author Ferdinando
 *
 */
public interface ILineageTraceable {

	public abstract Collection<Object> getAncestors();
	
}
