/**
 * IConformance.java
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

import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * Conformance objects are created to be used as "policies" for comparing
 * instances. The conformance idea is used to recognize the fact that two
 * instance may be equivalent or not according to the particular context of the
 * comparison. For example, in the context of model/data mediation we may be
 * comparing two observations that are done differently (e.g. at different
 * resolutions) but in fact observe the same thing in the same context and thus
 * should be considered equal when mediation is enabled. By defining the details
 * of the comparison into an appropriate conformance policy, we enable
 * comparison exactly the way it is intended.
 * 
 * Because conformance policies work by returning a Constraint that selects the
 * instances that conform to a passed one, it is possible to use a conformance
 * policy as a query generator to retrieve all the conformant instances from a
 * kBox.
 * 
 * @author UVM Affiliate
 * @see Constraint
 * @see IKBox
 */
public interface IConformance {

	/**
	 * Restrict to select whatever conforms with the passed instance under the
	 * conformance notion we represent. At the very least this should set the
	 * concept another object needs to match to be conformant with the passed
	 * one.
	 * 
	 * @param i
	 * @throws ThinklabException
	 */
	public abstract void setTo(IInstance i) throws ThinklabException;

	/**
	 * Set the extent of the conformance for a classification property.
	 * Basically the conceptual limit of the match between instances.
	 * 
	 * @param property
	 * @param extent
	 */
	public abstract Restriction setConformance(IProperty property,
			IConcept extent);

	/**
	 * Set the extent of the comparison for a literal property.
	 * 
	 * @param property
	 * @param extent
	 */
	public abstract Restriction setConformance(IProperty property, IValue extent);

	/**
	 * Set the extent of the comparison for an object property. By default this
	 * should probably just create another conformance of the same class for the
	 * linked object.
	 * 
	 * @param property
	 * @param objectSelector
	 */
	public abstract Restriction setConformance(IProperty property,
			IInstance object);

	/**
	 * Return a constraint that selects conformant objects reflecting this
	 * conformance notion and our specific configuration.
	 * 
	 * @return
	 */
	public abstract Constraint getConstraint();

	/**
	 * Reset the conformance object to a pristine state (before the setTo()
	 * method has been called).
	 * 
	 */
	public abstract void reset();
}
