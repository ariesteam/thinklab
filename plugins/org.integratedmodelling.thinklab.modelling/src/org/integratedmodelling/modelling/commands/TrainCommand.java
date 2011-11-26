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
		optionArgumentLabels="all kboxes,generated id, ",
		optionLongNames="kbox,id,clear",
		optionNames="k,id,clear",
		optionTypes="thinklab-core:Text,thinklab-core:Text,owl:Nothing",
		optionDescriptions="kbox,ID of trained instance,clear cache before computing",
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
			inpMin = new Integer(command.getArgumentAsString("input-threshold"));
		}
		if (command.hasOption("output-threshold")) {
			outMin = new Integer(command.getArgumentAsString("output-threshold"));
		}
		if (command.hasOption("algorithm")) {
			algorithm = command.getArgumentAsString("algorithm");
		}
		if (command.hasOption("id")) {
			id = command.getArgumentAsString("id");
		}
		
		
		id = TrainingManager.get().doTraining(model, context, kbox, session, id, algorithm, inpMin, outMin);

		if (id == null) {
			session.print("no candidates for training found in " + model.getId());
		} else {
			session.print("trained instance ID is " + id);
		}
		
//		IQueryResult r = 
//			ModelFactory.get().run(model, kbox, session, null, context);
//		
//		if (session.getOutputStream() != null) {
//			session.getOutputStream().println(
//					r.getTotalResultCount() + " possible observation(s) found");
//		}
//		
//		IValue ret = null;
//		
//		if (r.getTotalResultCount() > 0) {
//			
//			IValue res = r.getResult(0, session);
//			IContext result = ((ContextValue)res).getObservationContext();
//
//			if (command.hasOption("write")) {
//				IDataset archive = new FileArchive(result);
//				archive.persist();
//			}
//			
//			if (command.hasOption("visualize")) {
//				IVisualization visualization = new FileVisualization(result);
//				visualization.visualize();
//			}
//			
//			// check if a listener has set ctx, which means we're visualizing
//			if (command.hasOption("outfile")) {
//
//				/*
//				 * save to netcdf
//				 */
//				String outfile = command.getOptionAsString("outfile");
//
//				NetCDFArchive out = new NetCDFArchive();
//				out.setContext(result);
//				out.write(outfile);
//				session.print(
//					"result of " + concept + " model written to " + outfile);
//			}
//			
//			if (command.hasOption("tiff")) {
//
//				/*
//				 * save to netcdf
//				 */
//				GISArchive out = new GISArchive(result);
//				String outdir = out.persist();
//				session.print(
//					"GeoTIFF files " + concept + " written to " + outdir);
//			}
//			
			if (command.hasOption("dump")) {
//				ObservationListing lister = new ObservationListing(result);
//				lister.dump(session.getOutputStream());
			}

//			ret = res;
//		}
			
		return null;
	}

}
