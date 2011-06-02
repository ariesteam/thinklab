package org.integratedmodelling.thinklab.rest.resources;

import java.util.Properties;

import org.integratedmodelling.thinklab.authentication.AuthenticationManager;
import org.integratedmodelling.thinklab.exception.ThinklabAuthenticationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
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
				} else {
					throw new ThinklabAuthenticationException("failed to authenticate user " + user);
				}
			}
			
			ISession session = RESTManager.get().createRESTSession(this.getArguments(), uprop);
			session.getUserModel().setProperties(uprop);
		
			put("session", session.getSessionID());
		
		} catch (Exception e) {
			fail(e);
		}
		
		return wrap();
	}
	
}
