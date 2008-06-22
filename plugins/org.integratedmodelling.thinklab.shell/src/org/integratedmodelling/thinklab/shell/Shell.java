/**
 * Shell.java
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
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.command.CommandParser;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.impl.protege.FileKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.java.plugin.boot.Application;
import org.java.plugin.boot.ApplicationPlugin;
import org.java.plugin.util.ExtendedProperties;

/**
 * A simple command-line driven knowledge manager. Just run and type 'help'.
 * @author Ferdinando Villa
 */
public class Shell extends ApplicationPlugin implements Application {

    public static final String PLUGIN_ID = "org.integratedmodelling.thinklab.shell";
	
	public ISession session;
	
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

	@Override
	protected Application initApplication(ExtendedProperties arg0, String[] arg1)
			throws Exception {

		getManager().activatePlugin("org.integratedmodelling.thinklab.core");
	
		Thinklab thinklab = (Thinklab) getManager().getPlugin("org.integratedmodelling.thinklab.core");


//        new Help().install(km);
//        new List().install(km);
//        new Load().install(km);
//        new Clear().install(km);
//        new Is().install(km);
//        new Import().install(km);
//        new Eval().install(km);
//        new Hierarchy().install(km);
//        new Query().install(km);
//        new KExport().install(km);
//        new KCopy().install(km);
//        new KImport().install(km);
//        new CMap().install(km);
//        new Find().install(km);
        
		
		return this;
	}

	@Override
	protected void doStart() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doStop() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startApplication() throws Exception {
		
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
					
					IValue result = CommandManager.get().submitCommand(cmd, null, session);
                    if (result != null)
                        System.out.println(result.toString());
				} catch (ThinklabException e) {
					e.printStackTrace();
					System.out.println(" error: " + e.getMessage());
				}
			}
		}
		
	}
}
