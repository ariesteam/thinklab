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
package org.integratedmodelling.dynamicmodelling.commands;

import org.integratedmodelling.dynamicmodelling.DynamicModellingPlugin;
import org.integratedmodelling.dynamicmodelling.interfaces.IModelLoader;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * Command to invoke the parser on a Simile file. It's a temporary command meant
 * for testing only, which will be phased out after model files are seen as
 * sources of knowledge for the load command (and the loadObjects function in
 * sessions).
 * 
 * @author Ferdinando Villa
 * 
 */
public class MDoc implements ICommandHandler {

	public IValue execute(Command command, ISession session) throws ThinklabException {

		String msource = command.getArgumentAsString("m1");
		String loader = "doc";
		IModelLoader l = null;

		if (command.hasOption("loader")) {
			loader = command.getOptionAsString("loader");

			/* look for loader */
			l = DynamicModellingPlugin.get().retrieveModelLoader(loader);

			if (l == null) {
				throw new ThinklabPluginException(
						"no loader registered under name " + loader
								+ " to interpret model");
			}
		}

		// default to documentation loader
		if (l == null) {
			DynamicModellingPlugin.get().retrieveModelLoader(loader);
		}

		l.loadModel(msource);

		return null;
	}

}
