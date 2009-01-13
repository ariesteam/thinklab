package org.integratedmodelling.thinklab.interfaces.commands;

import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

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
