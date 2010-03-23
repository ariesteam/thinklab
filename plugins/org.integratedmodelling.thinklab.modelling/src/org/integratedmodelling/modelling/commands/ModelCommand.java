package org.integratedmodelling.modelling.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.listeners.IContextualizationListener;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.geospace.interfaces.IGazetteer;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.idv.IDV;
import org.integratedmodelling.modelling.Model;
import org.integratedmodelling.modelling.ModelFactory;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.ObservationFactory;
import org.integratedmodelling.modelling.visualization.NetCDFArchive;
import org.integratedmodelling.modelling.visualization.ObservationListing;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.thinklab.literals.ObjectReferenceValue;

@ThinklabCommand(
		name="model",
		description="build a model observation of the given concept and return it",
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
public class ModelCommand implements ICommandHandler {

	IObservationContext ctx = null;
	HashMap<IConcept, IState> states = new HashMap<IConcept, IState>();
	
	class Listener implements IContextualizationListener {

		@Override
		public void onContextualization(IObservation original,
				IObservation obs, ObservationContext context) {
			ctx = context;
			try {
				states.putAll(ObservationFactory.getStateMap(obs));
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		}

		@Override
		public void postTransformation(IObservation original, IObservation obs,
				ObservationContext context) {
			try {
				states.putAll(ObservationFactory.getStateMap(obs));
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		}

		@Override
		public void preTransformation(IObservation original, IObservation obs,
				ObservationContext context) {
			try {
				states.putAll(ObservationFactory.getStateMap(obs));
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
	}
	
	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {
		
		String concept = command.getArgumentAsString("model");
		
		IKBox kbox = KBoxManager.get();
		if (command.hasOption("kbox"))
			kbox = KBoxManager.get().requireGlobalKBox(command.getOptionAsString("kbox"));
		
		Model model = ModelFactory.get().requireModel(concept);
		
		IInstance where = null;
		
		if (command.hasArgument("context")) {
			
			int res = 
				(int)command.getOptionAsDouble("resolution", 256.0);	
			ShapeValue roi = null;
			IQueryResult result = 
				Geospace.get().lookupFeature(
						command.getArgumentAsString("context"));
			if (result.getTotalResultCount() > 0)
				roi = (ShapeValue) result.getResultField(0, IGazetteer.SHAPE_FIELD);
				
			if (roi != null)
				where = 
					session.createObject(RasterGrid.createRasterGrid(roi, res));
			else 
				throw new ThinklabResourceNotFoundException(
						"region name " + 
						command.getArgumentAsString("context") +
						" cannot be resolved");
		}
		
		ArrayList<IContextualizationListener> listeners = 
			new ArrayList<IContextualizationListener>();
		if (command.hasOption("visualize") || command.hasOption("outfile")) {
			listeners.add(new Listener());
		}
	
		if (command.hasOption("clear")) {
			ModelFactory.get().clearCache();
		}
		
		IQueryResult r = ModelFactory.get().run(model, kbox, session, listeners, 
				(Topology)ObservationFactory.getObservation(where));
		
		if (session.getOutputStream() != null) {
			session.getOutputStream().println(
					r.getTotalResultCount() + " possible model(s) found");
		}
		
		IValue ret = null;
		
		if (r.getTotalResultCount() > 0) {
			
			IInstance result = r.getResult(0, session).asObjectReference().getObject();

			// check if a listener has set ctx, which means we're visualizing
			if (this.ctx != null) {
				
				/*
				 * save to netcdf
				 */
				String outfile = null;
				try {
					outfile = command.hasOption("outfile") ? command
							.getOptionAsString("outfile") : File
							.createTempFile("ncf", ".nc").toString();
				} catch (IOException e) {
					throw new ThinklabIOException(e);
				}

				NetCDFArchive out = new NetCDFArchive();
				out.setStates(this.states, this.ctx);
				out.write(outfile);
				ModellingPlugin.get().logger()
						.info(
							"result of " + concept + " model written to "
										+ outfile);

				if (command.hasOption("visualize")) {
					IDV.visualize(outfile);
				}
			}
			
			if (command.hasOption("dump")) {
				ObservationListing lister = new ObservationListing(result);
				lister.dump(session.getOutputStream());
			}

			ret = new ObjectReferenceValue(result);
		}
			
		return ret;
	}

}
