/**
 * IInstanceImplementation.java
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
package org.integratedmodelling.thinklab.interfaces.knowledge;

import java.util.HashMap;

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
	 * Instance implementation may have metadata, which are returned here. Any use of this
	 * must be prepared to get a null as a result. 
	 * 
	 * @return
	 */
	public HashMap<String, Object> getMetadata();
	
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
