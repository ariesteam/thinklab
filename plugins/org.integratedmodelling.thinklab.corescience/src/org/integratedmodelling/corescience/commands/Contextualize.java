/**
 * Contextualize.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCoreSciencePlugin.
 * 
 * ThinklabCoreSciencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCoreSciencePlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.corescience.commands;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.exceptions.ThinklabContextualizationException;
import org.integratedmodelling.corescience.interfaces.IContextualizationWorkflow;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.workflow.DefaultWorkflow;
import org.integratedmodelling.corescience.workflow.debug.DebugWorkflow;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

public class Contextualize implements ICommandHandler {

	public IValue execute(Command command, ISession session) throws ThinklabException {

		String obs = command.getArgumentAsString("c1");
		String wft = command.getArgumentAsString("w");

		IContextualizationWorkflow workflow = null;

		if (wft != null && !wft.equals("_NONE_")) {

			/*
			 * select workflow type and create it to pass to contextualize.
			 * Should use plugins, or just reflection?
			 */
			workflow = CoreScience.get().retrieveWorkflow(wft);

			if (workflow == null) {
				throw new ThinklabPluginException("requested workflow type "
						+ wft + " is unknown. Cannot proceed.");
			}

		} else if (command.hasOption("d")) {
			workflow = new DebugWorkflow();
		} else {
			workflow = new DefaultWorkflow();
		}

		IInstance o = session.requireObject(obs);

		if (!o.is(CoreScience.OBSERVATION)) {
			throw new ThinklabContextualizationException(obs
					+ " is not an observation: cannot contextualize");
		}

		IObservation observation = (IObservation) o.getImplementation();

		IValue ret = observation.contextualize(workflow);

		return ret;
	}

}
