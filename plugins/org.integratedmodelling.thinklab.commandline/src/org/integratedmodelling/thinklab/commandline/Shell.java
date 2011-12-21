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
package org.integratedmodelling.thinklab.commandline;

import java.io.IOException;

import jline.ConsoleReader;
import jline.Terminal;

import org.apache.commons.logging.Log;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.command.CommandParser;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.utils.MiscUtilities;

/**
 * A simple command-line driven interface. Just attach to a session, startConsole() and type 'help'.
 * @author Ferdinando Villa
 */
public class Shell {
	
	public ISession session;
	ConsoleReader console = null;
	
	public Shell(ISession session) {
		this.session = session;
	}
	
	public Shell() {
		try {
			this.session = new Session();
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}
	
	public void printStatusMessage() throws IOException {
		
		console.printString("ThinkLab shell 0.1alpha\n");
		console.printString("System path: " + LocalConfiguration.getSystemPath() + "\n");
		console.printString("Data path: " + LocalConfiguration.getDataPath() + "\n");					
		console.printString("\n");
		console.printString("Enter \'help\' for a list of commands; \'exit\' quits\n");
		console.printString("\n");
	}

	public void startConsole() throws Exception {
		
		Terminal.setupTerminal();
		this.console = new ConsoleReader();
		
		/* greet user */
		printStatusMessage();
		
		String input = "";
		
		/* define commands from user input */
		while(true) {
			
			input = console.readLine("> ");
		
			if (input == null)
				continue;
			
			if ("exit".equals(input)) {
				console.printString("shell terminated\n");
				break;
			} else if (!("".equals(input))) {
				
				try {
					
					Command cmd = CommandParser.parse(input);
					
					if (cmd == null)
						continue;
					
					IValue result = CommandManager.get().submitCommand(cmd, session);
                    if (result != null)
                    	console.printString(result.toString() + "\n");
				} catch (ThinklabException e) {
					e.printStackTrace();
					console.printString(" error: " + e.getMessage() + "\n");
				}
			}
		}
		
	}

	public static void runScript(String s, ISession session, Log log) throws ThinklabException {

		for (String input : MiscUtilities.readFileIntoStrings(s)) {
			
			input = input.trim();
			if (input == null || input.isEmpty() || input.startsWith("#"))
				continue;
			
			if ("exit".equals(input)) {
				break;
			} else {
				
				try {
					
					Command cmd = CommandParser.parse(input);
					
					if (cmd == null)
						continue;
					
					IValue result = CommandManager.get().submitCommand(cmd, session);
                    if (result != null)
                    	log.info(cmd + " -> " + result.toString());

				} catch (ThinklabException e) {
					log.error("executing " + input, e);
				}
			}
		}
		
	}
}
