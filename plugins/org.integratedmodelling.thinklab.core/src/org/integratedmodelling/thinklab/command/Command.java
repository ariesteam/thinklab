/**
 * Command.java
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
package org.integratedmodelling.thinklab.command;

import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.literals.Value;

/**
 * Commands are passed back and forth between the Knowledge Interface and the Knowledge Manager. 
 * Commands must match a declaration.
 * 
 * @author Ferdinando Villa
 */
public class Command {

	CommandDeclaration declaration;
    HashMap<String, String> opts;
    HashMap<String, String> args;
    HashMap<String, IValue> optValues;
    HashMap<String, IValue> argValues;
    String stringValue = null;
	private boolean verbose;
	private boolean debug;
    
    public CommandDeclaration getDeclaration() {
    	return declaration;
    }

    public HashMap<String, String> getOptions() {
    	return opts;
    }
    
    public HashMap<String, String> getArguments() {
    	return args;
    }
    
    public void setOptionValue(String s, IValue v) {
    	
    	if (optValues == null)
    		optValues = new HashMap<String, IValue>();
    	
    	optValues.put(s, v);
    }

    public void setArgumentValue(String s, IValue v) {
    	
    	if (argValues == null)
    		argValues = new HashMap<String, IValue>();
    	
    	argValues.put(s, v);
    }

	public void validate() throws ThinklabException {
    	
    	if (declaration == null)
    		// shouldn't happen
    		throw new ThinklabValidationException("unknown command");
 
    	declaration.validateCommand(this);
    }
    
    /**
     * Create a command from a declaration, to be filled in later.
     * @param d
     */
    public Command(CommandDeclaration d)  {
    	args = new HashMap<String, String>();
    	opts = new HashMap<String, String>();
    	declaration = d;
    }
    
    public Command(String ID, HashMap<String, String> args) throws ThinklabException {
    	if (ID != null)
    		declaration = CommandManager.get().requireDeclarationForCommand(ID);
        this.args = args;
        opts = null;
    }
    
    public Command(String ID, HashMap<String, String> args, HashMap<String, String> opts) throws ThinklabException {
        declaration = CommandManager.get().requireDeclarationForCommand(ID);   
        this.args = args;
        this.opts = opts;        
    }
    
    /**
     * Create a command from pre-parsed declaration and arguments.
     * @param args
     * @param opts
     * @param PLUGIN_ID
     * @throws ThinklabException
     */
    public Command(CommandDeclaration declaration, HashMap<String, IValue> args, HashMap<String, IValue> opts) throws ThinklabException {
        this.declaration = declaration;   
        this.argValues = args;
        this.optValues = opts;        
    }
    
    /**
     * Create and validate command making sure that all required arguments are set into the given
     * map. Also lookup any options in it. Will try to convert POD data to the corresponding IValue.
     * 
     * @param commandName
     * @param allargs
     * @throws ThinklabException
     */
    public Command(String commandName, Map<String, Object> allargs) throws ThinklabException {

    	args = new HashMap<String, String>();
    	opts = new HashMap<String, String>();

        declaration = CommandManager.get().requireDeclarationForCommand(commandName);  
    	
        for (String arg : declaration.getMandatoryArgumentNames()) {
        
        	Object o = allargs.get(arg);
        	
        	IValue val = null;
        	
        	if (o == null)
        		throw new ThinklabValidationException("command " + commandName + " requires argument " + arg);
        	if ( !(o instanceof IValue)) {
        		val = Value.getValueForObject(o);
        	} else {
        		val = (IValue) o;
        	}

        	args.put(arg, val.toString().trim());
        	setArgumentValue(arg, val);
        }
        
        for (String arg : declaration.getOptionalArgumentNames()) {
        	
        	Object o = allargs.get(arg);
        	IValue val = null;
        	
        	if (o != null) {
				if (!(o instanceof IValue)) {
					val = Value.getValueForObject(o);
				} else {
					val = (IValue) o;
				}
				
	        	args.put(arg, val.toString().trim());
				setArgumentValue(arg, val);
        	}
        }
        for (String arg : declaration.getOptionNames()) {
        	
        	Object o = allargs.get(arg);
        	IValue val = null;
        	
        	if (o != null) {
				if (!(o instanceof IValue)) {
					val = Value.getValueForObject(o);
				} else {
					val = (IValue) o;
				}
	        	opts.put(arg, val.toString().trim());
				setOptionValue(arg, val);
        	}
        }
        
        validate();
    }

    public IValue getOption(String option) {
    	return opts == null ? null : optValues.get(option);
    }
    
    public IValue getArgument(String argument) {
    	return args == null ? null : argValues.get(argument);
    }
    
    public String getOptionAsString(String option) {
        return opts == null ? null : opts.get(option);
    }
    
    public String getOptionAsString(String option, String defaultValue) {
        return opts == null ? 
        		defaultValue : 
        		(opts.containsKey(option) ? opts.get(option) : defaultValue);
    }
    
    public String getArgumentAsString(String argName) {
        return args == null ? null : args.get(argName);
    }
    
    /**
     * NOTE: this will return true for optional arguments that were not supplied on the 
     * command line but have a default value declared for them.
     * @param argName
     * @return
     */
    public boolean hasArgument(String argName) {
    	return args != null && args.get(argName) != null && !args.get(argName).trim().equals("");
    }
    
    public boolean hasOption(String optName) {
        return opts.containsKey(optName);
    }
    
    public String getName() {
    	return declaration.ID;
    }
    
    public String toString() {
    	
    	/* if we were not read from a string, reconstruct it */
    	if (stringValue == null) {
    		
    		stringValue = "";
    		// TODO reconstruct command as string
    	}   
    	
    	return stringValue;
    }

	public double getOptionAsDouble(String string, double d) {
		String ret = getOptionAsString(string);
		return ret == null ? d : Double.parseDouble(ret);
	}

	void setVerbose(boolean b) {
		this.verbose = b;
	}

	void setDebug(boolean b) {
		this.debug = b;
	}
	
	public boolean isVerbose() {
		return this.verbose;
	}
	
	public boolean isDebug() {
		return this.debug;
	}

}
