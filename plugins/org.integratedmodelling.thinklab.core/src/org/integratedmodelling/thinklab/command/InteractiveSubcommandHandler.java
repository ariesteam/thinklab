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
package org.integratedmodelling.thinklab.command;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * Something to derive from to implement a sub-command system. Automates the handling of
 * subcommands, while InteractiveCommand just gives the option of doing so. It automatically 
 * goes into a read-eval-print loop until user says exit. Uses a virtual to react to each 
 * subcommand.
 * 
 * @author Ferdinando
 *
 */
public abstract class InteractiveSubcommandHandler extends InteractiveCommandHandler {
	
	/**
	 * This will be passed a subcommand and a set of arguments (arg[0] is the subcommand again,
	 * like in C's argv). The "exit" subcommand will exit the loop and should not be intercepted.
	 * 
	 * @param cmd
	 * @param arguments
	 * @return
	 * @throws ThinklabException
	 */
	protected abstract IValue cmd(String cmd, String[] arguments) throws ThinklabException;

	@Override
	public IValue doInteractive(Command command, ISession session)
			throws ThinklabException {

		String cm = null;
		IValue ret = null;
		do {
			cm = prompt();
			if (cm == null)
				continue;
			if (cm.equals("exit")) {
				break;
			}
			if (!cm.trim().equals("")) {
				String[] cmm = cm.split(" ");
				ret = cmd(cmm[0], cmm);
			}
		} while (true);
		
		return ret;
	}

}
