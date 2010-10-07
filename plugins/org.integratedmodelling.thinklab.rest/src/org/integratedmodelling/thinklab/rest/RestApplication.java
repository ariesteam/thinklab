package org.integratedmodelling.thinklab.rest;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

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
		 * TODO configure an entry point per installed command
		 * down here is just a test
		 */
		router.attach("/cmd", JSONCommandResource.class);
		
		return router;
	}

	
	
}
