package org.integratedmodelling.thinklab.interfaces.storage;

import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.utils.Polylist;

/**
 * Recognize a source of objects from a URL and allows retrieval of object definitions
 * contained in it. Use together with the KnowledgeLoader annotation if necessary to 
 * link to a specific format tag or extension. 
 * 
 * @author Ferdinando Villa
 *
 */
public interface IKnowledgeImporter {

	public void initialize(String url, Properties properties) throws ThinklabException;
	
	/**
	 * Number of objects in the source.
	 * 
	 * @return
	 */
	public int getObjectCount();
	
	/**
	 * ID of object #n
	 * @param i
	 * @return
	 */
	public String getObjectId(int i) throws ThinklabException;
	
	/**
	 * Create the n-th object, use the passed properties (and the global ones if
	 * necessary) to define the semantics if required.
	 * 
	 * @param i
	 * @param properties
	 * @return
	 */
	public Polylist getObjectDefinition(int i, Properties properties) throws ThinklabException ;
}
