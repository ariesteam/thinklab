package org.integratedmodelling.modelling.commands;

import org.integratedmodelling.thinklab.command.InteractiveSubcommandInterface;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

@ThinklabCommand(name="datamod")
public class Datamod extends InteractiveSubcommandInterface {

	
	
	@Override
	protected IValue cmd(String cmd, String[] arguments)
		throws ThinklabException {
		
		IValue ret = null;
		
		if (cmd.equals("kbox")) {
			
		} else if (cmd.equals("import")) {
			
		} else if (cmd.equals("save")) {
			
		} else if (cmd.equals("scan")) {
			
		}
			
		return ret;
	}

}
