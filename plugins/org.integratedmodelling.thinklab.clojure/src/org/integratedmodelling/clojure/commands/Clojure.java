/**
 * Clear.java
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
package org.integratedmodelling.clojure.commands;

import org.integratedmodelling.clojure.ClojurePlugin;
import org.integratedmodelling.clojure.REPL;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.extensions.CommandHandler;
import org.integratedmodelling.thinklab.interfaces.ICommandInputProvider;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;

/**
 * Run an application configured in plugin.xml, or even pass a list to run inline
 *  
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 * 
 */
public class Clojure implements CommandHandler {

	public IValue execute(Command command, ICommandInputProvider inputSource,
			ICommandOutputReceptor outputDest, ISession session, KnowledgeManager km) throws ThinklabException {

		IValue ret = null;
		String[] args = null;
		
		if (!command.hasArgument("resource")) {
			
			if (inputSource == null) {
				/* not interactive: just ignore command */
				ClojurePlugin.get().logger().warn("Clojure interpreter invoked by a non-interactive application");
				return null;
			}
			
			args = new String[0];
			
		} else {

			args = new String[1];
			args[0] = command.getArgumentAsString("resource");
		}

		try {
			
			REPL repl = new REPL();
			
			repl.setInput(inputSource.getInputStream());
			repl.setOutput(outputDest.getOutputStream());
			repl.setSession(session);
			
			repl.run(args);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
}
