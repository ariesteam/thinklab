/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.command;

import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedCommandException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.applications.ISessionManager;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.commands.IListingProvider;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.rest.interfaces.IRESTHandler;

public class CommandManager {
	
	HashMap<String, Class<?>> listingProviders = new HashMap<String, Class<?>>();
	HashMap<String, Class<?>> itemListingProviders = new HashMap<String, Class<?>>();
	
	/**
	 * command declarations are kept in a hash, indexed by command ID
	 */
	HashMap<String, CommandDeclaration> commands = new HashMap<String, CommandDeclaration>();

	/**
	 * each command has an action associated, also kept in a hash indexed by
	 * command ID
	 */
	HashMap<String, ICommandHandler> actions = new HashMap<String, ICommandHandler>();

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
	public void registerCommand(CommandDeclaration command, ICommandHandler action)
			throws ThinklabException {

		// TODO throw exception if command is installed
		commands.put(command.ID, command);
		actions.put(command.ID, action);
		
		// TODO register a REST service for the command if the command also implements IRESTHandler
		if (action instanceof IRESTHandler) {
			
		}
			
	}

	public void registerListingProvider(String label, String itemlabel, Class<?> provider) {
		listingProviders.put(label, provider);
		if (!itemlabel.equals(""))
			itemListingProviders.put(itemlabel, provider);
	}
	
	public IListingProvider getListingProvider(String label) {
		Class<?> cls = listingProviders.get(label);
		if (cls == null)
			return null;
		
		try {
			return (IListingProvider)cls.newInstance();
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
	}
	
	public IListingProvider getItemListingProvider(String label) {
		Class<?> cls = itemListingProviders.get(label);
		if (cls == null)
			return null;
		
		try {
			return (IListingProvider)cls.newInstance();
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
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
	 * @param session
	 *            the session t
	 * @return a literal containing a result value and the associated concept,
	 *         or null if the command is void.
	 * @throws ThinklabException
	 *             if anything happens in command execution
	 */
	public IValue submitCommand(Command cmd, ISession session)
			throws ThinklabException {

		/*
		 * happens at times with botched commands (e.g., strange eof from
		 * shutting down the VM)
		 */
		if (cmd == null || cmd.getDeclaration() == null)
			return null;

		ICommandHandler a = actions.get(cmd.getDeclaration().ID);
		return a.execute(cmd, session);

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
