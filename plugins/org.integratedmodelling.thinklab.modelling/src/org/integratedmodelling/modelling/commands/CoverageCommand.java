package org.integratedmodelling.modelling.commands;

import java.util.HashMap;

import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.modelling.Model;
import org.integratedmodelling.modelling.ModelFactory;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.KBoxManager;

@ThinklabCommand(
		name="coverage",
		description="compute the extent coverage of a model in the linked kboxes",
		argumentNames="model",
		argumentTypes="thinklab-core:Text",
		argumentDescriptions="the model id",
		optionLongNames="kbox",
		optionNames="k",
		optionArgumentLabels="all kboxes",
		optionTypes="thinklab-core:Text",
		optionDescriptions="kbox"
)
public class CoverageCommand implements ICommandHandler {

	IObservationContext ctx = null;
	HashMap<IConcept, IState> states = new HashMap<IConcept, IState>();
		@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {
		
		String concept = command.getArgumentAsString("model");
		
		IKBox kbox = KBoxManager.get();
		if (command.hasOption("kbox"))
			kbox = KBoxManager.get().requireGlobalKBox(command.getOptionAsString("kbox"));
		
		Model model = ModelFactory.get().requireModel(concept);	
		
		return 
			ModelFactory.get().getSpatialCoverage(model, kbox, session).
				transform(Geospace.get().getStraightGeoCRS());
	}

}
