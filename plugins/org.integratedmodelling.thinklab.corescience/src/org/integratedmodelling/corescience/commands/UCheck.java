package org.integratedmodelling.corescience.commands;

import org.integratedmodelling.corescience.units.Unit;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

@ThinklabCommand(
		name="ucheck",
		description="check unit syntax",
		argumentNames="unit",
		argumentTypes="thinklab-core:Text",
		argumentDescriptions="unit string to check (no spaces)")
public class UCheck implements ICommandHandler {

	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {

		String s = command.getArgumentAsString("unit");
		Unit unit = new Unit(s);
		unit.dump(session.getOutputStream());
		
		return null;
	}

}
