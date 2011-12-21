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
package org.integratedmodelling.modelling.literals;

import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.literals.Value;

/**
 * Just a shuttle object for a context so it can be returned easily by semantically aware
 * functions, and eventually parsed from a literal. Takes its type from the observation it
 * describes.
 * 
 * @author Ferdinando
 *
 */
public class ContextValue extends Value {
	
	IObservationContext c;

	public ContextValue(IObservationContext c) {
		this.c = c;
	}
	
	@Override
	public String toString() {
		return c.toString();
	}

	@Override
	public boolean isPODType() {
		return false;
	}

	@Override
	public IConcept getConcept() {
		return c.getObservation().getObservableClass();
	}

	public IObservationContext getObservationContext() {
		return c;
	}
	
}
