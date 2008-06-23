/**
 * Load.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandPattern;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IAction;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IKBox;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;

/**
 * Load ontologies, OPAL files, objects from remote KBoxes into current session
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 */
public class Load extends CommandPattern {

	class LoadAction implements IAction {

		public IValue execute(Command command, ICommandOutputReceptor outputWriter, ISession session, KnowledgeManager km) throws ThinklabException {
			
			String toload = command.getArgumentAsString("resource");
			String kbox = command.getOptionAsString("kbox");

			Collection<IInstance> objs = null;
			ArrayList<String> kids = null;
			
			if (toload.contains("#")) {
				
				/* kbox or other, load from wherever KM figures out */
				objs = new ArrayList<IInstance>();
				
				IInstance i = km.getInstanceFromURI(toload, session);
				if (i != null) {
					objs.add(i);
				}
				
			} else {
				objs = session.loadObjects(toload);
			}
			
			// TODO move these functionalities to kimport; implement the virtual kbox for 
			// loaded sources.
			if (kbox != null && objs.size() > 0) {
				
				IKBox kb = session.retrieveKBox(kbox);
				kids = new ArrayList<String>();
				
				HashMap<String, String> references = new HashMap<String, String>();
				
				for (IInstance obj : objs) {
					kids.add(kb.storeObject(obj, session, references));
				}
			}
			
			outputWriter.displayOutput(
					(objs == null ? 0 : objs.size()) + 
					" main objects loaded from " + 
					toload +
					(kbox == null ? "" : " [stored to kbox: " + kbox + "]"));
			
			if (objs != null) {
				int cnt = 0;
				for (IInstance obj : objs) {

					outputWriter.displayOutput(
							"\t#" + obj.getLocalName() + 
							(kids == null ? "" : 
								("\t-> " + kbox + "#" + kids.get(cnt++))));
				}
			}

			return null;
		}
		
	}

	public Load( ) {
		super();
	}

	@Override
	public CommandDeclaration createCommand() {
		CommandDeclaration ret = new CommandDeclaration("load", "load knowledge from external sources into current session");
		try {
			ret.addMandatoryArgument("resource", "filename or URL to load", 
					KnowledgeManager.get().getTextType().getSemanticType());
			ret.addOption("k", "kbox", "<knowledge box>", "the URL of a knowledge box to load to",
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
