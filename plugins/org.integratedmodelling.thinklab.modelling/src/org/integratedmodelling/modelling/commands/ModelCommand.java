package org.integratedmodelling.modelling.commands;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.integratedmodelling.corescience.contextualization.Compiler;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.idv.IDV;
import org.integratedmodelling.modelling.Model;
import org.integratedmodelling.modelling.ModelManager;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.visualization.NetCDFArchive;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.thinklab.literals.ObjectReferenceValue;
import org.integratedmodelling.utils.Polylist;

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
		optionLongNames="kbox,visualize,outfile,resolution",
		optionNames="k,v,o,r",
		optionArgumentLabels="all kboxes,,none,256",
		optionTypes="thinklab-core:Text,owl:Nothing,thinklab-core:Text,thinklab-core:Integer",
		optionDescriptions="kbox,visualize after modeling,NetCDF file to export results to,max linear resolution for raster grid",
		returnType="observation:Observation")
public class ModelCommand implements ICommandHandler {

	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {
		
		String concept = command.getArgumentAsString("model");
		
		IKBox kbox = KBoxManager.get();
		if (command.hasOption("kbox"))
			kbox = KBoxManager.get().requireGlobalKBox(command.getOptionAsString("kbox"));
		
		Model model = ModelManager.get().requireModel(concept);
		
		IInstance where = null;
		
		if (command.hasArgument("context")) {
			
			int res = 
				(int)command.getOptionAsDouble("resolution", 256.0);	
			ShapeValue roi = null;
			Collection<ShapeValue> shapes = 
				Geospace.get().lookupFeature(
						command.getArgumentAsString("context"), true);
			if (shapes.size() > 0)
				roi = shapes.iterator().next();
				
			if (roi != null)
				where = 
					session.createObject(RasterGrid.createRasterGrid(roi, res));
		}
		
		IQueryResult r = model.observe(kbox, session, where);
				
		if (session.getOutputStream() != null) {
			
			session.getOutputStream().println(
					"query returned " + r.getTotalResultCount() + " results");
		

		}
		
		IQueryResult obs = model.observe(kbox, session, where);
				
		if (session.getOutputStream() != null)
			session.getOutputStream().println(
					"\tQuery returned " + obs.getTotalResultCount() + " results");

		IValue ret = null;
		
		if (r.getTotalResultCount() > 0) {
			
			Polylist lr = r.getResultAsList(0, null);
			IInstance res = session.createObject(lr);

			IInstance result = Compiler.contextualize((IObservation) res
					.getImplementation(), session);

			if (command.hasOption("visualize") || command.hasOption("outfile")) {
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
				out.setObservation(result);
				out.write(outfile);
				ModellingPlugin.get().logger()
						.info(
							"result of " + concept + " model written to "
										+ outfile);

				if (command.hasOption("visualize")) {
					IDV.visualize(outfile);
				}
			}

			ret = new ObjectReferenceValue(result);
		}
			
		return ret;
	}

}
