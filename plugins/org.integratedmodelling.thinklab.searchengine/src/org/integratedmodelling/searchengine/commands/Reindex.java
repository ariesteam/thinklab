package org.integratedmodelling.searchengine.commands;

import org.integratedmodelling.searchengine.SearchEngine;
import org.integratedmodelling.searchengine.SearchEnginePlugin;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

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
