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
import org.integratedmodelling.lang.SemanticType;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.literals.BooleanValue;

public class Is implements ICommandHandler {

	public ISemanticObject execute(Command command, ISession session) throws ThinklabException {

		// TODO this should figure out what the semantic type is for, cross
		// check properly, and
		// call the appropriate methods. So far it only handles concepts.
		SemanticType s1 = new SemanticType(command.getArgumentAsString("c1"));
		SemanticType s2 = new SemanticType(command.getArgumentAsString("c2"));

		boolean res = KnowledgeManager.get().requireConcept(s1).is(
				KnowledgeManager.get().requireConcept(s2));

		return BooleanValue.wrap(res);
	}

}
