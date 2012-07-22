package org.integratedmodelling.thinklab.kbox.neo4j;

import java.util.Set;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.knowledge.query.IOperator;
import org.neo4j.graphdb.Node;

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
	void setAndIndexProperty(Node node, IKbox kbox, IProperty property, Object value) throws ThinklabException;
	
	/**
	 * Use whatever indexing strategy is appropriate to search and return the 
	 * set of object IDs that match the given operator applied to the given 
	 * property.
	 * 
	 * @param kbox
	 * @param property
	 * @param operator
	 * @return
	 */
	Set<Long> searchIndex(IKbox kbox, IProperty property, IOperator operator) throws ThinklabException;
	
}