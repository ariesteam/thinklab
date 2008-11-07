package org.integratedmodelling.thinklab.extensions;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.ICommandInputProvider;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;

public interface CommandHandler {
	
	/**
	 * Execute the passed command.
	 * @param command the command that triggered the action.
	 * @param inputSource TODO
	 * @param outputDest TODO
	 * @param session TODO
	 * @param km the Knowledge Manager, only for convenience.
	 * @return a Value containing the result.
	 * @see Command
	 */ 
	public IValue execute (Command command, ICommandInputProvider inputSource, ICommandOutputReceptor outputDest, ISession session, KnowledgeManager km) throws ThinklabException;

}
