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

	public abstract ISession getCurrentSession();

}
