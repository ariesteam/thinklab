package org.integratedmodelling.modelling.commands;

import java.util.HashMap;

import org.integratedmodelling.clojure.ClojureInterpreter;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.interfaces.IVisualization;
import org.integratedmodelling.modelling.literals.ContextValue;
import org.integratedmodelling.modelling.model.Model;
import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.modelling.storage.NetCDFArchive;
import org.integratedmodelling.modelling.visualization.FileVisualization;
import org.integratedmodelling.modelling.visualization.ObservationListing;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.KBoxManager;

@ThinklabCommand(
		name="rank",
		description="build a ranking observation of the given concept and return it",
		argumentNames="model",
		argumentTypes="thinklab-core:Text",
		argumentDescriptions="the concept to build a model for or the model id",
		optionalArgumentNames="context",
		optionalArgumentDefaultValues="_NONE_",
		optionalArgumentDescriptions="id of a spatial feature to define the spatial context",
		optionalArgumentTypes="thinklab-core:Text",
		optionArgumentLabels="all kboxes,,,none,256, ",
		optionLongNames="kbox,visualize,dump,outfile,resolution,clear",
		optionNames="k,v,d,o,r,c",
		optionTypes="thinklab-core:Text,owl:Nothing,owl:Nothing,thinklab-core:Text,thinklab-core:Integer,owl:Nothing",
		optionDescriptions="kbox,visualize after modeling,dump results to console,NetCDF file to export results to,max linear resolution for raster grid,clear cache before computing",
		returnType="observation:Observation")
public class RankCommand implements ICommandHandler {

	IObservationContext ctx = null;
	HashMap<IConcept, IState> states = new HashMap<IConcept, IState>();
	
	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {
		
		String concept = command.getArgumentAsString("model");
		
		IKBox kbox = KBoxManager.get();
		if (command.hasOption("kbox"))
			kbox = KBoxManager.get().requireGlobalKBox(command.getOptionAsString("kbox"));
		
		/*
		 * build ranking model for concept
		 */
		String clj = 
			"(modelling/model '" + 
			concept + 
			" (modelling/ranking '" + concept + "))";
					
		Model model = (Model) new ClojureInterpreter().evalRaw(clj, "user", null);
		
		IContext context = null;
		
		if (command.hasArgument("context")) {	
			context = ModelFactory.get().requireContext(command.getArgumentAsString("context"));
		}	
			
		if (command.hasOption("clear")) {
			ModelFactory.get().clearCache();
		}
		
		IQueryResult r = ModelFactory.get().run(model, kbox, session, null, context);
		
		if (session.getOutputStream() != null) {
			session.getOutputStream().println(
					r.getTotalResultCount() + " possible model(s) found");
		}
		
		IValue ret = null;
		
		if (r.getTotalResultCount() > 0) {
			
			IValue res = r.getResult(0, session);
			IObservationContext result = ((ContextValue)res).getObservationContext();

			if (command.hasOption("outfile")) {
				
				/*
				 * save to netcdf
				 */
				String outfile = command.getOptionAsString("outfile");

				NetCDFArchive out = new NetCDFArchive();
				out.setContext(result);
				out.write(outfile);
				ModellingPlugin.get().logger()
						.info(
							"result of " + concept + " model written to "
										+ outfile);
			}
			
			if (command.hasOption("visualize")) {
				IVisualization visualization = new FileVisualization(result);
				visualization.visualize();
			}
			
			if (command.hasOption("dump")) {
				ObservationListing lister = new ObservationListing(result);
				lister.dump(session.getOutputStream());
			}

			ret = res;
		}
			
		return null;
	}

}
