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
package org.integratedmodelling.modelling.commands;

import org.integratedmodelling.modelling.ModelMap;
import org.integratedmodelling.modelling.annotation.OPALAnnotationImporter;
import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.InteractiveCommandHandler;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * Driver for everything that can be done with the modeling system. 
 *  
 * @author Ferdinando
 *
 */
@ThinklabCommand(name="mod",
		argumentNames="action",
		argumentTypes="thinklab-core:Text",
		optionalArgumentNames="arg0,arg1,arg2",
		optionalArgumentDefaultValues="_,_,_",
		optionalArgumentTypes="thinklab-core:Text,thinklab-core:Text,thinklab-core:Text",
		optionalArgumentDescriptions=" , , ")
public class ModellingSubsystemCommand extends InteractiveCommandHandler {

	@Override
	protected IValue doInteractive(Command command, ISession session)
			throws ThinklabException {
		
		String action = command.getArgumentAsString("action");
		
		if (action.equals("list")) {
			
			String s = command.getArgumentAsString("arg0");
			ModelMap.printSource(s, session.getOutputStream());
			
		} else if (action.equals("import")) {
			
			String s = command.getArgumentAsString("arg0");
			
			/*
			 * 2nd arg decides what to import from where
			 * TODO modularize this
			 */
			if (s.equals("opal")) {
				/*
				 * import annotations from XML
				 */
				OPALAnnotationImporter.importAnnotations(
						command.getArgumentAsString("arg1"), command.getArgumentAsString("arg2"));
			}
			
		} else if (action.equals("sync")) {
			
		} else if (action.equals("release")) {
			
			String s = command.getArgumentAsString("arg0");
			ModelFactory.get().releaseNamespace(s);
			
		}
		
		return null;
	}

}
