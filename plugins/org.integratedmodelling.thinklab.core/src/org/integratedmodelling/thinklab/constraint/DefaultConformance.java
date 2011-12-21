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
package org.integratedmodelling.thinklab.constraint;

import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.IValue;

/**
 * Default conformance policy will select same concepts for class types and classification properties, 
 * and will ignore all literal properties.
 * 
 * @author Ferdinando
 *
 */
public class DefaultConformance extends DefaultAbstractConformance {

	@Override
	public IConcept getMatchingConcept(IConcept concept) {
		return concept;
	}

	@Override
	public Restriction setConformance(IProperty property, IConcept extent) {
		return new Restriction(property, extent);
	}

	@Override
	public Restriction setConformance(IProperty property, IValue extent) {
		// by default, literals are not part of conformance checking.
		return null;
	}
}
