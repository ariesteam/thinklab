package org.integratedmodelling.thinklab.rest.commands;

import java.util.HashMap;

import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.rest.RestApplication;
import org.restlet.Component;
import org.restlet.data.Protocol;

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
		optionNames="p",
		optionLongNames="port",
		optionDescriptions="port",
		optionTypes="thinklab-core:Integer"
		)
public class Rest implements ICommandHandler {

	HashMap<Integer, Component> _components = 
		new HashMap<Integer, Component>(); 
	
	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {

		String cmd = command.getArgumentAsString("cmd");
		Integer port = 8182;
		
		if (command.hasOption("port"))
			port = Integer.parseInt(command.getOptionAsString("port"));
		
		if (cmd.equals("start")) {
			
			if (_components.containsKey(port))
				throw new ThinklabInappropriateOperationException(
						"a REST service is already running on port " + port);
			
			Component component = new Component();
			// TODO configure port
			component.getServers().add(Protocol.HTTP, port);
			component.getDefaultHost().attach("/tl", new RestApplication());
			try {
				component.start();
			} catch (Exception e) {
				throw new ThinklabInternalErrorException(e);
			}
			_components.put(port, component);
			
		} else if (cmd.equals("stop")) {
			
			Component component = _components.get(port);
			if (component == null) 
				throw new ThinklabInappropriateOperationException(
						"no REST service running on port " + port);
			try {
				component.stop();
			} catch (Exception e) {
				throw new ThinklabInternalErrorException(e);
			}
			_components.remove(port);
			
		}  else if (cmd.equals("status")) {
			
		}  else if (cmd.equals("restart")) {
			
		} 
		
		return null;
	}

}
