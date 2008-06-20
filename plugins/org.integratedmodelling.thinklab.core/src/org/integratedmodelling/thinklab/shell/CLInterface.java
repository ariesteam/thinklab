/**
 * CLInterface.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandParser;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.impl.Session;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeInterface;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;


/**
 * @author Ferdinando Villa
 * @author Ioannis N. Athanasiadis
 */
public class CLInterface implements IKnowledgeInterface {
	
	public ISession session;
	public ICommandOutputReceptor cout = new ShellCommandOutputReceptor();

	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.IKnowledgeInterface#RegisterCommand(org.integratedmodelling.ima.core.CommandDeclaration)
	 */
	public void registerCommand(CommandDeclaration declaration) throws ThinklabException {
		// TODO Auto-generated method stub   
	}
	
	public void printStatusMessage() {
		
		try {
			KnowledgeManager.get().printBanner();
		} catch (ThinklabNoKMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Enter \'help\' for a list of commands; \'exit\' quits");
		System.out.println();
	}
	
	/**
	 * Collect commands from stdin and pass them to KM to execute.
	 */
	public void start() {
		
		/* greet user */
		printStatusMessage();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		
		/* define commands from user input */
		while(true) {
			
			System.out.print("> ");
			try {
				input = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if ("exit".equals(input)) {
				System.out.println("shell terminated");
				break;
			} else if (!("".equals(input))) {
				try {
	
					// Command cmd = new Command(input);
					Command cmd = CommandParser.parse(input);
					
					if (cmd == null)
						continue;
					
					IValue result = KnowledgeManager.get().submitCommand(cmd, cout, getCurrentSession());
                    if (result != null)
                        System.out.println(result.toString());
				} catch (ThinklabException e) {
					e.printStackTrace();
					System.out.println(" error: " + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Constructor takes the command line arguments as parameters
	 * @param args
	 */
	public CLInterface(String[] args) {
		
	}

    public void importPreferences() {

    }

    public void savePreferences() {
    }

	public ISession createNewSession() throws ThinklabException {
		return new Session();
	}

	public void notifySessionDeletion(ISession session) {
		// TODO Auto-generated method stub
		
	}

	public ISession getCurrentSession() throws ThinklabException {
		
		if (session == null) {
			try {
				// create one session. This is sent back to us so it's a bit crooked. May deserve some
				// more thought, but not a big deal for now.
				session = KnowledgeManager.get().requestNewSession();
			} catch (ThinklabNoKMException e1) {
				e1.printStackTrace();
			} catch (ThinklabException e1) {
				e1.printStackTrace();
			}
		}
		return session;
	}
    
	
	void clearSession(ISession session) {
		
		/* just delete it */
		session = null;
	}
      // TODO save system preferences somewhere
}
