package org.integratedmodelling.thinklab.commandline.rest;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.authentication.AuthenticationManager;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

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
@RESTResourceHandler(id="user", description="manage users",  arguments="arg")
public class UserService extends DefaultRESTHandler {

	@Get
	public Representation service() throws ThinklabException {

		if (!checkPrivileges("user:Administrator"))
			return wrap();

		String cmd 	= this.getArgument("arg");

		AuthenticationManager auth = AuthenticationManager.get();
		
		try {

			if (cmd.equals("add")) {
		
				String user = requireArgument("user");
				String pass = requireArgument("password");
				
				auth.createUser(user, pass);
				
			} else if (cmd.equals("remove")) {

				String user = requireArgument("user");
				auth.deleteUser(user);
				
			} else if (cmd.equals("password")) {

				String user = requireArgument("user");
				String pass = requireArgument("password");
				
				auth.setUserPassword(user, pass);
				
			} else if (cmd.equals("set")) {
				
				String user = requireArgument("user");
				String prop = requireArgument("property");
				String valu = requireArgument("value");
				
				auth.setUserProperty(user, prop, valu);
				
			} else if (cmd.equals("unset")) {

				String user = requireArgument("user");
				String prop = requireArgument("property");

				auth.setUserProperty(user, prop, null);
			} 
		
		} catch (Exception e) {
			fail(e);
		}
		
		return wrap();
	}
	
}
