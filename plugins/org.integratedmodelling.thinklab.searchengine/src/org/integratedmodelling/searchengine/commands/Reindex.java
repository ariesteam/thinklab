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
package org.integratedmodelling.searchengine.commands;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.searchengine.SearchEngine;
import org.integratedmodelling.searchengine.SearchEnginePlugin;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;

@ThinklabCommand(
		name="reindex",
		description="index all loaded ontologies")
public class Reindex implements ICommandHandler {

	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {
		
		SearchEngine engine = 
			SearchEnginePlugin.get().getDefaultSearchEngine();
		
		String ontprop = "";
		for (IOntology o : 
			   KnowledgeManager.get().getKnowledgeRepository().retrieveAllOntologies()) {
			ontprop += 
				(ontprop.length() == 0 ? "" : ",") +
				o.getConceptSpace();
		}
		
		System.out.println("onts: " + ontprop);
		
		engine.clear();
		engine.setOntologies(ontprop);
		engine.initialize();
		
		return null;
	}

}
