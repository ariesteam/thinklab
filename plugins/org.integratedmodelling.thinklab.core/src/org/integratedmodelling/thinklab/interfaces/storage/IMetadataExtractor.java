package org.integratedmodelling.thinklab.interfaces.storage;

import java.util.Map;

import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * An object that can be passed to a kbox to define and compute fields that will be accessible for query at the object
 * level. According to implementation, these can be computed from the actual objects inserted.
 * 
 * @author Ferdinando
 *
 */
public interface IMetadataExtractor {
	
	/**
	 * 
	 * @param field
	 * @param object
	 * @return
	 */
	public abstract Map<String, IValue> extractMetadata(IInstance object);
	
}
