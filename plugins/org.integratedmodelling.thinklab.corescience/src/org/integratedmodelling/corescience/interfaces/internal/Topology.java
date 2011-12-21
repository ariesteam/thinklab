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
package org.integratedmodelling.corescience.interfaces.internal;

import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.units.Unit;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * Conceptual models of observations that define observation extents should be capable of
 * creating, merging and mediating extents. This is a different operation than standard 
 * mediation. In order to represent extents, conceptual models must implement ExtentConceptualModel.
 * 
 * @author Ferdinando Villa
 *
 */
public interface Topology extends IObservation {
	
	/**
	 * Produce the extent that describes the observation we're linked to.
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract IExtent getExtent() throws ThinklabException;

	/**
	 * Check that the passed unit represents an observable that adopts this topology. 
	 * 
	 * @param unit
	 */
	public abstract void checkUnitConformance(IConcept concept, Unit unit) throws ThinklabValidationException;
		

}
