package org.integratedmodelling.mca.commands;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.mca.electre3.controller.E3Controller;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;

@ThinklabCommand(name="electre3")
public class Electre3 implements ICommandHandler {

	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {
        new E3Controller();
		return null;
	}
}
