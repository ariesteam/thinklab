package org.integratedmodelling.thinklab.commandline.rest;

import java.util.Collection;
import java.util.Properties;

import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.integratedmodelling.thinklab.rest.RESTManager;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
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
@RESTResourceHandler(path="/list", description="list service")
public class List extends DefaultRESTHandler {

	@Get
	public Representation authenticate() throws ThinklabException {

		String arg 	= this.getArgument("arg");
		
		IListingProvider prov = 
				CommandManager.get().getListingProvider(arg);
		
		if (prov != null) {
			Collection<String> ps = prov.getListing();
			setResult(ps.toArray(new String[ps.size()]));
		}
		
		return wrap();
	}
	
}
