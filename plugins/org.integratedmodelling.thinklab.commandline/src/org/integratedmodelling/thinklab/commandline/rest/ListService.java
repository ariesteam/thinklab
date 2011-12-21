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
