/**
 * Olist.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Mar 25, 2008
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
 * @date      Mar 25, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.corescience.commands;

import java.util.HashSet;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.exceptions.ThinklabContextualizationException;
import org.integratedmodelling.corescience.observation.IObservation;
import org.integratedmodelling.corescience.observation.IObservationState;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

public class Olist implements ICommandHandler {
		
		private void dumpObservation(IObservation o, String prefix, ISession out, HashSet<String> ids) throws ThinklabException {
			
			if (ids == null)
				ids = new HashSet<String>();
			
			if (ids.contains(o.getObservationInstance().toString())) {
				out.displayOutput(prefix + "(" + o.getObservationInstance().toString() + ")");
				return;
			}
			
			ids.add(o.getObservationInstance().toString());
			
			out.displayOutput(
					prefix +
					o.getObservationInstance().getLocalName() + 
					" (a " +
					o.getObservationClass() +
					") observes " +
					o.getObservable() + 
					" (a " + 
					o.getObservableClass() + 
					")");
			
			/*
			 * TODO dump context
			 */
			
			/*
			 * TODO dump state
			 */
			IObservationState state = o.getObservationState();
			if (state != null) {
				double[] data = state.getDataAsDouble();
				out.displayOutput(prefix + "State:");
				for (int i = 0; i < data.length; i++) {
					out.appendOutput(data[i] + " ");
					if ((i % 100) == 0)
						out.displayOutput("");
				}
			}
			
			int i = 0;
			for (IObservation dep : o.getContingencies()) {
				if (i++ == 0)
					out.displayOutput(prefix + "Contingent to:");

				dumpObservation(dep, prefix + "  ", out, ids);
			}

			i = 0;
			for (IObservation dep : o.getDependencies()) {

				if (i++ == 0)
					out.displayOutput(prefix + "Dependent on:");

				dumpObservation(dep, prefix + "  ", out, ids);
			}

		}
		

		public IValue execute(Command command, ISession session) throws ThinklabException {
			
			String obs = command.getArgumentAsString("observation");
			IInstance o = session.requireObject(obs);

			if (!o.is(CoreScience.OBSERVATION)) {
				throw new ThinklabContextualizationException(obs + " is not an observation: cannot contextualize");
			}
			
			IObservation observation = (IObservation)o.getImplementation();

			dumpObservation(observation, "", session, null);
			
			return null;
		}
		
	}
	
