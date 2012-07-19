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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.interpreter.ModelGenerator;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IResolver;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.command.CommandParser;
import org.integratedmodelling.thinklab.modelling.ModelManager;
import org.integratedmodelling.thinklab.proxy.ModellingModule;
import org.integratedmodelling.thinklab.session.InteractiveSession;

import bsh.util.JConsole;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * A simple command-line driven interface, using the graphical BeanShell console.
 * 
 * @author Ferdinando Villa
 */
public class GraphicalTQLShell {
	
	private static final String USER_DEFAULT_NAMESPACE = "user";
	
	static final String LANGUAGE_NAME = "k*";
	
	JConsole console = null;
	
	File historyFile = null;
	
	Font inputFont = new Font("Courier", Font.BOLD, 12);
	Font outputFont = new Font("Courier", Font.PLAIN, 12);
	public class ConsolePanel extends JFrame {

		private static final long serialVersionUID = -1303258585100820402L;

		public ConsolePanel() {
		    super("Thinklab TQL shell");
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
	
	public GraphicalTQLShell() throws ThinklabException {
		
		historyFile = 
			new File(
				Thinklab.get().getScratchArea() + 
				File.separator + 
				".tqlhistory");
	}
	
	public  void printStatusMessage() {
		
		console.println("ThinkLab shell v" + Thinklab.get().getVersion());
		console.println("System path: " + Thinklab.get().getLoadPath(null));
		console.println("Workspace: " + Thinklab.get().getWorkspace());					
		console.println();
		
		console.println("Enter " + LANGUAGE_NAME + " statements or dot-commands; \'.help\' lists commands; \'.exit\' quits");
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
		 * Prepare interpreter
		 */
		Injector injector = Guice.createInjector(new ModellingModule());
		ModelGenerator mg = injector.getInstance(ModelGenerator.class);
		
		PrintStream w = new PrintStream(console.getOut());

		session = new InteractiveSession(console.getInputStream(), w);
		
		/*
		 * read history if any
		 */
		List<?> lines = null;
		try {
			lines = FileUtils.readLines(historyFile);
		} catch (IOException e) {
			// no problem
		}
		
		if (lines != null) {
			for (Object line : lines) {
				console.addToHistory(line.toString());
			}
		}
		
		IResolver resolver = 
				((ModelManager)Thinklab.get().getModelManager()).getInteractiveResolver(console.getInputStream(), w);
		
		/* greet user */
		printStatusMessage();

		try {
	
			String prompt = LANGUAGE_NAME + "> ";
			
			for (;;) {
				
				w.print(prompt);
				w.flush();
				
				String statement = readStatement(console.getInputStream()).trim();
				
				if (statement == null || statement.equals(".exit")) {
					w.println();
					w.flush();
					break;
				}
				
				/*
				 * I'll never understand why an empty return generates a semicolon.
				 */
				if (statement.isEmpty() || statement.equals(".") || statement.equals(";")) {
					continue;
				}

				try {
					if (statement.startsWith(".")) {
						execute(statement.substring(1));
						continue;
					}
				} catch (Exception e) {
					w.println("*** error: " + e.getMessage());
				} finally {
				}	
				
				try {
					InputStream is = new ByteArrayInputStream(statement.getBytes());
					mg.parseInNamespace(is, USER_DEFAULT_NAMESPACE, resolver);
					is.close();
				} catch (Exception e) {
					w.println("*** error: " + e.getMessage());
				} finally {
				}	
					
				IModelObject obj = resolver.getLastProcessedObject(); 
				
				if (obj instanceof IModel) {
					
					/*
					 * add to context, show it
					 */
					
				}

//				if (_visualizer == null) {
//					_visualizer = new ContextVisualizer();
//					_visualizer.show();
//				}
				
					
			}
		} catch (Exception e) {
			w.println("*** error: " + e.getMessage());
		} finally {
		}	
	}

	

	public String readStatement(InputStream input) {

		// gently flush stream from any leftover whitespace
		try {
			while (input.available() > 0)
				input.read();
		} catch (IOException e1) {
		}
		
		StringBuffer buff = new StringBuffer();
		Boolean escape = null;
		
		while (true) {
			int ch;
			try {
				ch = input.read();
				if (escape == null)
					escape = ch == '.';
			} catch (IOException e) {
				return null;
			}
			buff.append((char)ch);

			if (ch == (escape ? '\n' : ';')) {
				break;
			}
		}
		return buff.toString();
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
			
			ISemanticObject<?> result = CommandManager.get().submitCommand(cmd, session);

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
