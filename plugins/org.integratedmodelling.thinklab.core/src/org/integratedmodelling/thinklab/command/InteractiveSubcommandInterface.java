package org.integratedmodelling.thinklab.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * Something to derive from to implement a sub-command system.
 * 
 * @author Ferdinando
 *
 */
public abstract class InteractiveSubcommandInterface implements ICommandHandler {

	InputStream inp = null;
	PrintStream out = null;
	String id = null;
	BufferedReader in = null;
	
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
	
	private String prompt() throws ThinklabIOException {
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
	 * Just print the passed text.
	 * 
	 * @param text
	 */
	protected void say(String text) {
		out.println(text);
	}
	
	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {

		this.inp = session.getInputStream();
		this.out = session.getOutputStream();
		this.id = command.getName();
		this.in = new BufferedReader(new InputStreamReader(this.inp));
		
		if (this.inp == null || this.out == null)
			throw new ThinklabInappropriateOperationException(
					"command " + command.getName() + " must be executed interactively");

		String cm = null;
		IValue ret = null;
		do {
			cm = prompt();
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
