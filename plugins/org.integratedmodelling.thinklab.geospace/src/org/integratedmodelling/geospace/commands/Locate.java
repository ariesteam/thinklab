/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.geospace.commands;

import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.interfaces.IGazetteer;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;

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
			
			gaz.importLocations(command.getOptionAsString("import"), null);
		}
		
		if (command.getArgumentAsString("location").equals("_")) {
		
			for (String s : Geospace.get().listKnownFeatures()) {
				session.getOutputStream().println("\t" + s);
			}
			
		} else {
			
			String loc = command.getArgumentAsString("location");
			
			IQueryResult result = 
				(gaz == null ?
					Geospace.get().lookupFeature(loc) :
					gaz.query(gaz.parseQuery(loc)));		
			

			if (result.getResultCount() > 0) {

				for (int i = 0; i < result.getResultCount(); i++) {

					session.getOutputStream().println(
							i +
							".\t"
							+ result.getResultField(i, "id")
							+ "\t"
							+ (int)(result.getResultScore(i)) + "%"
							+ "\t"
							+ result.getResultField(i, "label"));
					
					if (result.getResultCount() == 1)
						session.getOutputStream().println(
							"\t" +
							result.getResultField(i, IGazetteer.SHAPE_FIELD));
				}
			} else {
				session.getOutputStream().println("no results found");
			}

			
		}
	
		return null;
	}

}
