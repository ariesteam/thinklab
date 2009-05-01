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
package org.integratedmodelling.thinklab.commandline;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.command.CommandParser;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.applications.IUserModel;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.owlapi.Session;

import bsh.util.JConsole;

/**
 * A simple command-line driven interface, using the graphical BeanShell console.
 * 
 * @author Ferdinando Villa
 */
public class GraphicalShell {
	
	JConsole console = null;
	
	Font inputFont = new Font("SansSerif", Font.BOLD, 12);
	Font outputFont = new Font("SansSerif", Font.PLAIN, 12);

	public class ConsoleUserModel implements IUserModel {

		@Override
		public InputStream getInputStream() {
			return console.getInputStream();
		}

		@Override
		public PrintStream getOutputStream() {
			return console.getOut();
		}
	}
	
	public class ConsoleSession extends Session {

		public ConsoleSession() throws ThinklabException {
			super();
		}

		@Override
		protected IUserModel createUserModel() {
			return new ConsoleUserModel();
		}
		
	}
	
	public class ConsolePanel extends JFrame {

		private static final long serialVersionUID = -1303258585100820402L;

		public ConsolePanel() {
		    super("Thinklab console");
		    Container content = getContentPane();
		    content.setBackground(Color.lightGray);
		    JPanel controlArea = new JPanel(new GridLayout(2, 1));
		    content.add(controlArea, BorderLayout.EAST);
		    console = new JConsole();
		    console.setFont(outputFont);
		    // Preferred height is irrelevant, since using WEST region
		    console.setPreferredSize(new Dimension(600, 400));
		    console.setBorder(BorderFactory.createLineBorder (Color.blue, 2));
		    console.setBackground(Color.white);
		    content.add(console, BorderLayout.WEST);
		    pack();
		    setVisible(true);
		  }
		}
	
	public ISession session;
	
	public GraphicalShell() throws ThinklabException {
		this.session = new ConsoleSession();
	}
	
	public  void printStatusMessage() {
		
		console.println("ThinkLab shell v" + CommandLine.get().getDescriptor().getVersion());
		console.println("System path: " + LocalConfiguration.getSystemPath());
		console.println("Data path: " + LocalConfiguration.getDataPath());					
		console.println();
		
		console.println("Enter \'help\' for a list of commands; \'exit\' quits");
		console.println();
	}

	
	public static String readLine(InputStream stream) throws ThinklabIOException {
		String ret = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		try {
			ret = reader.readLine();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		return ret;
	}
	
	public void startConsole() throws Exception {
				
		ConsolePanel jpanels = new ConsolePanel();
		
		/* greet user */
		printStatusMessage();
		
		String input = "";
		
		/* define commands from user input */
		while(true) {
			
			console.print("> ");
			console.setStyle(inputFont);
			
			input = readLine(session.getInputStream());

			console.setStyle(outputFont);
			
			if ("exit".equals(input)) {
				console.println("shell terminated");
				System.exit(0);
				break;
			} else if (!("".equals(input.trim())) && /* WTF? */!input.equals(";")) {
				try {
					
					Command cmd = CommandParser.parse(input);
					
					if (cmd == null)
						continue;
					
					IValue result = CommandManager.get().submitCommand(cmd, session);
                    if (result != null)
                        console.println(result.toString());
				} catch (ThinklabException e) {
					e.printStackTrace();
					console.println(" error: " + e.getMessage());
				}
			}
		}
		
	}
}
