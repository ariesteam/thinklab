/**
 * KImport.java
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
package org.integratedmodelling.thinklab.shell;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandPattern;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IAction;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.utils.MiscUtilities;

/**
 * Load ontologies, OPAL files. 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 */
public class KImport extends CommandPattern {

	class LoadAction implements IAction {

		public IValue execute(Command command, ICommandOutputReceptor outputWriter, ISession session, KnowledgeManager km) throws ThinklabException {
			
			// TODO only handle ontologies for now
			String toload = command.getArgumentAsString("resource");
			String name = command.hasOption("name") ? command.getOptionAsString("name") : null;
			
			if (name == null)
				name = MiscUtilities.getNameFromURL(toload);
			
			name = km.registerOntology(toload, name);
			
			outputWriter.displayOutput("ontology " + name + " loaded");
			
			return null;
		}
		
	}

	public KImport( ) {
		 super();
	}

	@Override
	public CommandDeclaration createCommand() {
		CommandDeclaration ret = new CommandDeclaration("kimport", "import knowledge into a kbox from external sources");
		try {
			ret.addMandatoryArgument("resource", "filename or URL to load",
					KnowledgeManager.get().getTextType().getSemanticType());
			ret.addMandatoryArgument("kbox", "ID or URL of the destination kbox",
					KnowledgeManager.get().getTextType().getSemanticType());
		} catch (ThinklabException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public IAction createAction() {
		return new LoadAction();
	}

}
