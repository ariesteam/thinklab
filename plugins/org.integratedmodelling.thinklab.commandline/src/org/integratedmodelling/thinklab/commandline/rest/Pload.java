package org.integratedmodelling.thinklab.commandline.rest;

import java.util.Collection;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.commandline.CommandLine;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.java.plugin.PluginLifecycleException;
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
public class Pload extends DefaultRESTHandler {

	@Get
	public Representation authenticate() {

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
