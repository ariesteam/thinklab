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

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.literals.BooleanValue;

/**
 * TODO this is a topology operator (like contains) so it should end up in Corescience, not here
 * @author Ferdinando Villa
 *
 */
@InstanceImplementation(concept="thinklab-core:Intersection")
public class Intersects extends Operator {

	@Override
	public IValue eval(Object... arg) throws ThinklabException {
		/*
		 * TODO 
		 * FIXME
		 * this is BS, it's meant for strings, but the op should test
		 * topologies.
		 */
		return new BooleanValue(asText(arg[0]).contains(asText(arg[1])));
	}

	@Override
	public String getName() {
		return "intersects";
	}

}
