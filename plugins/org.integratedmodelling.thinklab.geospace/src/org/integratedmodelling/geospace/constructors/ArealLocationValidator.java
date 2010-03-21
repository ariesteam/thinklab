/**
 * ArealLocationValidator.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabGeospacePlugin.
 * 
 * ThinklabGeospacePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabGeospacePlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.geospace.constructors;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.ObjectReferenceValue;

/**
 * This validator creates a whole Observation structure, complete with value, conceptual model and
 * observable, corresponding to an areal location expressed in WKT.
 * 
 * @author Ferdinando Villa
 * FIXME doesn't validate anything - must be put back into validation with ontology
 */
public class ArealLocationValidator {
	
	public IValue validate(String literalValue, IConcept concept, IOntology ontology) 
		throws ThinklabValidationException {

		ObjectReferenceValue ret = null;
				
		try {
			
			/* create instance of Time observation ready for validation */
			IInstance tobs = 
				ontology.createInstance(ontology.getUniqueObjectName("aloc"), 
						Geospace.get().ArealLocation());
			
			/* complete definition with observable. */
			tobs.addObjectRelationship(CoreScience.HAS_OBSERVABLE, 
					Geospace.get().absoluteArealLocationInstance(ontology));
			
			/* make datasource out of time stamp and add to instance */
			ShapeValue shape = new ShapeValue(literalValue);
			tobs.addLiteralRelationship(CoreScience.HAS_DATASOURCE, shape);
			
			/* create return value */
			ret = new ObjectReferenceValue(tobs);
			
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}

		return ret;
	}

	public void declareType() {
		// TODO Auto-generated method stub
		
	}

}
