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
package org.integratedmodelling.thinklab.rest.resources;

import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabAuthenticationException;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.authentication.AuthenticationManager;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.integratedmodelling.thinklab.rest.RESTManager;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * Creates a session for the calling service and returns a descriptor 
 * containing the session URN. Must be called by clients before doing 
 * anything except server status requests, and almost all requests must
 * contain the session ID returned by this service.
 * 
 * If the request already contains a valid session URN, simply verify its validity and 
 * return session status.
 * 
 * @author ferdinando.villa
 * 
 */
public class AuthenticateService extends DefaultRESTHandler {

	@Get
	public Representation service() {
				
		try {
	
			String sess = this.getArgument("session");
			if (sess != null && !sess.equals("null")) {
				ISession s = RESTManager.get().getSession(sess);
				if (s != null) {
					info("session is already established");
				} else {
					fail("session is already active but does not exist");
				}
				return wrap();
			}
			
			String user = this.getArgument("user");
			String pass = this.getArgument("password");
			Properties uprop = null;
			
			if (user != null) {
			
				if (AuthenticationManager.get().authenticateUser(user, pass, null)) {
					uprop = AuthenticationManager.get().getUserProperties(user);			
					uprop.setProperty("authenticated-user", user);
				} else {
					throw new ThinklabAuthenticationException("failed to authenticate user " + user);
				}
			}
			
			ISession session = 
				RESTManager.get().createRESTSession(this.getArguments(), uprop);
//			session.getUserModel().setProperties(uprop);
		
			put("session", session.getSessionID());
		
		} catch (Exception e) {
			fail(e);
		}
		
		return wrap();
	}
	
}
