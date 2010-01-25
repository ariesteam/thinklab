/**
 * GISToOPAL.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabGeospacePlugin.
 * 
 * ThinklabGeospacePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabGeospacePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.commands;

import java.io.File;
import java.net.URL;

import org.integratedmodelling.geospace.coverage.RasterCoverage;
import org.integratedmodelling.geospace.coverage.VectorCoverage;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.gis.ThinklabRasterizer;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.MiscUtilities;

/**
 * Load ontologies, OPAL files, objects from remote KBoxes into current session
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 */
public class Rasterize implements ICommandHandler {

	public IValue execute(Command command, ISession session) throws ThinklabException {

		String toload = command.getArgumentAsString("resource");
		// String output = command.getArgumentAsString("output");
		int xCells = command.getArgument("xCells").asNumber().asInteger();
		int yCells = command.getArgument("yCells").asNumber().asInteger();

		String attrName = command.getOptionAsString("attribute", "the_value");
		String output = command.getOptionAsString("output", null);
		float nodata = (float)command.getOptionAsDouble("n", Float.NaN);
		
		URL theUrl = MiscUtilities.getURLForResource(toload);

		VectorCoverage vCoverage = new VectorCoverage(theUrl, attrName, false);

		// todo remove or condition to an option
		vCoverage.show();

		GridExtent extent = new GridExtent(vCoverage
				.getCoordinateReferenceSystem(), vCoverage.getLonLowerBound(),
				vCoverage.getLatLowerBound(), vCoverage.getLonUpperBound(),
				vCoverage.getLatUpperBound(), xCells, yCells);

		RasterCoverage rCoverage = ThinklabRasterizer.rasterize(vCoverage,
				attrName, nodata, extent, null, null);

		// TODO we should obviously endeavor to save it if an output arg is
		// passed.
		rCoverage.show();
		
		if (output != null) {
			File f = new File(output);
			rCoverage.write(f);
		}

		return null;
	}
}
