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
