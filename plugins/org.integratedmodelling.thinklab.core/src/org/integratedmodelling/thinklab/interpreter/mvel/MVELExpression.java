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
package org.integratedmodelling.thinklab.interpreter.mvel;

import java.io.Serializable;
import java.util.Map;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.mvel2.MVEL;

/**
 * Helper class to manage running an MVEL expression without working too much.
 * 
 * @author Ferdinando
 *
 */
public class MVELExpression {

	private Serializable bytecode;

	public MVELExpression(String s) {
		this.bytecode = MVEL.compileExpression(s); 
	}
	
	public Object eval(Map<?,?> parms) {
		
		Object ret = null;
		ClassLoader clsl = null;
		
		try {
			clsl = Thinklab.get().swapClassloader();			
			ret = MVEL.executeExpression(this.bytecode, parms);
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		} finally {
			Thinklab.get().resetClassLoader(clsl);
		}
		return ret;
	}
	
}
