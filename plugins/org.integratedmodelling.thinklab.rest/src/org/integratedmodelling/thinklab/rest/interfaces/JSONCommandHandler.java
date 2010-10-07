package org.integratedmodelling.thinklab.rest.interfaces;

import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.json.JSONObject;

public interface JSONCommandHandler extends ICommandHandler {

	public abstract JSONObject executeJSON(Command command, ISession session);
	
}
