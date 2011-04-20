package org.integratedmodelling.thinklab.http.commands;

import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.http.ThinklabHttpdPlugin;
import org.integratedmodelling.thinklab.http.application.ThinklabWebApplication;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.mortbay.jetty.Server;

/**
 * Start and stop the HTTP service formerly known as Thinkcap. Should be 
 * modified to take an application plugin as a mandatory argument, so that
 * only one application is served at a time.
 * 
 * @author ferdinando.villa
 *
 */
@ThinklabCommand(
		name="http",
		argumentNames="cmd",
		argumentTypes="thinklab-core:Text",
		argumentDescriptions="command (start|stop|restart|status)",
		optionalArgumentNames="application",
		optionalArgumentTypes="thinklab-core:Text",
		optionalArgumentDescriptions="application to start",
		optionNames="p",
		optionLongNames="port",
		optionDescriptions="port",
		optionTypes="thinklab-core:Integer"
		)
public class Http implements ICommandHandler {
	
	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {

		String cmd = command.getArgumentAsString("cmd");
		Integer port = -1;
		
		if (command.hasOption("port"))
			port = Integer.parseInt(command.getOptionAsString("port"));
		
		if (cmd.equals("start")) {
			
			if (!command.hasArgument("application"))
				throw new ThinklabValidationException("application not specified");

			String app = command.getArgumentAsString("application");
			
			ThinklabHttpdPlugin.get().publishCommonResources();
			
			Server server = 
				ThinklabHttpdPlugin.get().startServer("localhost", port);
			ThinklabWebApplication webapp =
				ThinklabHttpdPlugin.get().publishApplication(app, server);
			
			session.getOutputStream().println(
					"application " + app + 
					" published at " +
					webapp.getApplicationUrl());
			
			
		} else if (cmd.equals("stop")) {
			
			if (port == -1) {
				port = 8060;
			}
			ThinklabHttpdPlugin.get().stopServer(port);
			
		}  else if (cmd.equals("status")) {
		
			// TODO
			
		}  else if (cmd.equals("restart")) {

			if (port == -1) {
				port = 8060;
			}

			ThinklabHttpdPlugin.get().startServer("localhost", port);
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// come on
			}
			ThinklabHttpdPlugin.get().stopServer(port);

		} 
		
		return null;
	}

}
