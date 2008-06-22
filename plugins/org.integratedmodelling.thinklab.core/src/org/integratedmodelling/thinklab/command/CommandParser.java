package org.integratedmodelling.thinklab.command;

import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;

/**
 * Use a Jopt-simple parser to parse POSIX-style command line into a command.
 * 
 * @author Ferdinando
 *
 */
public class CommandParser {
	
	public static Command parse(String s) throws ThinklabException {
		
		String[] a = s.split("\\s");
		Command ret = null;
		
		if (a.length < 1) {
			throw new ThinklabValidationException("can't parse an empty command");
		}
		
		CommandDeclaration declaration = 
			CommandManager.get().requireDeclarationForCommand(a[0]);
				
		String[] args = new String[a.length-1];
		System.arraycopy(a, 1, args, 0, a.length - 1);
		
		OptionParser parser = declaration.createParser();
		OptionSet options = parser.parse(args);
		
		ret = new Command(declaration);
		
		/*
		 * scan all options; if not given, create default
		 */
		for (String opt : declaration.getOptionNames()) {
			
			if (options.has(opt)) {

				String o = null;
				if (declaration.getOptionType(opt) != null)
					o = options.valueOf(opt).toString();
				
				ret.opts.put(opt,o);
			} 
		}
		/*
		 * count remaining arguments; complain if any missing or too many given
		 */
		List<Object> ags = options.nonOptionArguments();
		
		/*
		 * assign all arguments in order
		 */
		for (int i = 0; i < ags.size(); i++) {

			String argname = declaration.getArgumentNameAtIndex(i);
			String as = ags.get(i).toString();

			ret.args.put(argname, as);
		}

		/*
		 * validate command, turning options and arguments into values.
		 */
		ret.validate();
		
		return ret;
	}
	
}
