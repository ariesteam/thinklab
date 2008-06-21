/**
 * IKnowledgeInterface.java
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

import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.exception.ThinklabException;


/**
 * @author Ferdinando Villa
 * @author Ioannis N. Athanasiadis
 */
public interface ISessionManager {

	/**
	 * TODO
	 * @param declaration
	 * @throws ThinklabException
	 */
    public abstract void registerCommand(CommandDeclaration declaration) throws ThinklabException;

    /**
     * Import user and/or system preferences, if any, into java Preferences for packages. Called at KM initialization before
     * everything else. Should not throw exceptions. May or may not be
     * defined. From this point on, all code should only use Preferences for configuration. Program is given
     * a chance to save prefs at end when savePreferences() is called.
     *
     */
    public abstract void importPreferences();
    
    /** called at KM shutdown. */
    public abstract void savePreferences();
    
    /**
     * Start the interface. Called as last thing by KM.initialize(). Some interfaces will only provide a stub.
     */
	public abstract void start();

	/**
	 * Interfaces should support a notion of a "current session" where stuff can be created and
	 * loaded. For some (servers), this can be complex and related to the internal environment, so
	 * this function will probably change to have parameters - either a generic Object or some 
	 * RuntimeContext interface.
	 * 
	 * @return the current session. It should not return null - if no current session is supported,
	 * 		   calling this one is a design error, so it should throw an exception.
	 * @throws ThinklabException if no session can exist (or other runtime error)
	 */
	public abstract ISession getCurrentSession() throws ThinklabException;	
	
	/**
	 * Create and return a new Session that suits the runtime context. Many interfaces will have enough with
	 * the standard Session, others may need more sophistication and/or metadata. A Session is a good place to
	 * hold user info, preferences, parameters, and should be coupled to any other session abstraction that
	 * the particular runtime environment provides.
	 * @return a new Session, or fail with an exception. null should never be returned.
	 */
	public abstract ISession createNewSession() throws ThinklabException;

	/**
	 * A callback that is invoked just before a session is deleted. Use as you please.
	 * @param session the session that is going to be deleted.
	 */
	public abstract void notifySessionDeletion(ISession session);

}
