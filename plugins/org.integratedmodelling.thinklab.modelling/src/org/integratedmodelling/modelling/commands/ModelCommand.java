package org.integratedmodelling.modelling.commands;

import org.integratedmodelling.modelling.Model;
import org.integratedmodelling.modelling.ModelManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.KBoxManager;

@ThinklabCommand(
		name="model",
		description="build a model observation of the given concept and return it",
		argumentNames="concept",
		argumentTypes="thinklab-core:Text",
		argumentDescriptions="the concept to build a model for or the model id",
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
		
		String concept = command.getArgumentAsString("concept");
		String kb = command.getArgumentAsString("kbox");

		IKBox kbox = null;
		if (kb != null && !kb.equals("_NONE_"))
			kbox = KBoxManager.get().requireGlobalKBox(kb);
		
		Model model = ModelManager.get().requireModel(concept);
		
		IQueryResult obs = model.observe(kbox, session);
				
		if (session.getOutputStream() != null)
			session.getOutputStream().println(
					"\tQuery returned " + obs.getTotalResultCount() + " results");
		
		return obs.getTotalResultCount() == 0 ? null : obs.getResult(0, session);
	}

}
