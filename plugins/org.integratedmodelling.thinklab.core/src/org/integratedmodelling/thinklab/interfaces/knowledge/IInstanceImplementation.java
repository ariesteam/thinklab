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
package org.integratedmodelling.thinklab.interfaces.knowledge;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;

/**
 * The implementation of an instance, generated and initialized by a ConceptManager after a knowledge
 * model has been read.
 * 
 * @author UVM Affiliate
 * @see org.integratedmodelling.thinklab.interfaces.knowledge.OntoBean for automatically wiring properties to class fields.
 */
public interface IInstanceImplementation {

	
	/**
	 * Called just after creation, passing the OWL counterpart. 
	 * @param i
	 * @param session the session that created the object, or null 
	 * @throws ThinklabException
	 */
	public abstract void initialize(IInstance i) throws ThinklabException;
	
	/**
	 * Called when the OWL counterpart is validated. Supposed to throw
	 * an explanatory exception if validation is unsuccessful.
	 * @param i
	 * @throws ThinklabValidationException
	 * @throws ThinklabException 
	 */
	public abstract void validate(IInstance i) throws ThinklabException;
}
