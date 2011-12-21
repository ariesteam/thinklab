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
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.graph.ConceptMap;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;

/** the help command for the command-line interface */
public class CMap implements ICommandHandler {

	public IValue execute(Command command, ISession session) throws ThinklabException {

		String c = command.getArgumentAsString("concept");

		IConcept ct = KnowledgeManager.Thing();

		if (c != null && !c.equals("__none")) {
			ct = KnowledgeManager.get().requireConcept(c);
		}

		ConceptMap cmap = new ConceptMap(ct);

		if (session.getOutputStream() != null)
			cmap.dump(session.getOutputStream());

		cmap.show();

		return null;
	}
}
