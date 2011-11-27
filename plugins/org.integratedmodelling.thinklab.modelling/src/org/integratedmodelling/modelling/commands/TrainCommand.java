package org.integratedmodelling.modelling.commands;

import java.util.HashMap;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.listeners.IContextualizationListener;
import org.integratedmodelling.modelling.model.Model;
import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.modelling.training.TrainingManager;
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
		name="train",
		description="train the trainable subcomponents of the passed model in a context",
		argumentNames="model",
		argumentTypes="thinklab-core:Text",
		argumentDescriptions="the concept to build a model for or the model id",
		optionalArgumentNames="context,context1",
		optionalArgumentDefaultValues="_NONE_,_NONE_",
		optionalArgumentDescriptions="spatial or temporal context,spatial or temporal context",
		optionalArgumentTypes="thinklab-core:Text,thinklab-core:Text",
		optionArgumentLabels="n,n,kbox,id, , ",
		optionLongNames="input-threshold,output-threshold,kbox,training-id,clear,algorithm",
		optionNames="it,ot,k,id,clr,alg",
		optionTypes="thinklab-core:Integer,thinklab-core:Integer,thinklab-core:Text,thinklab-core:Text,owl:Nothing,thinklab-core:Text",
		optionDescriptions="min inputs,min outputs,kbox,ID of trained instance,clear cache before computing,training algorithm (when applicable)",
		returnType="observation:Observation")
public class TrainCommand implements ICommandHandler {

	IObservationContext ctx = null;
	HashMap<IConcept, IState> states = new HashMap<IConcept, IState>();
	
	class Listener implements IContextualizationListener {

		@Override
		public void onContextualization(IObservation original, ObservationContext context) {
			ctx = context;
		}

		@Override
		public void postTransformation(IObservation original, ObservationContext context) {
		}

		@Override
		public void preTransformation(IObservation original, ObservationContext context) {
		}
	}

	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {
		
		String concept = command.getArgumentAsString("model");
		String ctxname = command.getArgumentAsString("context");
		
		IKBox kbox = KBoxManager.get();
		if (command.hasOption("kbox"))
			kbox = KBoxManager.get().requireGlobalKBox(command.getOptionAsString("kbox"));
		
		Model model = ModelFactory.get().requireModel(concept);
		
		IContext context = ModelFactory.get().requireContext(ctxname);
		
		if (command.hasOption("clear")) {
			// TODO clear all previous trainings for this model
		}
			
		int inpMin = 1, outMin = 1;
		String id = null;
		
		String algorithm = null;

		if (command.hasOption("input-threshold")) {
			inpMin = new Integer(command.getOptionAsString("input-threshold"));
		}
		if (command.hasOption("output-threshold")) {
			outMin = new Integer(command.getOptionAsString("output-threshold"));
		}
		if (command.hasOption("algorithm")) {
			algorithm = command.getOptionAsString("algorithm");
		}
		if (command.hasOption("training-id")) {
			id = command.getOptionAsString("training-id");
		}
		
		id = TrainingManager.get().doTraining(model, context, kbox, session, id, algorithm, inpMin, outMin);

		if (id == null) {
			session.print("no candidates for training found in " + model.getId());
		} else {
			session.print("trained instance ID is " + id);
		}
		
		return null;
	}

}
