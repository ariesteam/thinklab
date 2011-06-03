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
public class RunService extends DefaultRESTHandler {

	class AppThread extends Thread implements RESTTask {

		String app;
		IValue result = null;
		boolean _done = false;
		
		AppThread(String app) {
			this.app = app;
		}
		
		@Override
		public void run() {
			try {
				result = Application.run(app);
			} catch (ThinklabException e) {
				fail(e);
			} finally {
				_done = true;
			}
		}

		@Override
		public Representation getResult() {
			if (this.result != null)
				setResult(result.toString());
			return wrap();
		}

		@Override
		public boolean isFinished() {
			return _done;
		}
	}
	
	@Get
	public Representation service() throws ThinklabException {

		if (!checkPrivileges("user:Administrator"))
			return wrap();
		
		try {
			String app = this.getArgument("application");
			return enqueue(new AppThread(app));
		} catch (Exception e) {
			fail(e);
		}
		
		return wrap();
	}
	
}
