package org.integratedmodelling.thinklab.interfaces.storage;

import java.util.Collection;

import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
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
	 * Metadata schemata need to be defined in kbox properties, so we need a parser for their specification.
	 * @param metadataSpecs
	 */
	public abstract void parseDefinition(String metadataSpecs);
	
	/**
	 * 
	 * @return
	 */
	public abstract Collection<String> getFieldNames();
	
	/**
	 * 
	 * @param field
	 * @return
	 */
	public abstract IConcept getFieldType(String field);
	
	/**
	 * 
	 * @param field
	 * @param object
	 * @return
	 */
	public abstract IValue computeField(String field, IInstance object);
	
}
