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
package org.integratedmodelling.thinklab.commandline.commands;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;

@ThinklabCommand(
	name="help", description="list commands and their options", 
	optionalArgumentNames="topic", optionalArgumentDefaultValues="__none", 
	optionalArgumentDescriptions="command to get help on",
	optionalArgumentTypes="thinklab-core:Text",
	optionArgumentLabels="command")
public class Help implements ICommandHandler {

	public ISemanticObject execute(Command command, ISession session) throws ThinklabException {

		String topic = command.getArgumentAsString("topic");

		if ("__none".equals(topic)) {
			/* loop over commands in KM; print a line for each one */
			session
					.getOutputStream().println("Available commands (\'help <command>\' for more):");
			for (CommandDeclaration decl : CommandManager.get()
					.getCommandDeclarations()) {
				session.getOutputStream().println("\t" + decl.ID + "\t"
						+ decl.description);
			}
		} else {
			/* should be a command name */
			CommandDeclaration cd = CommandManager.get()
					.getDeclarationForCommand(topic);

			if (cd == null)
				throw new ThinklabValidationException("command " + topic
						+ " undefined");

			if (session.getOutputStream() != null)
				cd.printLongSynopsis(session.getOutputStream());
		}

		return null;
	}
}
