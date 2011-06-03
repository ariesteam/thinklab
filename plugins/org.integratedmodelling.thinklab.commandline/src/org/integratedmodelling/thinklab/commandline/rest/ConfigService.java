package org.integratedmodelling.thinklab.commandline.rest;

import java.util.Collection;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.commandline.CommandLine;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.java.plugin.Plugin;
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
@RESTResourceHandler(id="config", description="load plugin",  
					 arguments="plugin,variable,value",
					 options="keep")
public class ConfigService extends DefaultRESTHandler {

	@Get
	public Representation service() throws ThinklabException {

		if (!checkPrivileges("user:Administrator"))
			return wrap();
		
		try {
			String plugin 	= this.getArgument("plugin");
			plugin = Thinklab.resolvePluginName(plugin, true);
			Plugin zio = CommandLine.get().getManager().getPlugin(plugin);
			if (zio instanceof ThinklabPlugin) {
				
				String var = this.getArgument("variable");
				String val = this.getArgument("value");
				String kep = this.getArgument("keep");
				
				/*
				 * change value 
				 */
				String previous = ((ThinklabPlugin)zio).getProperties().getProperty(var);
				((ThinklabPlugin)zio).getProperties().setProperty(var, val);
				
				/*
				 * persist if requested
				 */
				if (kep != null && kep.equals("true")) {
					((ThinklabPlugin)zio).persistProperty(var, val);
				}
				
				if (previous != null)
					put("previous", previous);
			}
		} catch (Exception e) {
			fail(e);
		}
		
		return wrap();
	}
	
}
