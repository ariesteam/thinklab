package org.integratedmodelling.thinklab.rest;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.rest.resources.Authenticate;
import org.integratedmodelling.thinklab.rest.resources.Capabilities;
import org.integratedmodelling.thinklab.rest.resources.CheckWaiting;
import org.integratedmodelling.thinklab.rest.resources.Ping;
import org.integratedmodelling.thinklab.rest.resources.Receive;
import org.integratedmodelling.thinklab.rest.resources.Restart;
import org.integratedmodelling.thinklab.rest.resources.Send;
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
		router.attach("/", Ping.class);
		
		/*
		 * register authentication service, which gives you a session with privileges depending on
		 * user properties. Will use whatever authentication manager is installed.
		 */
		router.attach("/auth", Authenticate.class);

		/*
		 * register service to access status of enqueued task, returning the 
		 * result if the task has finished.
		 */
		router.attach("/check", CheckWaiting.class);

		/*
		 * receive uploaded files, return handle
		 */
		router.attach("/receive", Receive.class);
		
		/*
		 * send uploaded files to client using download handle
		 */
		router.attach("/send", Send.class);

		/*
		 * returns array of services if no further context, or 
		 * service description etc if context is given. Use wiki/html if html requested.
		 */
		router.attach("/capabilities", Capabilities.class, Template.MODE_STARTS_WITH);

		/*
		 * remote shutdown and restart
		 */
		router.attach("/shutdown", Restart.class);

		
		/*
		 * configure an entry point per installed command
		 */
		for (String path : RESTManager.get().getPaths()) {
			router.attach("/" + path, RESTManager.get().getResourceForPath(path), Template.MODE_STARTS_WITH);
			Thinklab.get().logger().info("REST command " + path + " registered");
		}
		
		return router;
	}

	
	
}
