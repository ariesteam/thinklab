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
package org.integratedmodelling.thinklab.commandline.commands;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.kbox.KBoxManager;

/**
 * Performs a query over all the installed kboxes or a specific one.
 */
public class Query implements ICommandHandler {

	public IValue execute(Command command, ISession session) throws ThinklabException {

		String kb = command.getOptionAsString("kbox");
		String toEval = command.getArgumentAsString("query");

//		// twisted logics, but I like it.
//		for (String kbox : KBoxManager.get().getInstalledKboxes()) {

			IKBox theBox = KBoxManager.get();

//			if (kb != null) {
//				kbox = kb;
//				theBox = session.retrieveKBox(kb);
//			} else {
//				theBox = session.retrieveKBox(kbox);
//			}

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
//			if (kbox == null)
//				break;
//		}

		return null;
	}

}
