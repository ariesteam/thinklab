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

import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;


/**
 * If an instance implementation class implements IParseable, whole instances can be specified as string 
 * literals in Thinklab lists (and derived formalisms such as OPAL) using the syntax (# literal).
 * 
 * @author Ferdinando
 *
 */
public interface IParseableKnowledge {

	/**
	 * Called when the instance is being create, after the IParseable has been set as an implementation.
	 * 
	 * @param inst
	 * @param literal
	 * @return
	 * @throws ThinklabValidationException 
	 */
	public abstract void parseSpecifications(IInstance inst, String literal) throws ThinklabValidationException;
	
}
