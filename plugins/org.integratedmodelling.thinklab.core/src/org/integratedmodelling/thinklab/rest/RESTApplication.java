package org.integratedmodelling.thinklab.rest;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.rest.resources.AuthenticateService;
import org.integratedmodelling.thinklab.rest.resources.CapabilitiesService;
import org.integratedmodelling.thinklab.rest.resources.CheckWaitingService;
import org.integratedmodelling.thinklab.rest.resources.PingService;
import org.integratedmodelling.thinklab.rest.resources.FileReceiveService;
import org.integratedmodelling.thinklab.rest.resources.ProjectService;
import org.integratedmodelling.thinklab.rest.resources.ShutdownService;
import org.integratedmodelling.thinklab.rest.resources.FileSendService;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

/**
 * The REST application that exposes all thinklab commands through the REST API
 * 
 * @author ferdinando.villa
 */
public class RESTApplication extends Application {

	@Override
	public Restlet createInboundRoot() {

		Router router = new Router(getContext());
		
		/*
		 * root entry point is a "ping" service that returns Thinklab and server stats.
		 */
		router.attach("/", PingService.class);
		
		/*
		 * register authentication service, which gives you a session with privileges depending on
		 * user properties. Will use whatever authentication manager is installed.
		 */
		router.attach("/auth", AuthenticateService.class);

		/*
		 * register service to access status of enqueued task, returning the 
		 * result if the task has finished.
		 */
		router.attach("/check", CheckWaitingService.class);

		/*
		 * receive uploaded files, return handle
		 */
		router.attach("/receive", FileReceiveService.class);
		
		/*
		 * send uploaded files to client using download handle
		 */
		router.attach("/send", FileSendService.class);

		/*
		 * returns array of services if no further context, or 
		 * service description etc if context is given. Use wiki/html if html requested.
		 */
		router.attach("/capabilities", CapabilitiesService.class, Template.MODE_STARTS_WITH);

		/*
		 * remote shutdown and restart
		 */
		router.attach("/shutdown", ShutdownService.class);
		
		/*
		 * remote project management
		 */
		router.attach("/project", ProjectService.class);

		
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
