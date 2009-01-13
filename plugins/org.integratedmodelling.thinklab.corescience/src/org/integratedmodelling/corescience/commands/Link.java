/**
 * Link.java
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

import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IConformance;

/**
 * The link command should take two observations as parameters, ensure their
 * observables are conformant (possibly allowing to use a specified conformance
 * policy, creating a default one if not) and if so, create a link between the
 * two, so that the next contextualization will use the linked observation and
 * mediate as needed.
 * 
 * @author Ferdinando Villa
 * 
 */
public class Link implements ICommandHandler {

	public IValue execute(Command command, ISession session) throws ThinklabException {

		String obs1 = command.getArgumentAsString("c1");
		String obs2 = command.getArgumentAsString("c1");
		String cnf = command.getArgumentAsString("conformance");

		IInstance o1 = session.requireObject(obs1);
		IInstance o2 = session.requireObject(obs2);

		if (o1.getImplementation() == null
				|| !(o1.getImplementation() instanceof IObservation))
			throw new ThinklabValidationException(obs1
					+ " is not an observation");

		if (o2.getImplementation() == null
				|| !(o2.getImplementation() instanceof IObservation))
			throw new ThinklabValidationException(obs2
					+ " is not an observation");

		IKnowledgeSubject observable1 = ((IObservation) o1.getImplementation())
				.getObservable();
		IKnowledgeSubject observable2 = ((IObservation) o2.getImplementation())
				.getObservable();

		IConformance conformance = null;

		if (!cnf.equals("_NONE_")) {

			// TODO get conformance policy from session and attach to
			// observable1

		} else {

			// TODO create default conformance policy for observable1
		}

		/* TODO use conformance policy to ensure that observable2 conforms */

		/* insert equivalence between the observations */
		session.linkObjects(o1, o2);

		return null;
	}

}
