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
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.thinklab.thinkql.REPL;

/**
 * Run an application configured in plugin.xml, or even pass a list to run inline
 *  
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 * 
 */
@ThinklabCommand(name="tql", description="start a Thinklab query language REPL")
public class ThinkQL implements ICommandHandler {

	public IValue execute(Command command, ISession session) throws ThinklabException {

		IValue ret = null;
		String arg = null;
		ThinklabPlugin plugin = null;

		if (command.hasOption("context")) {

			String contextplugin = command.getOptionAsString("context");
			plugin = Thinklab.resolvePlugin(contextplugin, true);
		}

		
		if (!command.hasArgument("resource")) {
			
			if (session.getInputStream() == null) {
				/* not interactive: just ignore command */
				Thinklab.get().logger().warn("Clojure interpreter invoked by a non-interactive application");
				return null;
			}
			
		} else {

			arg = command.getArgumentAsString("resource");
		}

		try {
			

			
			if (arg == null) {
				
				REPL repl = new REPL();
				repl.setInput(session.getInputStream());
				repl.setOutput(session.getOutputStream());
				repl.setSession(session);
				repl.run(null);
				
			} 
			
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		
		return ret;
	}
}