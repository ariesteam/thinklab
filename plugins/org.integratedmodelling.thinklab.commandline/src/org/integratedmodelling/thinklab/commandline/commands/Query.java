/**
 * Query.java
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

import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.utils.Polylist;

/**
 * Performs a query over all the installed kboxes or a specific one.
 */
public class Query implements ICommandHandler {

	public IValue execute(Command command, ISession session) throws ThinklabException {

		String kb = command.getOptionAsString("kbox");
		String toEval = command.getArgumentAsString("query");

		// twisted logics, but I like it.
		for (String kbox : KBoxManager.get().getInstalledKboxes()) {

			IKBox theBox = null;

			if (kb != null) {
				kbox = kb;
				theBox = session.retrieveKBox(kb);
			} else {
				theBox = session.retrieveKBox(kbox);
			}

			IQuery query = theBox.parseQuery(toEval);

			IQueryResult result = theBox.query(query, 0, -1);

			int nres = result.getResultCount();

			if (nres > 0) {

				session.getOutputStream().println("\tID\tClass\tLabel\tDescription");

				for (int i = 0; i < nres; i++) {

					session.getOutputStream().println("\t"
							+ result.getResultField(i,
									IQueryResult.ID_FIELD_NAME)
							+ "\t"
							+ result.getResultField(i,
									IQueryResult.CLASS_FIELD_NAME)
							+ "\t"
							+ result.getResultField(i,
									IQueryResult.LABEL_FIELD_NAME)
							+ "\t"
							+ result.getResultField(i,
									IQueryResult.DESCRIPTION_FIELD_NAME));

				}
			}

			session.getOutputStream().println("total: " + nres);

			// just once if we had a kbox specified
			if (kbox == null)
				break;
		}

		return null;
	}

}
