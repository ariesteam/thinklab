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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * Return a resource known to the system.
 * 
 * Parameters:
 * 
 * ontology = String                  return the file representation of the given ontology
 * resource = String, plugin = String return the file resource accessible to the given plugin
 * 
 * @author ferdinando.villa
 *
 */
public class SendResourceService extends DefaultRESTHandler {

	@Get
	public Representation service() {
		
		Representation ret = null;
		
		FileInputStream input = null;
		try {
			
			File rfile = null;

			if (this.getArgument("ontology") != null) {
				
			} else if (this.getArgument("resource") != null && this.getArgument("plugin") != null) {
				
			} else {
				throw new ThinklabValidationException("wrong arguments to resource service");
			}
			
			try {
				input = new FileInputStream(rfile);
			} catch (FileNotFoundException e) {
				throw new ThinklabIOException(e);
			}

			ret = new InputRepresentation(input, MediaType.TEXT_PLAIN);
			ret.setCharacterSet(CharacterSet.UTF_8);
			input.close();
			
		} catch (Exception e) {
			fail(e);
		}

		return ret;
	}
	
}
