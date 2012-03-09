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
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.ISemantics;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.metadata.Metadata;

@ThinklabCommand(name="test",argumentNames="arg",argumentTypes="thinklab:Text", argumentDescriptions="test argument")
public class Test implements ICommandHandler {

	@Override
	public ISemanticObject execute(Command command, ISession session)
			throws ThinklabException {
		
		Metadata metadue = new Metadata();
		metadue.put(Metadata.DC_COMMENT, "Diopungolo");

		Metadata metadata = new Metadata();
		metadata.put(Metadata.DC_COMMENT, "Stocazzo");
		metadata.put(Metadata.DC_CONTRIBUTOR, "Piccione");
		metadata.put(Metadata.DC_COVERAGE_SPATIAL, metadue);
		
		ISemanticObject o = Thinklab.get().annotate(metadata);
		
		ISemantics semantics = o.getSemantics();
		
		for (ISemantics rel : semantics.getRelationships()) {
			System.out.println(rel);
		}
		
		Object porco = Thinklab.get().instantiate(semantics);
		
		session.print(PolyList.prettyPrint(semantics.asList()));
		session.print("\n" + porco);
		
		return o;
	}

}
