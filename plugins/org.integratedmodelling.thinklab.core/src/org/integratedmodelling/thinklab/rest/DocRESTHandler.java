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
