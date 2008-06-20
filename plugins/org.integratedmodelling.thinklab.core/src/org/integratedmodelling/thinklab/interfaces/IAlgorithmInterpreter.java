/**
 * IAlgorithmInterpreter.java
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

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interpreter.AlgorithmInterpreterFactory;
import org.integratedmodelling.thinklab.value.AlgorithmValue;

/**
 * The simple interface for an algorithm interpreter that is run in a session. Algorithms are
 * IValues, intended as literals of some Algorithm class implemented by a plug-in. Their validation
 * may entail compilation and syntax checking.
 * 
 * Algorithm interpreters are given to sessions by an AlgorithmInterpreterFactory, which holds
 * separate interpreters per sessions and uses the class of the algorithm to select the right 
 * interpreter. 
 * @see AlgorithmInterpreterFactory
 * @author Ferdinando Villa
 *
 */
public interface IAlgorithmInterpreter {
	
	public interface IContext {
		
		public void bind(Object object, String name);
		
	}

	public void initialize(IKnowledgeProvider km);
	
	public IValue execute(AlgorithmValue code, IContext context) throws ThinklabException;

}
