/**
 * TextValidator.java
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
package org.integratedmodelling.thinklab.validators;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.ILiteralValidator;
import org.integratedmodelling.thinklab.interfaces.IOntology;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.TextValue;

/**
 * Concept manager for text values. Not much to do.
 * 
 * These are only necessary to enable derived ontologies to create simple literals without having to
 * pair them with new API validators: simply add Number to their superclass and any literal will be
 * passed through this.
 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 */
public class TextValidator implements ILiteralValidator {

    public IValue validate(String s, IConcept c, IOntology ontology) throws ThinklabValidationException {

    	TextValue ret = new TextValue(s);
		ret.setConceptWithValidation(c);
		
		return ret;
    }

	public void declareType() {
		// TODO Auto-generated method stub
		
	} 
}
