/**
 * ISession.java
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
package org.integratedmodelling.thinklab.interfaces.applications;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Collection;
import java.util.Properties;

import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;


/**
 * A Session is a temporary concept space that contains all instances that are
 * created during a user session. The ontology metadata also contain details
 * about session creation. Typically, all operations on sessions
 * are synchronized. 
 * 
 * @author Ferdinando Villa
 * 
 */
public interface ISession {
	
	// standard variable names for notification levels
	public static final String DEBUG = "session.debug";
	public static final String INFO = "session.info";
	public static final String COMMAND = "session.command";
	
	
	/**
	 * Each session has a unique ID assigned by the Knowledge manager. 
	 * @return the session's ID.
	 */
	public abstract String getSessionID();
	
	/**
	 * A session must have properties that users and plugins can set. This method must
	 * return a valid properties object.
	 * @return the session's properties.
	 */
	public abstract Properties getProperties();
	
	/**
	 * The knowledge manager may pass one or more session listeners to a newly created session using
	 * this function. The function has the option of throwing an exception if it does not want to deal
	 * with listeners, but should not accept them and then ignore them. If listeners are accepted,
	 * the session commits to calling the objectCreated() and objectDeleted() on FIRST-CLASS OBJECTS ONLY (those
	 * that are not secondary to other objects when loaded from sources or created by the API) appropriately. 
	 * The sessionCreated and sessionDeleted methods are called by the knowledge manager, so the session
	 * doesn't need to worry about them.
	 * 
	 * @param listener
	 * @throws ThinklabException
	 */
	public abstract void addListener(IThinklabSessionListener listener) throws ThinklabException;
	
    /**
     * <p>Write all current contents of ontology on passed ontology file.</p>
     * <p><b>NOTE:</b> this will remove all non-validated instances, rendering all relative objects meaningless and their use
     * dangerous. This may change.</p>
     * @param file
     * @throws ThinklabException
     */
    public abstract void write(String file) throws ThinklabException;
    
	
	/**
	 * Sessions must be capable of creating temporary concepts from a list specification. These
	 * concepts can only restrict "global" ones by specifying owl:hasValue restrictions. The list
	 * syntax is very similar to the one used for instances.
	 * 
	 * @param list 
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IConcept createConcept(Polylist list) throws ThinklabException;


	/**
	 * Create object of passed type. Object is all yours to define. 
	 * 
	 * @param name A name for the new object. Must be unique or exception is thrown. 
	 * @param parent the instance parent concept.
	 * @return a new unvalidated IInstance. 
	 * @throws ThinklabException  if anything wrong.
	 * 
	 * FIXME must return a garbage collected instance, if we ever manage to implement it.
	 * 	 */
	public abstract IInstance createObject(String name, IConcept parent) throws ThinklabException;
	
	/**
	 * Create object of passed type. Object is all yours to define. The object must be validated
	 * using validate() before it can be used. Name of object is automatically assigned.
	 * @param concept a string representing the semantic type of the parent concept.
	 * @return a new unvalidated IInstance
	 * @throws ThinklabException  if anything wrong.
	 * 
	 * 	 * FIXME must return a garbage collected instance, if we ever manage to implement it.
	 */
	public abstract IInstance createObject(String concept) throws ThinklabException;

	/**
	 * Create object of passed type. Object is all yours to define. The object must be validated
	 * using validate() before it can be used. Name of object is automatically assigned.
	 * @param concept the actual semantic type of the object's parent concept
	 * @return a new unvalidated IInstance
	 * @throws ThinklabException  if anything wrong.
	 * 
	 * 	 * FIXME must return a garbage collected instance, if we ever manage to implement it.
	 */
	public abstract IInstance createObject(SemanticType concept) throws ThinklabException;

	/**
	 * Create object from list definition. Can be used to copy instances from a session to another or from the
	 * KB. Careful with nested instances though. Instance shoud be completely defined by list, so it is
	 * validated before it's returned.
	 * @param name Unique name for the new object. Use getUniqueObjectName() if no clue. The name is not
	 * 			   a property of the object, so it can't be passed in the list (this may change).
	 * @param definition the list defining a new instance.
	 * @return a new <b>validated</b> Instance.
	 * @throws ThinklabException if anything wrong.
	 * 
	 *  FIXME must return a garbage collected instance, if we ever manage to implement it.
	 */
	public abstract IInstance createObject(String name, Polylist definition) throws ThinklabException;
	
	/**
	 * Create instance, using made up name. Identical to createObject(String, Polylist) otherwise.
	 * @param polylist
	 * @param properties TODO
	 * @param properties for defaults and such, passed to implementation.initialize()
	 * @return a new validated instance.
	 * 
	 *  FIXME must return a garbage collected instance, if we ever manage to implement it.
	 */
	public abstract IInstance createObject(Polylist polylist) throws ThinklabException;
	
	/**
	 * Read in objects from the given URL. What can be read depends on the implementation, but it should
	 * support OWL and OPAL at least.
	 * @param url a URL to read from
	 * @return a collection of the main-level IInstances (those that are defined in the 
	 *         main level, i.e. are not "linked" to others).
	 * @throws ThinklabException if anything goes wrong
	 * 
	 *  FIXME must return garbage collected instances, if we ever manage to implement it.
	 */
	public abstract Collection<IInstance> loadObjects(URL url) throws ThinklabException;


	/**
	 * Delete the named object.
	 * @param name name of object
	 * @throws ThinklabException 
	 */
	public abstract void deleteObject(String name) throws ThinklabException;

	/**
	 * Retrieve the named object or null if no such object exists in session.
	 * @param name an object name. Note: this is just a name, not a semantic type.
	 * @return the object or null if not found
	 */
	public abstract IInstance retrieveObject(String name);
	
	/**
	 * Retrieve the named object, throwing an exception if not found.
	 * @param name an object name. Note: this is just a name, not a semantic type.
	 * @return the object
	 * @throws ThinklabResourceNotFoundException if object isn't in session
	 */
	public abstract IInstance requireObject(String name) throws ThinklabResourceNotFoundException;
	
    /**
     * Create object as copy of other object from any session. Object ID will usually change.
     * @param ii an instance to copy
     * @return the new instance.
     * @throws ThinklabException if anything goes wrong
     * 
     * FIXME see if we really need this one. IInstance.clone(session) should be plenty.
     */
    public abstract IInstance createObject(IInstance ii) throws ThinklabException;


	/**
	 * Return all the listeners registered with the session. Can return null if no listeners are registered or
	 * allowed.
	 * 
	 * @return a collection of all listeners added to the session using addListener(), or null.
	 */
	public abstract Collection<IThinklabSessionListener> getListeners();

	/**
	 * KBox retrieval is moved to the session level because kboxes may be local to sessions.
	 * 
	 * @param string
	 * @return
	 * @throws ThinklabException 
	 * 
	 * FIXME check rationale 
	 */
	public abstract IKBox retrieveKBox(String string) throws ThinklabException;

	/**
	 * 
	 * @param string
	 * @return
	 * @throws ThinklabResourceNotFoundException
	 * 
	 * FIXME check rationale
	 */
	public abstract IKBox requireKBox(String string) throws ThinklabException;

	/**
	 * 
	 * @return
	 * 
	 * FIXME check rationale
	 */
	public abstract Collection<String> getLocalKBoxes();
	
	/**
	 * Return the user model for the session. If the session is not interactive, the user model
	 * may be null.
	 * 
	 * @return
	 */
	public abstract IUserModel getUserModel();

	/**
	 * Get the input stream if the user model has it, or return null.
	 * 
	 * @return
	 */
	public abstract InputStream getInputStream(); 
	
	/**
	 * get the output stream if the user model defines one, otherwise return null.
	 */
	public PrintStream getOutputStream();
	

	/**
	 * Print a string wherever is appropriate, or ignore if not appropriate. Do not
	 * raise errors.
	 * @param s
	 */
	public void print(String s);
	
	/**
	 * 
	 * @param varname
	 * @param value
	 */
	public abstract void pushVariable(String varname, Object value);
	
	/**
	 * 
	 * @param varname
	 * @return
	 * @throws ThinklabInappropriateOperationException 
	 */
	public abstract Object popVariable(String varname) throws ThinklabInappropriateOperationException;
	
	/**
	 * 
	 * @param varname
	 * @return
	 */
	public abstract Object getVariable(String varname);

	/**
	 * Return a unique directory name for the session's workspace, in case applications request it. The directory 
	 * should be a simple identifier.
	 * 
	 * @return
	 */
	public abstract String getSessionWorkspace();

}
