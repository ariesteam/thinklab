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
package org.integratedmodelling.thinklab.commandline.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;

/**
 * Load ontologies, OPAL files, objects from remote KBoxes into current session
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 */

public class Load implements ICommandHandler {
	
	public IValue execute(Command command, ISession session) throws ThinklabException {

		String toload = command.getArgumentAsString("resource");
		String kbox = command.getOptionAsString("kbox");

		Collection<IInstance> objs = null;
		ArrayList<String> kids = null;

		if (kbox != null && objs.size() > 0) {

//			IKBox kb = session.retrieveKBox(kbox);
//			kids = new ArrayList<String>();
//
//			HashMap<String, String> references = new HashMap<String, String>();
//
//			for (IInstance obj : objs) {
//				kids.add(kb.storeObject(obj, null, null, session, references));
//			}
		}

		session.getOutputStream().println((objs == null ? 0 : objs.size())
				+ " main objects loaded from " + toload
				+ (kbox == null ? "" : " [stored to kbox: " + kbox + "]"));

		if (objs != null) {
			int cnt = 0;
			for (IInstance obj : objs) {

				session.getOutputStream().println("\t#"
						+ obj.getLocalName()
						+ (kids == null ? "" : ("\t-> " + kbox + "#" + kids
								.get(cnt++))));
			}
		}

		return null;
	}

}
