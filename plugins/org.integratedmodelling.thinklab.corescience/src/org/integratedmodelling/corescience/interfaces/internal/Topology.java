/**
 * IExtentConceptualModel.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCoreSciencePlugin.
 * 
 * ThinklabCoreSciencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCoreSciencePlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.corescience.interfaces.internal;

import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.units.Unit;
import org.integratedmodelling.thinklab.constraint.Restriction;
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

	/*
	 * FIXME still rough - return a restriction that will match observations with
	 * similar topology, using the parameter to define the relationship (which should be
	 * a formal enum or topological operator)
	 */
	public abstract Restriction getConstraint(String operator) throws ThinklabException;

	/**
	 * Check that the passed unit represents an observable that adopts this topology. 
	 * 
	 * @param unit
	 */
	public abstract void checkUnitConformance(IConcept concept, Unit unit) throws ThinklabValidationException;
		

}
