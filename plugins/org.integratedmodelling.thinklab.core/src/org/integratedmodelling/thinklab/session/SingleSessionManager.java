/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.session;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.runtime.ISession;
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
