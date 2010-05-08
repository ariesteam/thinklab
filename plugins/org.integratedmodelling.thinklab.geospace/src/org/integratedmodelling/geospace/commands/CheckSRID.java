package org.integratedmodelling.geospace.commands;

import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

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
