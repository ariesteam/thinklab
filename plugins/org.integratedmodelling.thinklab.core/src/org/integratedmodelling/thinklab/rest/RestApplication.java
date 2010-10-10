package org.integratedmodelling.thinklab.rest;

import org.integratedmodelling.thinklab.rest.resources.JSONCommandResource;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

/**
 * The REST application that exposes all thinklab commands through the REST API
 * 
 * @author ferdinando.villa
 */
public class RestApplication extends Application {

	@Override
	public Restlet createInboundRoot() {

		Router router = new Router(getContext());
		
		/*
		 * TODO
		 * root entry point should be linked to a "ping" service that returns stats on the
		 * service.
		 */
		
		/*
		 * TODO
		 * register "capabilities" service - returns array of services if no further context, or 
		 * service description etc if context is given. Use wiki/html if html requested.
		 */
		
		/*
		 * configure an entry point per installed command
		 * down here is just a test
		 */
		router.attach("/cmd", JSONCommandResource.class, Template.MODE_STARTS_WITH);
		
		return router;
	}

	
	
}
