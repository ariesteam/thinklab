package org.integratedmodelling.corescience.interfaces;

import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * Implemented by anything which is a physical entity or can produce one or more physical entities.
 * Used to extract identifications from observations of inherent properties. The current use is 
 * for derived representations such as grid extents, which may implement IEntifiable or have an IEntifiable in their
 * lineage so that the objects they were constructed from, if any (e.g. from rasterizing identifications of
 * spatial entities) can be reconstructed.
 *  
 * @author Ferdinando
 *
 */
public interface IEntifiable {

	/**
	 * Produce the entity or entities that this object represents. 
	 * 
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract Collection<IValue> getEntities() throws ThinklabException;
}
