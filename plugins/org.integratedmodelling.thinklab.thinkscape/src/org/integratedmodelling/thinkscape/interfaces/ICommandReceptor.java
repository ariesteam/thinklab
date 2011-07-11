package org.integratedmodelling.thinkscape.interfaces;

import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.command.Command;

public interface ICommandReceptor {
	
	public IValue submitCommand(Command cmd );
	
	public IValue submitCommand(String  strcmd );

}
