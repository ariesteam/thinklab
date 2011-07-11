package org.integratedmodelling.geospace.commands;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;

@ThinklabCommand(
		name="checksrid", 
		argumentNames="srid", 
		argumentTypes="thinklab-core:Text", 
		argumentDescriptions="SRID code to check (with authority)")
public class CheckSRID implements ICommandHandler {

	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {
		String srid = command.getArgumentAsString("srid");
		org.opengis.referencing.crs.CoordinateReferenceSystem crs = null;
		
		try {
			crs = Geospace.getCRSFromID(srid);			
		} catch (ThinklabException e) {
		}

		if (crs == null) {
			session.getOutputStream().println("SRID " + srid + " not recognized");
		} else {
			session.getOutputStream().println("SRID " + srid + "OK" + "\n" + crs);
		}
 		return null;
	}

}
