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
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.project.ThinklabProject;

/**
 * Start and stop the REST service.
 * 
 * @author ferdinando.villa
 *
 */
@ThinklabCommand(
		name="pload",
		argumentNames="project",
		argumentTypes="thinklab:Text",
		argumentDescriptions="project"
		)
public class PLoad implements ICommandHandler {
	
	@Override
	public ISemanticObject<?> execute(Command command, ISession session)
			throws ThinklabException {
		
		String project = command.getArgumentAsString("project");
		IProject prj = Thinklab.get().getProject(project);
		if (prj != null) {
			((ThinklabProject)prj).load();
		} else {
			throw new ThinklabResourceNotFoundException("project " + project + " not found");
		}
		return null;
	}

}
