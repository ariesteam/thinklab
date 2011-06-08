/**
 * IThinklabSessionListener.java
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

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

/**
 * Interface that must be implemented by a session listener, one instance of which is paired to any new
 * session and notified of changes in it. Note: implementations must have a no-arguments constructor, as
 * instances are created directly by the knowledge manager.
 * 
 * Listener classes must be registered with the knowledge manager before the run() function is called. The full
 * class name is passed to the KM in registerSessionListener(). More than one listener classes may be registered;
 * listeners will be created in the order of registration.
 * 
 * @author Ferdinando Villa
 *
 */
public interface IThinklabSessionListener {

	/**
	 * Notifier for the creation of a session, called by the KM immediately after the interface has created
	 * a session. Can be used, e.g. to insert session objects in plugins.
	 * @param session
	 */
	public abstract void sessionCreated(ISession session)  throws ThinklabException;
	
	/**
	 * Notifier for the deletion of a session. NOTE: not linked to the code yet. Implementation will not be
	 * used.
	 * @param session
	 * @throws ThinklabException 
	 */
	public abstract void sessionDeleted(ISession session) throws ThinklabException;
	
	/**
	 * Notifier for the creation of an object. Typically only called on main objects created explicitly by
	 * user actions or direct API calls, and not for those linked to them and created during recursive 
	 * calls. When this is called, the object has been fully defined and validated.
	 * @param object
	 */
	public abstract void objectCreated(IInstance object)  throws ThinklabException; 
	
	/**
	 * Notifier for the destruction of an object. NOTE: no functions in Thinklab currently destroy objects, 
	 * and session deletion is currently not guaranteed to destroy objects. Implementation will not be 
	 * used at this time.
	 * @param object
	 */
	public abstract void objectDeleted(IInstance object)  throws ThinklabException;
	
}
