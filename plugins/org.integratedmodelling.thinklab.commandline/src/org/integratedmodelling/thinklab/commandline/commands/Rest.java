package org.integratedmodelling.thinklab.commandline.commands;

import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.rest.RESTManager;

/**
 * Start and stop the REST service.
 * 
 * @author ferdinando.villa
 *
 */
@ThinklabCommand(
		name="rest",
		argumentNames="cmd",
		argumentTypes="thinklab-core:Text",
		argumentDescriptions="command (start|stop|restart|status)",
		optionNames="p,server",
		optionLongNames="port,server",
		optionDescriptions="port,do not return",
		optionTypes="thinklab-core:Integer,owl:Nothing",
		optionArgumentLabels="port, "
		)
public class Rest implements ICommandHandler {
	
	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {

		String cmd = command.getArgumentAsString("cmd");
		String ept = System.getenv("THINKLAB_REST_PORT");
		if (ept == null)
			ept = "8182";
		
		Integer port = Integer.parseInt(ept);
		
		if (command.hasOption("port"))
			port = Integer.parseInt(command.getOptionAsString("port"));
		
		if (cmd.equals("start")) {
			
			RESTManager.get().start(port);
			
			if (command.hasOption("server")) {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
			
		} else if (cmd.equals("stop")) {
			
			RESTManager.get().stop(port);			

		}  else if (cmd.equals("status")) {
		
			// TODO
			
		}  else if (cmd.equals("restart")) {

			RESTManager.get().stop(port);			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// come on
			}
			RESTManager.get().start(port);

		} 
		
		return null;
	}

}
