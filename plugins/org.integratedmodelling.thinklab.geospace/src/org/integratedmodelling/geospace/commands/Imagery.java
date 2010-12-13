package org.integratedmodelling.geospace.commands;

import java.awt.image.BufferedImage;

import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.geospace.interfaces.IGazetteer;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.geospace.visualization.GeoImageFactory;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.image.Picture;

@ThinklabCommand(
		name="imagery",
		description="locate a name in the installed gazetteers and produce an image of the earth at that location",
		optionalArgumentDefaultValues="_",
		optionalArgumentDescriptions="region to locate",
		optionalArgumentTypes="thinklab-core:Text",
		optionalArgumentNames="location",
		optionNames="r,d",
		optionLongNames="resolution,draw-shape",
		optionTypes="thinklab-core:Integer,owl:Nothing",
		optionDescriptions="linear resolution of widest side of image (default 800),draw the outline of the region",
		optionArgumentLabels="res, "
)
public class Imagery implements ICommandHandler {

	@Override
	public IValue execute(Command command, ISession session)
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
			
			IQueryResult result = 
				(gaz == null ?
					Geospace.get().lookupFeature(loc) :
					gaz.query(gaz.parseQuery(loc)));		
			

			if (result.getResultCount() > 0) {

				for (int i = 0; i < result.getResultCount(); i++) {

					ShapeValue sh = 
							(ShapeValue) result.getResultField(i, IGazetteer.SHAPE_FIELD);
					
					if (command.hasOption("resolution"))
						x = command.getOption("resolution").asNumber().asInteger();
					
					Pair<Integer, Integer> xy = 
						RasterGrid.getRasterBoxDimensions(sh, x);
					
					BufferedImage bfi = 
						command.hasOption("draw-shape") ?
							GeoImageFactory.get().getImagery(sh, xy.getFirst(), xy.getSecond()) :
							GeoImageFactory.get().getImagery(sh.getEnvelope(), xy.getFirst(), xy.getSecond());
					
					new Picture(bfi).show();

				}
			} else {
				session.getOutputStream().println("no results found");
			}

			
		}
	
		return null;
	}

}
