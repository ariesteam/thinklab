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
package org.integratedmodelling.afl.commands;

import org.integratedmodelling.afl.Application;
import org.integratedmodelling.afl.AFLPlugin;
import org.integratedmodelling.afl.Interpreter;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.extensions.CommandHandler;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.utils.Polylist;

/**
 * Run an application configured in plugin.xml, or even pass a list to run inline
 *  
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 * 
 */
public class Run implements CommandHandler {

	public IValue execute(Command command, ICommandOutputReceptor outputDest,
			ISession session, KnowledgeManager km) throws ThinklabException {

		String appn = command.getArgumentAsString("application");
		
		if (appn.trim().startsWith("(")) {
			throw new ThinklabUnimplementedFeatureException("inline applications not supported yet");
		}
		
		Application app = AFLPlugin.get().getApplication(appn);
		
		if (app == null) {
			throw new ThinklabResourceNotFoundException("application " + appn + " is not defined");
		}
		
		Interpreter interp = app.getInterpreter();
		
		Polylist ret = null;
		if (command.hasOption("debug"))
			ret = interp.run_debug();
		else
			ret = interp.run();
		
		outputDest.displayOutput("  -> " + ret);
		
		return null;
	}
}
