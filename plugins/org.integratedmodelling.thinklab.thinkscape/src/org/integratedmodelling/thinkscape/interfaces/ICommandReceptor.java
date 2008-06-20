package org.integratedmodelling.thinkscape.interfaces;

import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.IValue;

public interface ICommandReceptor {
	
	public IValue submitCommand(Command cmd );
	
	public IValue submitCommand(String  strcmd );

}
