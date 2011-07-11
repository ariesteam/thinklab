/**
 * CLInterface.java
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
package org.integratedmodelling.thinklab.session;

import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISessionManager;
import org.integratedmodelling.thinklab.owlapi.Session;


/**
 * @author Ferdinando Villa
 * @author Ioannis N. Athanasiadis
 */
public class SingleSessionManager implements ISessionManager {
	
	public ISession session;

	public ISession createNewSession() throws ThinklabException {
		return new Session();
	}

	public void notifySessionDeletion(ISession session) {
		// TODO Auto-generated method stub		
	}

	
	public void clear() throws ThinklabException {
		/* just delete it, make it a new one */
		session = new Session();
	}

	@Override
	public ISession getCurrentSession() {
		return session;
	}
}
