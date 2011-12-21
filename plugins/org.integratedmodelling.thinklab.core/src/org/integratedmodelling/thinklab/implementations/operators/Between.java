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
package org.integratedmodelling.thinklab.implementations.operators;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.BooleanValue;

/**
 * 
 * @author Ferdinando
 *
 */
@InstanceImplementation(concept="thinklab-core:WithinInterval")
public class Between extends Operator {

	@Override
	public IValue eval(Object... arg) throws ThinklabException {

		boolean ret = false;
		
		if (isNumeric(arg[0])) {
			ret =
				asDouble(arg[0]) >= asDouble(arg[1]) &&
				asDouble(arg[0]) <= asDouble(arg[2]);
		}
		
		return new BooleanValue(ret);
		
	}
}
