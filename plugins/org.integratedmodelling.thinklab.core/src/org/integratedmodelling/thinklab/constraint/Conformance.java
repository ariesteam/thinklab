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

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.query.IConformance;

public abstract class Conformance implements IConformance {

	public abstract IConcept getMatchingConcept(IConcept concept);
	
	Constraint constraint = null; 
	
	public Constraint getConstraint() {
		return constraint;
	}

	/**
	 * By default, we create a restriction from a conformance constraint of the same
	 * class.
	 */
	public Restriction setConformance(IProperty property, IInstance object) {
		
		IConformance conf = null;
		
		try {
			conf = this.getClass().newInstance();
		} catch (Exception e) {
			return null;
		}
		
		try {
			conf.setTo(object);
		} catch (ThinklabException e) {
			return null;
		}
		
		return new Restriction(property, conf.getConstraint());

	}

	public void setTo(IInstance i) throws ThinklabException {
		
		reset();
		
		constraint = new Constraint(getMatchingConcept(i.getDirectType()));
		
		Restriction res = null;
		
		for (IRelationship r : i.getRelationships()) {

			Restriction rr = null;
			
			if (r.isClassification()) {				
				rr = setConformance(r.getProperty(), r.getValue().getConcept());
			} else if (r.isObject()) {
				rr = 
					setConformance(r.getProperty(), r.getValue().asObjectReference().getObject());
			} else {		
				rr = setConformance(r.getProperty(), r.getValue());
			}
			
			if (rr != null) {
				if (res == null)
					res = rr;
				else
					res.AND(rr);
			}
			
		}
		
		if (res != null)
			constraint.restrict(res);
	}
	
	public void reset() {
		constraint = null;
	}

}
