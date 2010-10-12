package org.integratedmodelling.thinklab.rest.resources;

import java.util.Properties;

import org.integratedmodelling.thinklab.authentication.AuthenticationManager;
import org.integratedmodelling.thinklab.exception.ThinklabAuthenticationException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.integratedmodelling.thinklab.rest.RESTManager;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;

/**
 * Authenticate creates a session for the calling service and returns a descriptor 
 * containing its URN, its privileges and other parameters. Sessions may be persistent
 * or ephemeral.
 * 
 * If the request already contains a session urn, simply verify its validity and 
 * return session status.
 * 
 * @author ferdinando.villa
 * 
 */
public class Authenticate extends DefaultRESTHandler {

	@Post
	public Representation authenticate() throws ThinklabException {
				
		/*
		 * TODO
		 */
		String user = this.getArgument("user");
		String pass = this.getArgument("password");
		Properties uprop = null;
		
		if (AuthenticationManager.get().authenticateUser(user, pass, null)) {
			uprop = AuthenticationManager.get().getUserProperties(user);			
		} else {
			throw new ThinklabAuthenticationException("failed to authenticate user " + user);
		}
		
		ISession session = RESTManager.get().createRESTSession(this.getArguments(), uprop);
		session.getUserModel().setProperties(uprop);
		
		put("session", session.getSessionID());
		
		return wrap();
	}
	
}
