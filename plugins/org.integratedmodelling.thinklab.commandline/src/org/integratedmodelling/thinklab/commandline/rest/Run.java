package org.integratedmodelling.thinklab.commandline.rest;

import java.util.Collection;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.application.Application;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.commandline.CommandLine;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.integratedmodelling.thinklab.rest.RESTTask;
import org.integratedmodelling.thinklab.rest.ResultHolder;
import org.java.plugin.PluginLifecycleException;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * Authenticate creates a session for the calling service and returns a descriptor 
 * containing its URN, its privileges and other parameters. Sessions may be persistent
 * or ephemeral.
 * 
 * If the request already contains a session urn, simply verify its validity and 
 * return session status.
 * 
 * @author ferdinando.villa
 * 
 */
@RESTResourceHandler(id="run", description="run application",  arguments="application")
public class Run extends DefaultRESTHandler {

	class AppThread extends RESTTask {

		String app;
		IValue result = null;
		
		AppThread(String app) {
			this.app = app;
		}
		
		@Override
		protected void execute() throws Exception {
			result = Application.run(app);
		}

		@Override
		protected void cleanup() {
		}

		@Override
		public ResultHolder getResult() {
			ResultHolder rh = new ResultHolder();
			if (this.result != null)
				rh.setResult(result.toString());
			return rh;
		}
	}
	
	@Get
	public Representation run() {

		try {
			String app = this.getArgument("application");
			enqueue(new AppThread(app));
		} catch (Exception e) {
			fail(e);
		}
		
		return wrap();
	}
	
}
