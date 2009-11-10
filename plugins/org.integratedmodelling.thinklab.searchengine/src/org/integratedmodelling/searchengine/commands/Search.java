package org.integratedmodelling.searchengine.commands;

import org.integratedmodelling.searchengine.QueryString;
import org.integratedmodelling.searchengine.ResultContainer;
import org.integratedmodelling.searchengine.SearchEngine;
import org.integratedmodelling.searchengine.SearchEnginePlugin;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;

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
		
		IQueryResult zio = engine.query(new QueryString(q));
		
		for (int i = 0; i < zio.getResultCount(); i++) {
			((ResultContainer)zio).printResult(i, session.getOutputStream());
		}
		
		session.getOutputStream().println("\n" + zio.getResultCount() + " results");
		
		return null;
	}

}
