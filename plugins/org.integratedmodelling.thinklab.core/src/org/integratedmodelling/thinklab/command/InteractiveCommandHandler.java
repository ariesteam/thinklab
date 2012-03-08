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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;

/**
 * Something to derive from to implement a sub-command system. Provides functions to ask questions
 * and print results; automatically binds in and out to console, and checks that session is
 * interactive. doInteractive() virtual behaves like execute - i.e. can invoke the interactive
 * functions or not. If a subcommand loop is desired, use InteractiveSubcommandHandler.
 * 
 * @author Ferdinando
 *
 */
public abstract class InteractiveCommandHandler implements ICommandHandler {

	InputStream inp = null;
	PrintStream out = null;
	String id = null;
	BufferedReader in = null;
	
	protected String prompt() throws ThinklabIOException  {
		return ask(null);
	}
	
	/**
	 * Ask a question and return the answer. 
	 * 
	 * @param prompt
	 * @return
	 * @throws ThinklabIOException
	 */
	protected String ask(String prompt) throws ThinklabIOException {
		out.print(prompt == null? (id + "> ") : prompt);
		String ret = null;
		try {
			ret = in.readLine();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		/*
		 * the stupid console returns a ; for any empty command, which is weird - this should be
		 * removed and the console should be fixed.
		 */
		return ret == null ? null : ((ret.trim().isEmpty() || ret.equals(";")) ? null : ret.trim());
	}
	

	/**
	 * Like ask(prompt), returns default if user presses enter.
	 * 
	 * @param prompt
	 * @param defaultresponse
	 * @return
	 * @throws ThinklabIOException
	 */
	protected String ask(String prompt, String defaultresponse) throws ThinklabIOException {
		String ret = ask(prompt);
		return ret == null ? defaultresponse : ret;
	}
	
	/**
	 * Just print the passed text.
	 * 
	 * @param text
	 */
	protected void say(String text) {
		out.println(text);
	}
	
	@Override
	public ISemanticObject execute(Command command, ISession session)
			throws ThinklabException {

		this.inp = session.getInputStream();
		this.out = session.getOutputStream();
		this.id = command.getName();
		this.in = new BufferedReader(new InputStreamReader(this.inp));
		
		if (this.inp == null || this.out == null)
			throw new ThinklabValidationException(
					"command " + command.getName() + " must be executed interactively");
		
		return doInteractive(command, session);
	}

	/**
	 * This can use interactive functions as much as necessary or not at all.
	 * 
	 * @return
	 * @throws ThinklabException 
	 */
	protected abstract ISemanticObject doInteractive(Command command, ISession session) 
		throws ThinklabException;

}
