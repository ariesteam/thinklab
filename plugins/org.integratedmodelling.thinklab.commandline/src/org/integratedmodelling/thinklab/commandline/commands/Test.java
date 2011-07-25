package org.integratedmodelling.thinklab.commandline.commands;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.modelling.model.ModelManager;

@ThinklabCommand(name="test",argumentNames="arg",argumentTypes="thinklab-core:Text", argumentDescriptions="test argument")
public class Test implements ICommandHandler {

	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {
		ModelManager.get().loadFile(command.getArgumentAsString("arg"));
		return null;
	}

}
