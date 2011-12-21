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
package org.integratedmodelling.time.constructors;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.ObjectReferenceValue;
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

	public IValue validate(String literalValue, IConcept concept, IOntology ontology) 
		throws ThinklabValidationException {

		ObjectReferenceValue ret = null;
				
		try {
			
			/* create instance of Time observation ready for validation */
			IInstance tobs = 
				ontology.createInstance(ontology.getUniqueObjectName("trec"), TimePlugin.TimeRecord());
			
			/* complete definition with observable. */
			tobs.addObjectRelationship(CoreScience.HAS_OBSERVABLE, TimePlugin.absoluteTimeInstance());
			
			/* make datasource out of time stamp and add to instance */
			TimeValue time = new TimeValue(literalValue);
			tobs.addLiteralRelationship(CoreScience.HAS_DATASOURCE, time);
			
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
