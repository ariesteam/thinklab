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
package org.integratedmodelling.thinklab.commands;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandPattern;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IAction;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.IKBox;
import org.integratedmodelling.thinklab.interfaces.IQuery;
import org.integratedmodelling.thinklab.interfaces.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.utils.Polylist;

/**
 * Performs a query over all the installed kboxes or a specific one.
 */
public class Query extends CommandPattern {

	class SearchAction implements IAction {

		public IValue execute(Command command, ICommandOutputReceptor outputWriter, ISession session, KnowledgeManager km) throws ThinklabException {
			
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
				
				Polylist schema = Polylist.list(
						IQueryResult.ID_FIELD_NAME,
						IQueryResult.CLASS_FIELD_NAME,
						IQueryResult.LABEL_FIELD_NAME, 
						IQueryResult.DESCRIPTION_FIELD_NAME);
				
				IQueryResult result = theBox.query(query, schema, 0, -1);

				int nres = result.getResultCount();
				
				if (nres > 0) {
					
					outputWriter.displayOutput("\tID\tClass\tLabel\tDescription"); 

					for (int i = 0; i < nres; i++) {

						outputWriter.displayOutput(
								"\t" + 
								result.getResultField(i, IQueryResult.ID_FIELD_NAME) +
								"\t" +
								result.getResultField(i, IQueryResult.CLASS_FIELD_NAME) +
								"\t" +
								result.getResultField(i, IQueryResult.LABEL_FIELD_NAME) +
								"\t" +
								result.getResultField(i, IQueryResult.DESCRIPTION_FIELD_NAME));

					}				
				}
				
				outputWriter.displayOutput("total: " + nres);

				// just once if we had a kbox specified
				if (kbox == null)
					break;
			}
			
			
			return null;
		}
		
	}

	public Query() {
		super();
	}

	@Override
	public CommandDeclaration createCommand() throws ThinklabException {
		
		CommandDeclaration ret = new CommandDeclaration("query", "query one or more kboxes using a constraint");
		ret.addOption("k", "kbox", 
					  "a specific kbox to search. If not provided, all installed kboxes are searched.", 
					  "",
					  KnowledgeManager.Text().getSemanticType());
		
		ret.addMandatoryArgument("query", "the query: a constraint or other query that works with the kbox",
								 KnowledgeManager.Text().getSemanticType());

		return ret;
	}

	@Override
	public IAction createAction() {
		return new SearchAction();
	}

}
