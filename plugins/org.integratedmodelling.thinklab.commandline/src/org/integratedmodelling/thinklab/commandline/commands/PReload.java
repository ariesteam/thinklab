package org.integratedmodelling.thinklab.commandline.commands;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.commandline.CommandLine;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.java.plugin.PluginLifecycleException;

/**
 * Just activate the named plugin.
 * 
 * @author Ferdinando
 *
 */
public class PReload implements ICommandHandler {

	@Override
	public IValue execute(Command command, ISession session) throws ThinklabException {

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
			throw new ThinklabValidationException(e);
		}
		
		return null;
	}

}
