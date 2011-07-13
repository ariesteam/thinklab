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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.api.runtime.IUserModel;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.command.CommandParser;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.owlapi.Session;

import bsh.util.JConsole;

/**
 * A simple command-line driven interface, using the graphical BeanShell console.
 * 
 * @author Ferdinando Villa
 */
public class GraphicalShell {
	
	JConsole console = null;
	
	File historyFile = null;
	
	Font inputFont = new Font("Courier", Font.BOLD, 12);
	Font outputFont = new Font("Courier", Font.PLAIN, 12);

	public class ConsoleUserModel implements IUserModel {

		Properties properties = null;
		
		@Override
		public InputStream getInputStream() {
			return console.getInputStream();
		}

		@Override
		public PrintStream getOutputStream() {
			return console.getOut();
		}

		@Override
		public void initialize(ISession session) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void setProperties(Properties uprop) {
			properties = uprop;
		}

		@Override
		public Properties getProperties() {

			if (properties == null)
				properties = new Properties();
			
			return properties;
		}

		@Override
		public IInstance getUserInstance() {
			// TODO Auto-generated method stub
			return null;
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

	private boolean error;
	
	public GraphicalShell() throws ThinklabException {
		this.session = new ConsoleSession();
		
		historyFile = 
			new File(
				CommandLine.get().getScratchPath() + 
				File.separator + 
				".history");
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
		
		/*
		 * read history if any
		 */
		List<?> lines = null;
		try {
			lines = FileUtils.readLines(historyFile, null);
		} catch (IOException e) {
			// no problem
		}
		
		if (lines != null) {
			for (Object line : lines) {
				console.addToHistory(line.toString());
			}
		}
		
		
		/* greet user */
		printStatusMessage();

		String input = "";
		
		/* define commands from user input */
		while(true) {
			
			console.print("> ");
			console.setStyle(inputFont);
			
			input = readLine(session.getInputStream()).trim();
			
			console.setStyle(outputFont);
			
			if ("exit".equals(input)) {
				
				console.println("shell terminated");
				System.exit(0);
				break;
				
			} else if (input.startsWith("!")) {
				
				String ss = input.substring(1);
				for (int i = console.getHistory().size(); i > 0; i--) {
					String s = console.getHistory().get(i-1);
					if (s.startsWith(ss)) {
						console.println(s);
						execute(s);
						break;
					}
				}
				
			} else if (!("".equals(input)) && /* WTF? */!input.equals(";")) {
				
				execute(input);
				
				// TODO see if we want to exclude commands that created errors.
				if (/*!error*/true) {
			          BufferedWriter bw = null;
				      try {
				        	 bw = new BufferedWriter(
				        			  new FileWriter(historyFile, true));
				          bw.write(input.trim());
				          bw.newLine();
				          bw.flush();
				       } catch (IOException ioe) {
				       } finally {
				 	 if (bw != null) 
				 		 try {
				 			 bw.close();
				 	 	} catch (IOException ioe2) {
				 	 	}
				    }
				}
			}
		}
	}

	private void execute(String input) {

		try {
			this.error = false;
			
			Command cmd = CommandParser.parse(input);
			
			if (cmd == null)
				return;
			
			if (cmd.isVerbose())
				session.pushVariable(ISession.INFO, Boolean.TRUE);
			if (cmd.isDebug())
				session.pushVariable(ISession.DEBUG, Boolean.TRUE);
			
			session.pushVariable(ISession.COMMAND, cmd);
			
			IValue result = CommandManager.get().submitCommand(cmd, session);

			session.popVariable(ISession.COMMAND);
			
			if (cmd.isDebug())
				session.popVariable(ISession.DEBUG);
			if (cmd.isVerbose())
				session.popVariable(ISession.INFO);
			
            if (result != null)
                console.println(result.toString());
            
            console.getOut().flush();

		} catch (Exception e) {
			
			e.printStackTrace();
			this.error = true;
			console.setStyle(Color.red);
			console.println("  " + e.getMessage());
			console.setStyle(Color.black);
		}

        /*
         *  give it a little rest to help the output show entirely before the prompt
         *  is printed again.
         */
		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {
		}
	}
}
