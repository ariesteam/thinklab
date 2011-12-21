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

import java.util.HashMap;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

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
		
		if (options.has("verbose")) {
			ret.setVerbose(true);
		}

		if (options.has("debug")) {
			ret.setDebug(true);
		}
		
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
	
	public static Command parse(HashMap<String, String> values) throws ThinklabException {

		Command ret = null;
		
		if (!values.containsKey("id")) {
			throw new ThinklabValidationException("can't parse an empty command");
		}
		
		CommandDeclaration declaration = 
			CommandManager.get().requireDeclarationForCommand(values.get("id"));
				
		ret = new Command(declaration);
		
		for (CommandDeclaration.argDescriptor k : declaration.mandatoryArguments) {
			if (values.containsKey(k.id)) {
				ret.args.put(k.id,values.get(k.id));
			}
		}
		for (CommandDeclaration.argDescriptor k : declaration.optionalArguments) {
			if (values.containsKey(k.id)) {
				ret.args.put(k.id,values.get(k.id));
			}
		}
		for (CommandDeclaration.argDescriptor k : declaration.options) {
			if (values.containsKey(k.id)) {
				ret.opts.put(k.id,values.get(k.id));
			}
		}
		
		ret.validate();
		return ret;
	}
}
