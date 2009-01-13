/**
 * KExport.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
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
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.commandline.commands;

import java.io.File;
import java.util.ArrayList;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.extensions.KnowledgeLoader;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.value.ObjectReferenceValue;
import org.integratedmodelling.utils.MiscUtilities;

/**
 * Load ontologies, OPAL files, objects from remote KBoxes into current session
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 */
public class KExport implements ICommandHandler {

	public IValue execute(Command command, ISession session) throws ThinklabException {

		String toload = command.getArgumentAsString("resource");
		String output = command.getArgumentAsString("output");
		String format = command.hasOption("format") ? command
				.getOptionAsString("format") : null;

		/* make sure we have a target format */
		if (format == null)
			format = MiscUtilities.getFileExtension(output);

		ArrayList<IInstance> objs = new ArrayList<IInstance>();

		/*
		 * Locate the plugin that will load the format.
		 */
		KnowledgeLoader writer = KnowledgeManager.get().getKnowledgeLoader(format);

		if (writer == null) {
			throw new ThinklabUnimplementedFeatureException("format " + format
					+ " unrecognized");
		}
		/*
		 * resource can be: an existing instance (#iid), a whole kbox, or an
		 * instance within a kbox. Whatever that is, load it in the array of
		 * instances to output.
		 */
		if (toload.startsWith("#")) {
			objs.add(session.requireObject(toload.substring(1)));
		} else if (toload.contains("#")) {
			objs.add(session.importObject(toload));
		} else {

			IKBox kbox = session.retrieveKBox(toload);

			if (kbox != null) {

				IQueryResult res = kbox.query(null);

				for (int i = 0; i < res.getResultCount(); i++) {

					IValue r = res.getResult(i, session);

					if (r instanceof ObjectReferenceValue) {

						IInstance ii = ((ObjectReferenceValue) r).getObject();
						objs.add(ii);
					}
				}

			} else {
				throw new ThinklabIOException("resource " + toload
						+ " cannot be found");
			}
		}

		if (objs.size() > 0) {

			File outfile = null;
			outfile = new File(output);
			writer.writeKnowledge(outfile, format, objs
					.toArray(new IInstance[objs.size()]));
		}

		session.displayOutput(objs.size() + "  objects written to "
				+ output);

		return null;
	}
}
