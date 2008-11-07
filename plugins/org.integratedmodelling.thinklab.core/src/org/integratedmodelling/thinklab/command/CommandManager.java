package org.integratedmodelling.thinklab.command;

import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedCommandException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.extensions.CommandHandler;
import org.integratedmodelling.thinklab.interfaces.ICommandInputProvider;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.ISessionManager;
import org.integratedmodelling.thinklab.interfaces.IValue;

public class CommandManager {
	/**
	 * command declarations are kept in a hash, indexed by command ID
	 */
	HashMap<String, CommandDeclaration> commands = new HashMap<String, CommandDeclaration>();

	/**
	 * each command has an action associated, also kept in a hash indexed by
	 * command ID
	 */
	HashMap<String, CommandHandler> actions = new HashMap<String, CommandHandler>();

	/**
	 * Register a command for use in the Knowledge Manager. The modality of
	 * invocation and execution of commands depends on the particular
	 * IKnowledgeInterface installed.
	 * 
	 * @param command
	 *            the CommandDeclaration to register
	 * @param action
	 *            the Action executed in response to the command
	 * @throws ThinklabException
	 * @see CommandDeclaration
	 * @see ISessionManager
	 */
	public void registerCommand(CommandDeclaration command, CommandHandler action)
			throws ThinklabException {

		// TODO throw exception if command is installed
		commands.put(command.ID, command);
		actions.put(command.ID, action);
	}

	public CommandDeclaration getDeclarationForCommand(String tok) {
		return commands.get(tok);
	}
	
    
	public Collection<CommandDeclaration> getCommandDeclarations() {
		return commands.values();
	}

	public CommandDeclaration requireDeclarationForCommand(String tok)
			throws ThinklabMalformedCommandException {
		CommandDeclaration cd = commands.get(tok);
		if (cd == null)
			throw new ThinklabMalformedCommandException("unknown command "
					+ tok);
		return cd;
	}

	/**
	 * Check if a command with a particular name has been registered.
	 * 
	 * @param commandID
	 * @return
	 */
	public boolean hasCommand(String commandID) {
		return actions.get(commandID) != null;
	}

	/**
	 * Submit and execute the passed command. Command is assumed validated so no
	 * checking is done. Returns a Value as result value, answered by execute()
	 * called on the corresponding action.
	 * 
	 * @param cmd
	 *            the command
	 * @param inputReader TODO
	 * @param outputWriter
	 * @param session
	 *            the session t
	 * @return a literal containing a result value and the associated concept,
	 *         or null if the command is void.
	 * @throws ThinklabException
	 *             if anything happens in command execution
	 */
	public IValue submitCommand(Command cmd,
			ICommandInputProvider inputReader, ICommandOutputReceptor outputWriter, ISession session)
			throws ThinklabException {

		/*
		 * happens at times with botched commands (e.g., strange eof from
		 * shutting down the VM)
		 */
		if (cmd == null || cmd.getDeclaration() == null)
			return null;

		CommandHandler a = actions.get(cmd.getDeclaration().ID);
		return a.execute(cmd, inputReader, outputWriter, session, KnowledgeManager.get());
		// TODO transfer to knowledge manager logging policy
	}

	/**
	 * Get the only instance of the plugin registry.
	 * 
	 * @return the plugin registry
	 * @throws ThinklabNoKMException
	 *             if no knowledge manager was initialized.
	 */
	static public CommandManager get() throws ThinklabNoKMException {
		return KnowledgeManager.get().getCommandManager();
	}

}
