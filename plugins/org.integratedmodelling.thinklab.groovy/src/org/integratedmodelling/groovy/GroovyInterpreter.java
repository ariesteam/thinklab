/**
 * GroovyInterpreter.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabGroovyPlugin.
 * 
 * ThinklabGroovyPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabGroovyPlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.groovy;

import groovy.lang.GroovyShell;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.extensions.KnowledgeProvider;
import org.integratedmodelling.thinklab.interfaces.IAlgorithmInterpreter;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.AlgorithmValue;
import org.integratedmodelling.thinklab.value.Value;

public class GroovyInterpreter implements IAlgorithmInterpreter {

	
	public IValue execute(AlgorithmValue codeval, IContext context) throws ThinklabException {
		
		IValue ret = null;
		
		GroovyShell shell = new GroovyShell(((GroovyContext)context).getBinding());

		Object result = null;
		
		try {
			result = shell.evaluate(codeval.toString());
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
			if (result == null)
			return ret;
		
		/* reparse result into IValue */
		if (result instanceof Value) {
			ret = (IValue)result;
		} else {
			
			try {
				ret = Value.getValueForObject(result);
			} catch (ThinklabValidationException e) {
			
				/* TODO nontrivial conversions */
				if (result instanceof IInstance) {
					
				}
			}
		}
		
		return ret;
	}
	
	public void initialize(KnowledgeProvider km) {
		// TODO Auto-generated method stub

	}

}
