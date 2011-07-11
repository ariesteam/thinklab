package org.integratedmodelling.thinklab.http.commands;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.http.ThinklabHttpdPlugin;
import org.integratedmodelling.thinklab.http.application.ThinklabWebApplication;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
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
		optionNames="p,b",
		optionLongNames="port,block",
		optionDescriptions="port,join server thread (never return)",
		optionTypes="thinklab-core:Integer,owl:Nothing",
		optionArgumentLabels="port number, "
		)
public class Http implements ICommandHandler {
	
	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {

		String cmd = command.getArgumentAsString("cmd");
		Integer port = -1;
		boolean block = false;
		
		if (command.hasOption("port"))
			port = Integer.parseInt(command.getOptionAsString("port"));
		
		if (command.hasOption("block")) 
			block = true;
		
		if (cmd.equals("start")) {
			
			if (!command.hasArgument("application"))
				throw new ThinklabValidationException("application not specified");

			String app = command.getArgumentAsString("application");
			
			ThinklabHttpdPlugin.get().publishCommonResources();
			
			Server server = 
				ThinklabHttpdPlugin.get().startServer("0.0.0.0", port);
			ThinklabWebApplication webapp =
				ThinklabHttpdPlugin.get().publishApplication(app, server);
			
			session.getOutputStream().println(
					"application " + app + 
					" published at " +
					webapp.getApplicationUrl());
			
			if (block) {
				session.getOutputStream().println(
						"joining server thread - command will not return");
				try {
					webapp.getServer().join();
				} catch (InterruptedException e) {
					throw new ThinklabRuntimeException(e);
				}
			}
				
			
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
