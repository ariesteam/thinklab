package org.integratedmodelling.geospace.commands;

import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.interfaces.IGazetteer;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

@ThinklabCommand(
		name="locate",
		description="locate a name in the installed gazetteers or list known locations",
		optionalArgumentDefaultValues="_",
		optionalArgumentDescriptions="region to locate",
		optionalArgumentTypes="thinklab-core:Text",
		optionalArgumentNames="location",
		optionNames="i,g",
		optionLongNames="import,gazetteer",
		optionTypes="thinklab-core:Text,thinklab-core:Text",
		optionDescriptions="URL to import from,name of gazetteer to use (default all)",
		optionArgumentLabels="import,gazetteer"
)
public class Locate implements ICommandHandler {

	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {

		IGazetteer gaz = null;
		String gname = null;
		
		if (command.hasOption("gazetteer")) {
			gaz = Geospace.get().requireGazetteer(gname = command.getOptionAsString("gazetteer"));
		}
		if (command.hasOption("import")) {
			
			if (gaz == null) {
				throw new ThinklabInappropriateOperationException(
					"a gazetteer to import to must be specified with the -g option");
			}
			if (gaz.isReadOnly()) {
				throw new ThinklabInappropriateOperationException(
					"gazetteer " + gname + " is read-only: cannot import");				
			}
			
			gaz.importLocations(command.getOptionAsString("import"));
		}
		
		if (command.getArgumentAsString("location").equals("_")) {
		
			for (String s : Geospace.get().listKnownFeatures()) {
				session.getOutputStream().println("\t" + s);
			}
			
		} else {
			
			String loc = command.getArgumentAsString("location");
			int i = 0;
			for (ShapeValue s : 
					(gaz == null ? 
						Geospace.get().lookupFeature(loc, false) : 
						gaz.resolve(loc, null, null))) {
				session.getOutputStream().println(i++ + ". " + s);
			}
		}
	
		return null;
	}

}
