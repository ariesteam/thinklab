package org.integratedmodelling.thinklab.interfaces.commands;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;

public interface ICommandHandler {
	
	/**
	 * Execute the passed command.
	 * @param command the command that triggered the action.
	 * @param session TODO
	 * @return a Value containing the result.
	 * @see Command
	 */ 
	public IValue execute (Command command, ISession session) throws ThinklabException;

}
