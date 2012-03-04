/**
 * TimeRecordValidator.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabTimePlugin.
 * 
 * ThinklabTimePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabTimePlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.time.constructors;

import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticLiteral;
import org.integratedmodelling.thinklab.modelling.internal.MN;
import org.integratedmodelling.time.TimePlugin;
import org.integratedmodelling.time.literals.TimeValue;

/**
 * This validator creates a whole Observation structure, complete with value, conceptual model and
 * observable, corresponding to a time record from the
 * literal string representing one or more ISO time points. Literal is passed unaltered to constructor
 * of TimeValue.
 * 
 * FIXME use to build these things with ontology - needs to be figured out
 * 
 * @author Ferdinando Villa
 * @see org.integratedmodelling.time.values.TimeValue;
 */
public class TimeRecordValidator  {

	public ISemanticLiteral validate(String literalValue, IConcept concept, IOntology ontology) 
		throws ThinklabValidationException {

//		ObjectReferenceValue ret = null;
				
		try {
			
			/* create instance of Time observation ready for validation */
			IInstance tobs = 
				ontology.createInstance(ontology.getUniqueObjectName("trec"), TimePlugin.TimeRecord());
			
			/* complete definition with observable. */
			tobs.addObjectRelationship(MN.HAS_OBSERVABLE, TimePlugin.absoluteTimeInstance());
			
			/* make datasource out of time stamp and add to instance */
			TimeValue time = new TimeValue(literalValue);
			tobs.addLiteralRelationship(MN.HAS_DATASOURCE, time);
			
			/* create return value */
//			ret = new ObjectReferenceValue(tobs);
			
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}

//		return ret;
		return null;
	}

	public void declareType() {
		// TODO Auto-generated method stub
		
	}

}
