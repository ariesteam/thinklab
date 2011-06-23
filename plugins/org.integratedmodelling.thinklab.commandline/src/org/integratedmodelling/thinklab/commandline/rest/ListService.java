package org.integratedmodelling.thinklab.commandline.rest;

import java.util.Collection;

import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;
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
@RESTResourceHandler(id="list", description="list service",  arguments="arg")
public class ListService extends DefaultRESTHandler {

	@Get
	public Representation service() throws ThinklabException {

		String arg 	= this.getArgument("arg");
		boolean isItem = false;
		
		IListingProvider prov = 
				CommandManager.get().getListingProvider(arg);
		
		if (prov == null) {
			prov = CommandManager.get().getItemListingProvider(arg);
			isItem = true;
		}
		
		// communicate if this was a single item display, just in case.
		this.put("is-item", isItem ? "true" : "false");
		
		if (prov != null) {
			
			String theItem = null;
			
			for (String key : getArguments().keySet()) {
				String val = getArgument(key);
				
				if (key.equals("match") && isItem) {
					theItem = val;
				} else if (!key.equals("arg")) {
					prov.notifyParameter(key, val);
				}
			}
			
			Collection<?> ps = 
				isItem ? 
					prov.getSpecificListing(theItem) : 
					prov.getListing();
					
			setResult(ps.toArray(new Object[ps.size()]));
			
		} else {
			fail("list: don't know how to list " + arg);
		}
		
		return wrap();
	}
	
}
