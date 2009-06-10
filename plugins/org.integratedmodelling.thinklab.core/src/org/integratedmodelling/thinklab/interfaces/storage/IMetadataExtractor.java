package org.integratedmodelling.thinklab.interfaces.storage;

import java.util.Collection;

import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

public interface IMetadataExtractor {
	
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
