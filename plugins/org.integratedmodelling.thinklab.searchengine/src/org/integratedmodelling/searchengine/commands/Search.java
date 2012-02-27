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

import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.searchengine.QueryString;
import org.integratedmodelling.searchengine.ResultContainer;
import org.integratedmodelling.searchengine.SearchEngine;
import org.integratedmodelling.searchengine.SearchEnginePlugin;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;

@ThinklabCommand(
		name="search",
		optionalArgumentNames="p1,p2,p3,p4,p5,p6",
		optionalArgumentTypes="thinklab-core:Text,thinklab-core:Text,thinklab-core:Text,thinklab-core:Text,thinklab-core:Text,thinklab-core:Text",
		optionalArgumentDescriptions="p1,p2,p3,p4,p5,p6",
		optionalArgumentDefaultValues="_,_,_,_,_,_",
		optionArgumentLabels="p1,p2,p3,p4,p5,p6")
public class Search implements ICommandHandler {

	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {
		
		if (session.getOutputStream() == null)
			throw new ThinklabValidationException("interactive command search called in non-interactive session");
		
		SearchEngine engine = SearchEnginePlugin.get().getDefaultSearchEngine();
		
//		if (engine.)
//			throw new ThinklabResourceNotFoundException("search engine not initialized. Run 'reindex' after loading all plugins of interest.");
		
		// chain arguments into search string
		String q = "";
		for (int i = 1; i < 7; i++) {
			if (command.getArgumentAsString("p"+i).equals("_"))
				break;
			q += (i == 1 ? "" : " ") + command.getArgumentAsString("p"+i);
		}
	
		if (q.trim().equals("")) {
			session.getOutputStream().println("empty query string");
			return null;
		}
		
		List<Object> zio = engine.query(new QueryString(q));
		
		for (int i = 0; i < zio.size(); i++) {
			((ResultContainer)zio).printResult(i, session.getOutputStream());
		}
		
		session.getOutputStream().println("\n" + zio.size() + " results");
		
		return null;
	}

}
