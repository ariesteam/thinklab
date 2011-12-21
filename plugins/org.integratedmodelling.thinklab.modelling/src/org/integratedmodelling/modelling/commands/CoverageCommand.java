/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.modelling.commands;

import java.util.HashMap;

import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.interfaces.IGazetteer;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.modelling.model.Model;
import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.KBoxManager;

@ThinklabCommand(
		name="coverage",
		description="compute the extent coverage of a model in the linked kboxes",
		argumentNames="model",
		argumentTypes="thinklab-core:Text",
		argumentDescriptions="the model id",
		optionLongNames="kbox,area",
		optionNames="k,c",
		optionArgumentLabels="all kboxes,area to check for intersection",
		optionTypes="thinklab-core:Text,thinklab-core:Text",
		optionDescriptions="kbox,area identifier"
)
public class CoverageCommand implements ICommandHandler {

	IObservationContext ctx = null;
	HashMap<IConcept, IState> states = new HashMap<IConcept, IState>();
		@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {
		
		String concept = command.getArgumentAsString("model");
		ShapeValue area = null;
		
		IKBox kbox = KBoxManager.get();
		if (command.hasOption("kbox"))
			kbox = KBoxManager.get().requireGlobalKBox(command.getOptionAsString("kbox"));
		
		if (command.hasOption("area")) {
			
			String loc = command.getOptionAsString("area");
			
			IQueryResult result = Geospace.get().lookupFeature(loc);		
			int shapeidx = 0;

			if (result.getResultCount() > 0) {

				for (int i = 0; i < result.getResultCount(); i++) {

					session.getOutputStream().println(
							i +
							".\t"
							+ result.getResultField(i, "id")
							+ "\t"
							+ (int)(result.getResultScore(i)) + "%"
							+ "\t"
							+ result.getResultField(i, "label"));
					
					session.getOutputStream().println(
							"\t" +
							result.getResultField(i, IGazetteer.SHAPE_FIELD));
				}
				
				if (result.getResultCount() > 1)
					session.getOutputStream().println("warning: multiple locations for " + loc + ": choosing the first match");

				area = (ShapeValue) result.getResultField(shapeidx, IGazetteer.SHAPE_FIELD);
				
			} else {
				throw new ThinklabResourceNotFoundException("no shape found for " + loc);
			}
		}
		
		Model model = ModelFactory.get().requireModel(concept);	
		
		ShapeValue ret = 
			ModelFactory.get().getSpatialCoverage(model, kbox, session);
		
		ret = ret.transform(DefaultGeographicCRS.WGS84/*Geospace.get().getStraightGeoCRS()*/);
		
		if (area != null) {
			if (ret.contains(area)) {
				session.getOutputStream().println(
						command.getOptionAsString("area") + " is fully covered in " + concept);
			} else {
				session.getOutputStream().println(
						command.getOptionAsString("area") + " is NOT fully covered in " + concept +
						": valid intersection is " +
						area.intersection(ret).transform(Geospace.get().getStraightGeoCRS()));
			}
		}
		
		return ret;
		
	}

}
