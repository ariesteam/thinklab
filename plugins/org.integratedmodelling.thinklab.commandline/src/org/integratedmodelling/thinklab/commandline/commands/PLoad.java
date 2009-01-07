package org.integratedmodelling.thinklab.commandline.commands;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.commandline.CommandLine;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.extensions.CommandHandler;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandInputProvider;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.java.plugin.PluginLifecycleException;

/**
 * Just activate the named plugin.
 * 
 * @author Ferdinando
 *
 */
public class PLoad implements CommandHandler {

	@Override
	public IValue execute(Command command, ICommandInputProvider inputSource,
			ICommandOutputReceptor outputDest, ISession session, KnowledgeManager km) throws ThinklabException {

		String plugin = command.getArgumentAsString("plugin");
		
		plugin = Thinklab.resolvePluginName(plugin, true);
		
		try {
			CommandLine.get().getManager().activatePlugin(plugin);
		} catch (PluginLifecycleException e) {
			throw new ThinklabPluginException(e);
		}
		
		return null;
	}

}
