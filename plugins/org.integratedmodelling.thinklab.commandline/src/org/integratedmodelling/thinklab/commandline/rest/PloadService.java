package org.integratedmodelling.thinklab.commandline.rest;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.commandline.CommandLine;
import org.integratedmodelling.thinklab.exception.ThinklabException;
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
@RESTResourceHandler(id="pload", description="load plugin",  arguments="arg")
public class PloadService extends DefaultRESTHandler {

	@Get
	public Representation service() throws ThinklabException {

		if (!checkPrivileges("user:Administrator"))
			return wrap();
		
		try {
			String plugin 	= this.getArgument("arg");
			plugin = Thinklab.resolvePluginName(plugin, true);
			CommandLine.get().getManager().activatePlugin(plugin);
		} catch (Exception e) {
			fail(e);
		}
		
		return wrap();
	}
	
}
