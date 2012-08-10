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

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * 
 * Basic management operations, including shutdown, vacuum kbox and who knows
 * what else.
 * 
 * Commands:
 *              
 * 	<manager> cmd=shutdown
 *  <manager> cmd=vacuum [kbox=<kbox>]
 * 
 * @author ferdinando.villa
 *
 */
public class ManagerService extends DefaultRESTHandler {

	@Get
	public Representation service() {
		
		try {
			if (!checkPrivileges("user:Administrator"))
				return wrap();
				
			String cmd = getArgument("cmd");
			
			if (cmd.equals("shutdown")) {
				Thinklab.get().shutdown(null, 5, null);
			} else if (cmd.equals("vacuum")) {
				String kbox = getArgument("kbox", "thinklab");
				IKbox kb = Thinklab.get().requireKbox(kbox);
				kb.clear();
			}

		} catch (Exception e) {
			fail(e);
		}
		
		return wrap();
	}
	
}
