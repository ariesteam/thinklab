/**
 * Conformance.java
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
package org.integratedmodelling.thinklab.constraint;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.IRelationship;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.knowledge.query.IConformance;

public abstract class DefaultAbstractConformance implements IConformance {

	public abstract IConcept getMatchingConcept(IConcept concept);

	/**
	 * Set the extent of the conformance for a classification property.
	 * Basically the conceptual limit of the match between instances.
	 * 
	 * @param property
	 * @param extent
	 */
	public abstract Restriction setConformance(IProperty property, IConcept extent);

	/**
	 * Set the extent of the comparison for a literal property.
	 * 
	 * @param property
	 * @param extent
	 */
	public abstract Restriction setConformance(IProperty property, IValue extent);


	@Override
	public Constraint getQuery(IInstance instance) throws ThinklabException {

		Constraint constraint = new Constraint(getMatchingConcept(instance.getDirectType()));
		Restriction res = null;
		
		for (IRelationship r : instance.getRelationships()) {

			Restriction rr = null;
			
			if (r.isClassification()) {				
				rr = setConformance(r.getProperty(), r.getValue().getConcept());
			} else if (r.isObject()) {
				rr = new Restriction(r.getProperty(), 
						getQuery(r.getValue().asObject()));
			} else {		
				rr = setConformance(r.getProperty(), r.getValue());
			}
			
			if (rr != null) {
				if (res == null)
					res = rr;
				else
					res = Restriction.AND(rr);
			}
		}
		
		if (res != null)
			constraint.restrict(res);
		
		return constraint;
	}

}
