/**
 * ICommandListener.java
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

import java.util.Map;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.value.AlgorithmValue;

/**
 * Classes implementing ICommandListener have the ability to react to a command and must implement a call method that is sent to
 * the class implementing them. For example, IInstance implements ICommandListener: as such,
 * instances of ontologies become capable of responding to commands just like objects in any 
 * object-oriented framework.
 * 
 * @author villa
 */
public interface ICommandListener {
    
	/**
	 * Execute passed algorithm (possibly validated/compiled from string). Execution context translates
	 * identifiers into objects in session and semantic types into objects in knowledge base. Current
	 * instance is available as "self" variable. 
	 * 
	 * @param algorithm compiled code to execute
	 * @return the value that results from execution, or null if void
	 * @throws ThinklabException if anything goes wrong.
	 */
	public abstract IValue execute(AlgorithmValue algorithm, ISession session) throws ThinklabException;

	/**
	 * Execute passed algorithm (possibly validated/compiled from string). Execution context translates
	 * identifiers into objects in session and semantic types into objects in knowledge base. Current
	 * instance is available as "self". Passed arguments are put into context prior to execution.
	 * @param algorithm
	 * @param session
	 * @param arguments 
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IValue execute(AlgorithmValue algorithm, ISession session, Map<String, IValue> arguments)
	throws ThinklabException;

    
    // TODO varargs call method
}
