/**
 * IAction.java
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
package org.integratedmodelling.thinklab.interfaces;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;

/**
 * <p>Actions are installed along with command declarations. Each action has a virtual execute() method which is
 * passed a command and a knowledge manager.</p>
 * <p>To implement a new command, submit its declaration and an action to the knowledge manager. The action must
 * implement the execute() method which returns a literal (can be a concept, a string, anything) or null if void.</p>
 * <p>Note that no assumption can be made on what generated the action. According to the knowledge interface, it
 * can be a command from the user, a CGI query string, a SOAP message, or anything else. All we know is that a 
 * Command is generated from it.</p>
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 * @see Command
 * @see KnowledgeManager
 */
public interface IAction {

    public IConcept receiver = null;
    
	/**
	 * Execute the passed command.
	 * @param command the command that triggered the action.
	 * @param outputDest TODO
	 * @param session TODO
	 * @param km the Knowledge Manager, only for convenience.
	 * @return a Value containing the result.
	 * @see Command
	 */ 
	public IValue execute (Command command, ICommandOutputReceptor outputDest, ISession session, KnowledgeManager km) throws ThinklabException;

}
