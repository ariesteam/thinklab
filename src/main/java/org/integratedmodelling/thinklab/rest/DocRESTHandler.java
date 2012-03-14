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
package org.integratedmodelling.thinklab.rest;

import org.restlet.resource.ResourceException;

/**
 * Specialized REST handler for services that return documentation. Able to use Wiki
 * syntax to serve formatted text, xml, html or pdf according to requested extension.
 *
 * Should have simple methods to add text inline (as wiki text) and to retrieve file resources
 * or expand templates stored with plugin.
 * 
 * @author Ferdinando
 *
 */
public abstract class DocRESTHandler extends DefaultRESTHandler {

	
	
	
	@Override
	protected void doInit() throws ResourceException {
	}

	@Override
	protected void doRelease() throws ResourceException {
	}
	
}
