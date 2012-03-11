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
package org.integratedmodelling.thinklab.geospace.commands;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.geospace.Geospace;
import org.integratedmodelling.thinklab.geospace.interfaces.IGazetteer;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;

@ThinklabCommand(
		name="imagery",
		description="locate a name in the installed gazetteers and produce an image of the earth at that location",
		optionalArgumentDefaultValues="_",
		optionalArgumentDescriptions="region to locate",
		optionalArgumentTypes="thinklab:Text",
		optionalArgumentNames="location",
		optionNames="r,d",
		optionLongNames="resolution,draw-shape",
		optionTypes="thinklab:Integer,owl:Nothing",
		optionDescriptions="linear resolution of widest side of image (default 800),draw the outline of the region",
		optionArgumentLabels="res, "
)
public class Imagery implements ICommandHandler {

	@Override
	public ISemanticObject execute(Command command, ISession session)
			throws ThinklabException {

		IGazetteer gaz = null;
		int x = 800;
		int y = 800;
		
		if (command.getArgumentAsString("location").equals("_")) {
		
			for (String s : Geospace.get().listKnownFeatures()) {
				session.getOutputStream().println("\t" + s);
			}
			
		} else {
			
			/*
			 * TODO also enable IContext
			 */
			
			String loc = command.getArgumentAsString("location");
			
//			IQueryResult result = 
//				(gaz == null ?
//					Geospace.get().lookupFeature(loc) :
//					gaz.query(gaz.parseQuery(loc)));		
//			
//
//			if (result.getResultCount() > 0) {
//
//				for (int i = 0; i < result.getResultCount(); i++) {
//
//					ShapeValue sh = 
//							(ShapeValue) result.getResultField(i, IGazetteer.SHAPE_FIELD);
//					
//					if (command.hasOption("resolution"))
//						x = command.getOption("resolution").asInteger();
//					
//					Pair<Integer, Integer> xy = 
//						RasterGrid.getRasterBoxDimensions(sh, x);
//					
//					BufferedImage bfi = 
//						command.hasOption("draw-shape") ?
//							GeoImageFactory.get().getImagery(sh, xy.getFirst(), xy.getSecond(), 0) :
//							GeoImageFactory.get().getImagery(sh.getEnvelope(), xy.getFirst(), xy.getSecond());
//					
//					new Picture(bfi).show();
//
//				}
//			} else {
//				session.getOutputStream().println("no results found");
//			}

			
		}
	
		return null;
	}

}
