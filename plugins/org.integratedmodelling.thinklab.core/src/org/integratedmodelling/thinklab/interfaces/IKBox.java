/**
 * IKBox.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.interfaces;

import java.util.HashMap;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.utils.Polylist;

/**
 * <p>Objects implementing IKBox contain instances and support insertion, retrieval, deletion and query. In general, they are optimized for
 * large storage and fast query, contrary to Ontologies. The IKBox (for Knowledge
 * Box, as a slightly friendlier version of the more proper "Assertion Box" or abox) can be implemented in any way, usually on top of a
 * RDBMS, XML database, or other RDF store. KBoxes and their content are always identified by a URL, and the Knowledge Manager
 * must be taught how to interpret each URL protocol as a kbox plug-in and create the kbox from it.</p>
 * 
 * <p>An Ontology is a proper implementation of a KBox but typically not an efficient one. On a KBox, we expect support of indexing,
 * transparent permanent storage, distributed access, and configurable query paradigms that can be extended to support literals of
 * various kinds (e.g. GIS operations on shapes). Full reasoning is not an option as it is not realistic to expect to compute the
 * semantic closure of a large collection. On the other hand, many operations that are done using reasoners on an Abox can be 
 * replicated with properly constructed Constraints.</p>
 *
 * <p>Each kbox is identified by a URI which must be enough for the system to understand what class to use to access it. For this
 * reason, the KM has methods that can match a plug-in handler class to the protocol identifier of the URI. The rest of the URI contains
 * access and configuration information that is passed to the kbox for "handshaking" with the KM. The fragment part of the URI can be
 * used to identify an object within the KBox.</p> 
 * 
 * <p>Once installed in the KM, a KBox gets its own ID and a semantic type is all it takes to obtain one of its instances from the KM. The
 * query method of the KM uses all the installed KBoxes unless one is mentioned specifically, and merges results. A priority can be
 * specified to define which kboxes should be searched first when the search is bounded to a specified max number of results.</p>
 * 
 * @author Ferdinando Villa
 * @see KnowledgeManager
 * @see ISession
 */
public abstract interface IKBox extends IQueriable {
    
	/**
	 * String constants to identify accepted properties for the .kbox property file.
	 * MANDATORY: registered protocol of requested kbox.
	 */
	public final String KBOX_PROTOCOL_PROPERTY = "kbox.protocol";

	/**
	 * String constants to identify accepted properties for the .kbox property file.
	 * MANDATORY: URI with access details of kbox.
	 */
	public final String KBOX_URI_PROPERTY = "kbox.uri";

	/**
	 * String constants to identify accepted properties for the .kbox property file.
	 * OPTIONAL: schema id (comma-separated list), interpreted according to protocol.
	 */
	public final String KBOX_SCHEMA_PROPERTY = "kbox.schema";

	/**
	 * String constants to identify accepted properties for the .kbox property file.
	 * OPTIONAL: comma-separated list of ontologies which are tested for existence.
	 */
	public final String KBOX_ONTOLOGIES_PROPERTY = "kbox.ontologies";

	/**
	 * String constants to identify accepted properties for the .kbox property file.
	 * OPTIONAL: kbox will be wrapped in given wrapper class at creation if specified.
	 */
	public final String KBOX_WRAPPER_PROPERTY = "kbox.wrapper";

	/**
	 * String constants to identify accepted properties for the .kbox property file.
	 * OPTIONAL: kbox will store imported objects by their local names if true (default). Must ensure
	 * there is no duplication of names.
	 */
	public final String KBOX_LOCALNAMES_PROPERTY = "kbox.localnames";

	public final String KBOX_METADATA_TYPES_PROPERTY = "kbox.metadata.types";
	
	public final String KBOX_METADATA_SCHEMA_PROPERTY = "kbox.metadata.schema";

	/**
	 * Return the list definition of an object in the kbox. If the second parameter is not null, 
	 * it will contain a map between string IDs of objects and their full URL; these objects, when present,
	 * should be referenced and not defined in the result list. If
	 * a non-null map is passed, it is also expected that the ID/URL pairs of the objects that are NOT in the set 
	 * and are defined in the result list are added to the map.
	 *  
	 * @param id
	 * @param refTable
	 * @return
	 * @throws ThinklabException
	 */
	public abstract Polylist getObjectAsListFromID(String id, HashMap<String, String> refTable) throws ThinklabException;
	
	/**
	 * Retrieve the named object from the KBox and create a corresponding IInstance in the passed
	 * ISession.
	 * @param id the object name
	 * @param session a session to create the object into
	 * @return the instance, or null if it's not found.
	 * @throws ThinklabException if anything goes wrong
	 */
	public abstract IInstance getObjectFromID(String id, ISession session) throws ThinklabException;

	/**
	 * Retrieve the named object from the KBox and create a corresponding IInstance in the passed
	 * ISession. Use the passed table to store the IDs of objects created in previous invocations
	 * and avoid duplicates.
	 * 
	 * @param id the object name
	 * @param session a session to create the object into
	 * @param reftable a table to store references of created object
	 * @return the instance, or null if it's not found.
	 * @throws ThinklabException if anything goes wrong
	 */
	public abstract IInstance getObjectFromID(String id, ISession session, HashMap<String, String> refTable)
	throws ThinklabException;

	/**
	 * A Kbox should be capable of storing an object from its list description. If necessary, the
	 * object can be created in an internal session; however, this is the function that is most
	 * likely to be called a large number of consecutive times during imports, so it is important
	 * that strategies are adopted to keep memory size in control.
	 * 
	 * @param list
	 * @param session
	 * 	a session is usually necessary anyway, but this function should be prepared to be
	 *  passed a null.
	 * 
	 * @return the name of the stored instance in the KBox.

	 * @throws ThinklabException
	 */
	public abstract String storeObject(Polylist list, ISession session) throws ThinklabException;
	
	/**
	 * A Kbox should be capable of storing an object from its list description. If necessary, the
	 * object can be created in an internal session; however, this is the function that is most
	 * likely to be called a large number of consecutive times during imports, so it is important
	 * that strategies are adopted to keep memory size in control.
	 * 
	 * @param list
	 *
	 * @param references 
	 * 	a hashmap to catalog the stored instances, initially empty. The reference table should map
	 * 	the local name of the stored instance with the name of the same instance in the
	 * 	kbox (possibly the same name). If the passed instance local name is in the map,
	 *  no instance should be stored. This should apply recursively to all linked instances.
	 * 
	 * @return the name of the stored instance in the KBox.

	 * @throws ThinklabException
	 */
	public abstract String storeObject(Polylist list, ISession session, HashMap<String, String> refTable) throws ThinklabException;
	
	/**
	 * Store an object in the KBox. Return its ID in it, which may or may not be the same ID as the
	 * object's. Implementations should endeavor to store at least the URIs of the ontologies that are
	 * required to understand the object and make sure that those are in the KB (possibly in appropriate
	 * versions).
	 * @param object an IInstance to store
	 * @param session the current session
	 * @return the name of the stored instance in the KBox. If possible this should be the same as the
	 *         instance's, but if not a different one may be returned.
	 * @throws ThinklabException if anything goes wrong.
	 */
	public abstract String storeObject(IInstance object, ISession session) throws ThinklabException;

	/**
	 * Store an object in the KBox. Return its ID in it, which may or may not be the same ID as the
	 * object's. By passing the same hashset of strings across multiple calls, we ensure that objects
	 * referenced by subsequent instances are stored only once.
	 * @param object 
	 * 	an IInstance to store
	 * @param session 
	 * 	the current session
	 * @param references 
	 * 	a hashset to catalog the stored instances, initially empty. The reference table should map
	 * 	the local name of the stored instance with the name of the same instance in the
	 * 	kbox (possibly the same name). If the passed instance local name is in the map,
	 *  no instance should be stored. This should apply recursively to all linked instances.
	 * 
	 * @return the name of the stored instance in the KBox.
	 * 
	 * @throws ThinklabException if anything goes wrong.
	 */
	public abstract String storeObject(IInstance object, ISession session, HashMap<String, String> references) throws ThinklabException;

	/**
	 * Return an object that we can query to know what operators and such are supported by
	 * this kbox implementation.
	 * 
	 * @return
	 */
	public IKBoxCapabilities getKBoxCapabilities();

	/**
	 * If the kbox properties define a schema, return it. Otherwise return the 
	 * default schema which is (id, type, label, comment). The schema must be 
	 * valid (use IKboxManager.validateSchema).
	 * 
	 * @return
	 * @throws ThinklabException 
	 */
	public Polylist getMetadataSchema() throws ThinklabException;
	
}
