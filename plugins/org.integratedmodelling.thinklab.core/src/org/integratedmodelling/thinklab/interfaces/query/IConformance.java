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
package org.integratedmodelling.thinklab.interfaces.query;

import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

/**
 * Conformance objects are "policies" for comparing
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
	 * Return a constraint that selects conformant objects, reflecting this
	 * conformance notion.
	 * 
	 * @return
	 */
	public abstract Constraint getConstraint(IInstance instance) throws ThinklabException;

}
