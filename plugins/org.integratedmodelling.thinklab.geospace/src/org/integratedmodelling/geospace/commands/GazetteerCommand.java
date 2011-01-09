package org.integratedmodelling.geospace.commands;

import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.InteractiveCommandHandler;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * Driver for everything that can be done with storylines. Subcommands are
 * 
 * 	create <namespace> [model context ...]
 *  update <namespace> [model context ...]
 *  run    {-o <outfile>|-v|-s <scenario>} <namespace> [<context>]
 *  test   {-o <outfile>|-v|-s <scenario>|-r <report>|-e <email>} <namespace> [<context>]
 *  copy   <namespace-from> <namespace-to> [model context ...]
 *  
 * @author Ferdinando
 *
 */
@ThinklabCommand(name="gazetteer",
		argumentNames="action",
		argumentTypes="thinklab-core:Text",
		argumentDescriptions="action {create|delete|reset|import}",
		optionalArgumentNames="arg0,arg1,arg2",
		optionalArgumentDefaultValues="_,_,_",
		optionalArgumentTypes="thinklab-core:Text,thinklab-core:Text,thinklab-core:Text",
		optionalArgumentDescriptions=" , , ")
public class GazetteerCommand extends InteractiveCommandHandler {

	@Override
	protected IValue doInteractive(Command command, ISession session)
			throws ThinklabException {

		String action = command.getArgumentAsString("action");
	
		if (action.equals("create")) {
			
		} else if (action.equals("delete")) {
			
		} else if (action.equals("reset")) {
			
		} else if (action.equals("import")) {
			
		}
	
		return null;
	
	}

}
