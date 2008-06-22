/**
 * Help.java
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
package org.integratedmodelling.thinklab.command.base;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.command.CommandPattern;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedCommandException;
import org.integratedmodelling.thinklab.interfaces.IAction;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;

/** the help command for the command-line interface */
public class Help extends CommandPattern {

       
    class HelpAction implements IAction {

        public IValue execute(Command command, ICommandOutputReceptor outputWriter, ISession session, KnowledgeManager km) throws ThinklabException {
            
            String topic = command.getArgumentAsString("topic");

            if ("__none".equals(topic)) {
                /* loop over commands in KM; print a line for each one */
            	outputWriter.displayOutput("Available commands (\'help <command>\' for more):");
            	for (CommandDeclaration decl : CommandManager.get().getCommandDeclarations()) {
            		outputWriter.displayOutput("\t" + decl.ID + "\t" + decl.description);
            	}
            } else {
            	/* should be a command name */
            	CommandDeclaration cd = CommandManager.get().getDeclarationForCommand(topic);
                
                if (cd == null)
                    throw new ThinklabMalformedCommandException("command " + topic + " undefined");
                
                cd.printLongSynopsis(outputWriter);
            }
            
            
            return null;
        }
    }
    
    public Help( ) {
    	super();
	}

	public CommandDeclaration createCommand() throws ThinklabException {
        CommandDeclaration ret = new CommandDeclaration("help", "get help on usage and commands");
        
        ret.addOptionalArgument("topic", "topic for in-depth help", 
        		KnowledgeManager.Text().getSemanticType(), "__none");
        
        return ret;
    }
    
    public IAction createAction() {
        return new HelpAction();
    }
    
}
