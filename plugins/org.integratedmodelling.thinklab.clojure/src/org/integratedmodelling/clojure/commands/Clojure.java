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
package org.integratedmodelling.clojure.commands;

import java.net.URL;

import org.integratedmodelling.clojure.ClojureInterpreter;
import org.integratedmodelling.clojure.ClojurePlugin;
import org.integratedmodelling.clojure.REPL;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabScriptException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.Value;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

/**
 * Run an application configured in plugin.xml, or even pass a list to run inline
 *  
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 * 
 */
public class Clojure implements ICommandHandler {

	public IValue execute(Command command, ISession session) throws ThinklabException {

		IValue ret = null;
		String arg = null;
		ThinklabPlugin plugin = null;
		ClassLoader cloader = null;

		if (command.hasOption("context")) {

			String contextplugin = command.getOptionAsString("context");
			plugin = Thinklab.resolvePlugin(contextplugin, true);
			cloader = plugin.getClassLoader();
		}

		
		if (!command.hasArgument("resource")) {
			
			if (session.getInputStream() == null) {
				/* not interactive: just ignore command */
				ClojurePlugin.get().logger().warn("Clojure interpreter invoked by a non-interactive application");
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
				repl.setClassloader(cloader);
				repl.run(null);
				
			} else {
				
				URL url = Thinklab.get().getResourceURL(arg, plugin);
				ClojureInterpreter intp = new ClojureInterpreter();
				intp.setInput(session.getInputStream());
				intp.setOutput(session.getOutputStream());
				ret = Value.getValueForObject(intp.eval(url));
			}
			
		} catch (Exception e) {
			throw new ThinklabScriptException(e);
		}
		
		return ret;
	}
}
