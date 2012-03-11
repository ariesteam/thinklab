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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import joptsimple.OptionParser;
import joptsimple.OptionSpecBuilder;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;

/**
 * Defines the interface for a command. A CommandDeclaration passed to the KnowledgeManager declares a command that
 * can be used in any way the adopted IKnowledgeInterface implies. Each Command object adopts a CommandDeclaration. The set of CommandDeclarations
 * known to the system can be translated, e.g. in WSDL for a SOAP interface policy. Plug-ins can create CommandInterfaces and register them.
 * 
 * @author Ferdinando Villa
 * @see org.integratedmodelling.thinklab.command.Command
 */
public class CommandDeclaration {

	public String ID;
	public String description;
	public IConcept returnType = null;
	
	/**
	 * these are always accepted, and are passed to the session so that the 
	 * run state can be pushed before any command is executed.
	 */
	public String[] generalOptions = {"--verbose", "--debug"};
	
	class argDescriptor {
		String id;
		String description;
		IConcept type;
		/* options use this to store the option label */
		String defaultValue;
		public String shortName;
	}
	
	ArrayList<argDescriptor> mandatoryArguments;
	ArrayList<argDescriptor> optionalArguments;
	ArrayList<argDescriptor> options;
	
	private boolean freeForm = false;

	public boolean admitsOption(String option) throws ThinklabValidationException {
		return findOption(option) != null;
	}
	
	public void setReturnType(IConcept concept) {
		returnType = concept;
	}
	
	public IConcept getReturnType() {
		return returnType;
	}
	
	private argDescriptor findOption(String id) throws ThinklabValidationException {
		
		for (argDescriptor d : options) {
			if (d.id.equals(id))
				return d;
		}
		throw new ThinklabValidationException(id + ": option " + id + " undefined");
	}
	
	private argDescriptor findArgument(String id) throws ThinklabValidationException {

		for (argDescriptor d : mandatoryArguments) {
			if (d.id.equals(id))
				return d;
		}
		for (argDescriptor d : optionalArguments) {
			if (d.id.equals(id))
				return d;
		}
		throw new ThinklabValidationException(id + ": argument " + id + " undefined");
	}
	
	public CommandDeclaration(String id, String description) {
        ID = id;
        this.description = description;
        mandatoryArguments = new ArrayList<argDescriptor>();
        optionalArguments = new ArrayList<argDescriptor>();
        options = new ArrayList<argDescriptor>();
    }

    public void validateCommand(Command command) throws ThinklabException {
		
    	/* add defaults for all optional arguments that we don't have */
    	for (argDescriptor s : optionalArguments) {
    		if (command.args.get(s.id) == null) {
    			
    			command.args.put(s.id, s.defaultValue);		
    		}
    	}

    	/* command must have the total number of args by now. */
    	if (command.args.size() != (mandatoryArguments.size() + optionalArguments.size()) )
    		throw new ThinklabValidationException(
    				"wrong number of argument. Run 'help " +
    				ID + 
    				"'.");
    		
    	/* validate types of args and options */
		for (Map.Entry<String, String> e :   command.args.entrySet()) {
            
            argDescriptor ad = findArgument(e.getKey());
            boolean ok = true;
            
			try {
				command.setArgumentValue(
						e.getKey(), 
						Thinklab.get().parse(e.getValue(), ad.type));
				
			} catch (ThinklabValidationException e1) {
				throw new ThinklabValidationException(
						"cannot validate input '" + 
						e.getValue() + 
						"' as " +
						ad.type +
						": " +
						e1.getMessage()
						);
			}
			
        }
	
        for (Map.Entry<String, String> e :   command.opts.entrySet()) {
            
            argDescriptor ad = findOption(e.getKey());

            if (ad.type == null)
            	continue;
			
			try {
				command.setArgumentValue(
						e.getKey(), 
						Thinklab.get().parse(e.getValue(), ad.type));
			} catch (ThinklabValidationException e1) {
				throw new ThinklabValidationException(
						"cannot validate input '" + 
						e.getValue() + 
						"' as " +
						ad.type +
						": " +
						e1.getMessage()
						);
			}
        }

    }

    
    /**
     * Declare a mandatory argument.
     * NOTE: no error checking is done for duplicate argument names, which will prevent correct operation.
     * 
     * @param argName the argument name
     * @param argDescription the argument name
     * @param argType the semantic type that will be used to validate the argument 
     * @param defaultValue the string representation of the default value
     * @throws ThinklabNoKMException 
     * @throws ThinklabResourceNotFoundException 
     */
	public void addMandatoryArgument(String argName, String argDescription, String argType) throws ThinklabException {

		argDescriptor a = new argDescriptor();
		a.id = argName;
		a.description = argDescription;
		a.type = Thinklab.c(argType);
		mandatoryArguments.add(a);
	}

    /**
     * You can only have optional arguments if you supply their default value. Commands will not contain
     * a flag indicating whether they have actually been supply, but only the default value. This is Java
     * and we like immutable interfaces.
     * 
     * NOTE: no error checking is done for duplicate argument names, which will prevent correct operation.
     * 
     * @param argName the argument name
     * @param argDescription the argument name
     * @param argType the semantic type that will be used to validate the argument 
     * @param defaultValue the string representation of the default value
     * @throws ThinklabNoKMException 
     * @throws ThinklabResourceNotFoundException 
     */
	public void addOptionalArgument(String argName, String argDescription, String argType, String defaultValue) throws ThinklabException {

		argDescriptor a = new argDescriptor();
		a.id = argName;
		a.description = argDescription;
		a.type = Thinklab.c(argType);
		a.defaultValue = defaultValue;
		optionalArguments.add(a);
	}

	/**
	 * Declare an option. Options can only have zero or one arguments.
	 * @param optName option name
	 * @param optArgumentLabel a one-word comprehensible label indicating the "class" of the expected option parameter, 
	 *        used in synopsis
	 * @param optDescription a longer-winded description of the option
	 * @param string 
	 * @param optType pass a null or "owl:Nothing" if the option does not have arguments
	 * @throws ThinklabNoKMException 
	 * @throws ThinklabResourceNotFoundException 
	 */
    public void addOption(String shortName, String optName, String optArgumentLabel, String optDescription, String optType) throws ThinklabException {
		argDescriptor a = new argDescriptor();
		a.id = optName;
		a.shortName = shortName;
		a.description = optDescription;
		a.type = 
			(optType == null || optType.equals("owl:Nothing")) ? 
					null : 
					Thinklab.c(optType);
		a.defaultValue = optArgumentLabel;
		options.add(a);   
    }
	
    public void printDescription(PrintStream writer) {
    	writer.append(description + "\n");
    }
    
    public String getShortSynopsis() {
    	
    	String s = ID + " ";
    	
    	if (options != null)
    		for (argDescriptor ad : options) {
    			s += "[-" + ad.id;
    			if (ad.type != null) 
    				s += "=<" + ad.type + ">";
    			s += "] "; 
    		}

    	if (mandatoryArguments != null)
    		for (argDescriptor ad : mandatoryArguments) {
    			s +=  "[" + ad.id + "=]<" + ad.type + "> "; 
    		}

    	if (optionalArguments != null)
    		for (argDescriptor ad : optionalArguments) {
    			s +=  "[[" + ad.id + "=]<" + ad.type + "> ]"; 
    		}

    	return s;
    }
    
    public String getLongSynopsis() {
    	
    	String s = "Usage: " + getShortSynopsis();
    	
    	s += "\n\n" + description + "\n\n";
    	
    	if (options != null && options.size() > 0) {
    		
    		s += "Options:\n";
    		for (argDescriptor ad : options) {
    			s += "\t-" + ad.shortName + "|--"  + ad.id;
    			if (ad.type != null) {
    				s += " (" + ad.type + ")";
    			}
    			s += "\t" + ad.description; 
    			if (ad.defaultValue != null) {
    				s += "\tdefault: " + ad.defaultValue;
    			}
    			s += "\n";
    		}
    		s += "\n";
    	}
    	
    	if (mandatoryArguments != null && mandatoryArguments.size() > 0) {

    		s += "Mandatory arguments:\n";
    		for (argDescriptor ad : mandatoryArguments) {
    			s += "\t" + ad.id + " (" + ad.type + ")\t" + ad.description; 
    			s += "\n";
    		}
    		s += "\n";
    	}
    		
    	if (optionalArguments != null && optionalArguments.size() > 0) {

    		s += "Optional arguments:\n";
    		for (argDescriptor ad : optionalArguments) {
    			s += "\t" + ad.id + " (" + ad.type + ")\t" + ad.description;		
    			s += "\n";
    		}
    		s += "\n";
    	}
    	
    	return s;
    }
     
    public void printShortSynopsis(PrintStream writer) {
        
    	writer.append(getShortSynopsis() + "\n");
    }
    
    public void printLongSynopsis(PrintStream writer) {
    	writer.append(getLongSynopsis() + "\n");
    }
        
    public String usage() {
    	return getLongSynopsis();
    }

	public String getArgumentNameAtIndex(int idx) throws ThinklabValidationException {
		
		if (idx < mandatoryArguments.size())
			return mandatoryArguments.get(idx).id;
		else if ((idx - mandatoryArguments.size()) < optionalArguments.size())
			return optionalArguments.get(idx - mandatoryArguments.size()).id;
		
		throw new ThinklabValidationException("command " + ID + " does not have " + (idx+1) + " parameters");
	}
    
	public IConcept getOptionType(String option) throws ThinklabValidationException {
		return findOption(option).type;
	}

	public IConcept getArgumentType(String arg) throws ThinklabValidationException {
		return findArgument(arg).type;
	}
	
	public String[] getMandatoryArgumentNames() {
		
		String[] ret = new String[mandatoryArguments.size()];
		
		int i = 0;
		for (argDescriptor arg : mandatoryArguments) {
			ret[i++] = arg.id;
		}
		
		return ret;
	}
	
	public String[] getOptionalArgumentNames() {
		
		String[] ret = new String[optionalArguments.size()];
		
		int i = 0;
		for (argDescriptor arg : optionalArguments) {
			ret[i++] = arg.id;
		}
		
		return ret;
	}
	
	public String[] getAllArgumentNames() {
		
		String[] ret = new String[mandatoryArguments.size() + optionalArguments.size()];
		
		int i = 0;
		for (argDescriptor arg : mandatoryArguments) {
			ret[i++] = arg.id;
		}
		for (argDescriptor arg : optionalArguments) {
			ret[i++] = arg.id;
		}
		return ret;
	}
	
	/**
	 * By setting the free form flag, we tell the system that the command can consist of any string; we stop
	 * parsing it after the command name and any recognized options, and set the string value of the command
	 * as whatever text follows after that.
	 * 
	 * @param b
	 */
	public void setFreeForm(boolean b) {
		freeForm = b;
	}
	
	public boolean isFreeForm() {
		return freeForm;
	}

	public boolean isOptionalArgument(String arg) {

		for (argDescriptor d : optionalArguments)
			if (d.id.equals(arg))
				return true;

		return false;
	}

	/*
	 * TODO move to joptsimple 
	 */
	public OptionParser createParser() {

		OptionParser ret = new OptionParser();
		
		
		// universally recognized
		ret.acceptsAll(Arrays.asList(new String [] {"verbose", "verbose", "debug", "debug" }));
		
		// TODO add defaults for arguments and options
		for (argDescriptor argd : options) {
			OptionSpecBuilder b = 
				ret.acceptsAll( Arrays.asList( new String[] { argd.shortName, argd.id } ));
			if (argd.type != null) {
				b.withRequiredArg();
			}
		}
		
		
		ret.recognizeAlternativeLongOptions(true);
		return ret;
	}

	public String[] getOptionNames() {

		String[] ret = new String[options.size()];
		
		int i = 0;
		for (argDescriptor arg : options) {
			ret[i++] = arg.id;
		}
		
		return ret;
	}

}
