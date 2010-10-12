package org.integratedmodelling.thinklab.rest;

import org.integratedmodelling.thinklab.rest.resources.Authenticate;
import org.integratedmodelling.thinklab.rest.resources.Capabilities;
import org.integratedmodelling.thinklab.rest.resources.JSONCommandResource;
import org.integratedmodelling.thinklab.rest.resources.Status;
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
		 * root entry point is a "ping" service that returns Thinklab and server stats.
		 */
		router.attach("/", Status.class);
		
		/*
		 * register authentication service, which gives you a session with privileges depending on
		 * user properties. Will use whatever authentication manager is installed.
		 */
		router.attach("/auth", Authenticate.class);

		/*
		 * register "capabilities" service - returns array of services if no further context, or 
		 * service description etc if context is given. Use wiki/html if html requested.
		 */
		router.attach("/capabilities", Capabilities.class, Template.MODE_STARTS_WITH);
		
		/**
		 * send service exposes all registered Thinklab commands as REST services.
		 */
		router.attach("/send", JSONCommandResource.class, Template.MODE_STARTS_WITH);

		/*
		 * TODO
		 * configure an entry point per installed command
		 */
		for (String path : RESTManager.get().getPaths()) {
			router.attach(path, RESTManager.get().getResourceForPath(path), Template.MODE_STARTS_WITH);
		}
		
		return router;
	}

	
	
}
