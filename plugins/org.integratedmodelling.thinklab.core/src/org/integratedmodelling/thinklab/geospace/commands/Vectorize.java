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

import java.net.URL;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticLiteral;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.geospace.coverage.CoverageFactory;
import org.integratedmodelling.thinklab.geospace.coverage.RasterCoverage;
import org.integratedmodelling.thinklab.geospace.coverage.VectorCoverage;
import org.integratedmodelling.thinklab.geospace.gis.ThinklabVectorizer;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.utils.MiscUtilities;

/**
 * Load ontologies, OPAL files, objects from remote KBoxes into current session
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 */
public class Vectorize implements ICommandHandler {

	public ISemanticLiteral execute(Command command, ISession session) throws ThinklabException {

		String toload = command.getArgumentAsString("resource");
		// String output = command.getArgumentAsString("output");

		URL theUrl = MiscUtilities.getURLForResource(toload);

		RasterCoverage rCoverage = (RasterCoverage) CoverageFactory
				.requireCoverage(theUrl, null);

		// todo remove or condition to an option
		rCoverage.show();

		VectorCoverage vCoverage = new ThinklabVectorizer().vectorize(
				rCoverage, null);

		// TODO we should obviously endeavor to save it if an output arg is
		// passed.
		vCoverage.show();

		return null;
	}
}
