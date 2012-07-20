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

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.rest.resources.AuthenticateService;
import org.integratedmodelling.thinklab.rest.resources.CapabilitiesService;
import org.integratedmodelling.thinklab.rest.resources.CheckWaitingService;
import org.integratedmodelling.thinklab.rest.resources.ExecuteStatementService;
import org.integratedmodelling.thinklab.rest.resources.ExportKnowledgeService;
import org.integratedmodelling.thinklab.rest.resources.FileReceiveService;
import org.integratedmodelling.thinklab.rest.resources.FileSendService;
import org.integratedmodelling.thinklab.rest.resources.PingService;
import org.integratedmodelling.thinklab.rest.resources.ProjectService;
import org.integratedmodelling.thinklab.rest.resources.SendResourceService;
import org.integratedmodelling.thinklab.rest.resources.ShutdownService;
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
		 * resource send
		 */
		router.attach("/resource", SendResourceService.class);
		
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
		 * knowledge export for client initialization
		 */
		router.attach("/export-knowledge", ExportKnowledgeService.class);

		/*
		 * exec model statement
		 */
		router.attach("/execute-statement", ExecuteStatementService.class);

		
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
