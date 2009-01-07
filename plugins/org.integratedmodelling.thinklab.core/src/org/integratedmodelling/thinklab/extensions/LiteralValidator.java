/**
 * ILiteralValidator.java
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
package org.integratedmodelling.thinklab.extensions;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

public interface LiteralValidator {

	/**
	 * Validate a literal string to a value of the passed concept. The third parameter is the ontology
	 * that this is ending up into, which is only used if the validation produces an IInstance and
	 * the resulting IValue is an ObjectReferenceValue. 
	 * 
	 * @param literalValue
	 * @param concept
	 * @param ontology
	 * @return
	 * @throws ThinklabValidationException
	 */
	public abstract IValue validate(String literalValue, IConcept concept, IOntology ontology) throws ThinklabValidationException;
	
	/**
	 * This one is called once when the type is installed. It is the place to put operator declarations, or any
	 * one-time initialization actions that depend on the KM being initialized.
	 */
	public abstract void declareType();
	
}
