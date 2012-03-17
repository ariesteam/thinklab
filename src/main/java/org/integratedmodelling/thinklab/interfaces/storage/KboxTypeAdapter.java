package org.integratedmodelling.thinklab.interfaces.storage;

import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;

/*
 * Linked to a literal type to provide strategies for storage and indexing. 
 */
public interface KboxTypeAdapter {
	
	/*
	 * set the given property for the object with the given id, using the
	 * appropriate strategy to index the result so that it can be queried
	 * later. 
	 * 
	 * The object will be of the type registered with this adapter. Concept
	 * will be found by annotating it first.
	 * 
	 */
	void setAndIndexProperty(long id, IKbox kbox, IProperty property, Object value);
	
}
