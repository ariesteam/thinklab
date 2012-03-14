package org.integratedmodelling.thinklab.interfaces.storage;

import java.util.Set;

import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;

/*
 * Linked to a literal type to provide strategies for storage, indexing and querying. 
 */
public interface KboxTypeAdapter {

	/*
	 * submit a query for this type and return the set of results. Queries that implement
	 * LiteralQuery must return the concept for the comparison and the arguments. The adapter
	 * must check that the property type supports queries of that type, validate the arguments
	 * and run it to return the set of IDs of the matching objects. 
	 */
	Set<Long> submitLiteralQuery(IProperty property, IKbox kbox, IConcept queryType, Object ... arguments);

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
