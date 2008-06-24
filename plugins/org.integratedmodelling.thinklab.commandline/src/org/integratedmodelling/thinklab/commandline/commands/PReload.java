package org.integratedmodelling.thinklab.commandline.commands;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.commandline.CommandLine;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.extensions.CommandHandler;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.java.plugin.PluginLifecycleException;

/**
 * Just activate the named plugin.
 * 
 * @author Ferdinando
 *
 */
public class PReload implements CommandHandler {

	@Override
	public IValue execute(Command command, ICommandOutputReceptor outputDest,
			ISession session, KnowledgeManager km) throws ThinklabException {

		String plugin = command.getArgumentAsString("plugin");
		
		if (!plugin.contains("."))
			plugin = "org.integratedmodelling.thinklab." + plugin;
		
		CommandLine.get().getManager().deactivatePlugin(plugin);

		/*
		 * TODO may want to upgrade the plugin if a --upgrade option is 
		 * specified.
		 */
		
		try {
			CommandLine.get().getManager().activatePlugin(plugin);
		} catch (PluginLifecycleException e) {
			throw new ThinklabPluginException(e);
		}
		
		return null;
	}

}
