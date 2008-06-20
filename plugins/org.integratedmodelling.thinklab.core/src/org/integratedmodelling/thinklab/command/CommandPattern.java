/**
 * CommandPattern.java
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

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IAction;

/**
 * A simple helper class to make it quicker to define a new command. It just provides two methods returning a command declaration 
 * and a response action. The install() method will install them in the Knowledge manager. Useful to keep the code clean, as all
 * classes can be declared as inner classes of a command pattern, and each command/response pair can be neatly incorporated in one
 * class file.
 * @see org.integratedmodelling.thinklab.shell.Help for an example.
 * @author villa
 */
public abstract class CommandPattern {
    
    abstract public CommandDeclaration createCommand() throws ThinklabException;
    abstract public IAction createAction();
    
    public void install(KnowledgeManager km) throws ThinklabException {
        km.registerCommand(createCommand(), createAction());
    }

}
