package org.integratedmodelling.modelling.commands;

import org.integratedmodelling.modelling.Model;
import org.integratedmodelling.modelling.ModelManager;
import org.integratedmodelling.modelling.ModelResult;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

@ThinklabCommand(
		name="model",
		description="build a model observation of the given concept and return it",
		argumentNames="concept",
		argumentTypes="thinklab-core:Text",
		argumentDescriptions="the concept to build a model for",
		optionalArgumentNames="kbox,context",
		optionalArgumentDefaultValues="_NONE_,_NONE_",
		optionalArgumentDescriptions="a kbox to resolve dependent concepts,a context observation to set time and/or space for the results",
		optionalArgumentTypes="thinklab-core:Text,thinklab-core:Text",
		optionArgumentLabels="kbox,context",
		returnType="observation:Observation")
public class ModelCommand implements ICommandHandler {

	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {
		
		IConcept concept = 
			KnowledgeManager.get().requireConcept(command.getArgumentAsString("concept"));
		
		IKBox kbox = null;
		IInstance context = null;
		
		Model model = ModelManager.get().requireModel(concept);
	
		ModelResult observation = ModelManager.query(model, kbox, session);
				
		return null;
	}

}
