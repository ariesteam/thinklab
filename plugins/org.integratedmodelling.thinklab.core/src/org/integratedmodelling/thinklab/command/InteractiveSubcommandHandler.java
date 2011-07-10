package org.integratedmodelling.thinklab.command;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.runtime.ISession;

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
