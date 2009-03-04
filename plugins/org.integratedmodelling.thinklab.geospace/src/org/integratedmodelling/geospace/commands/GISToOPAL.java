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

import org.integratedmodelling.geospace.coverage.InstanceCoverageExporter;
import org.integratedmodelling.geospace.feature.InstanceShapefileExporter;
import org.integratedmodelling.opal.utils.OPALListWriter;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.XMLDocument;

/**
 * Load ontologies, OPAL files, objects from remote KBoxes into current session
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 */
public class GISToOPAL implements ICommandHandler {

	public IValue execute(Command command, ISession session) throws ThinklabException {

		String toload = command.getArgumentAsString("resource");
		String output = command.getArgumentAsString("output");
		String format = command.hasOption("profile") ? command
				.getOptionAsString("profile") : null;

		XMLDocument document = OPALListWriter.getNewDocument(format);

		/*
		 * FIXME must use recognition of formats from GeospacePlugin. Also,
		 * endsWith is inappropriate for server URLS or anything with GET
		 * parameters.
		 * 
		 * for now just use a switch over supported formats
		 */
		if (toload.endsWith(".shp")) {

			InstanceShapefileExporter exporter = new InstanceShapefileExporter(
					MiscUtilities.getURLForResource(toload), document, format);

			int nObjects = exporter.process();

			document.writeToFile(new File(output));

			session.getOutputStream().println(nObjects + " objects written to "
					+ output);

		} else if (toload.endsWith(".tif") || toload.endsWith(".tiff")) {

			/*
			 * FIXME this one actually handles other raster formats as well
			 */

			InstanceCoverageExporter exporter = new InstanceCoverageExporter(
					MiscUtilities.getURLForResource(toload), document, format);

			int nObjects = exporter.process();

			document.writeToFile(new File(output));

			session.getOutputStream().println(nObjects + " objects written to "
					+ output);

		} else {
			throw new ThinklabUnimplementedFeatureException("file " + toload
					+ " uses an unsupported format");
		}

		return null;
	}
}
