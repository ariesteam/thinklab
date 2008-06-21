/**
 * NumberValidator.java
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

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.extensions.LiteralValidator;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IOntology;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.NumberValue;

/**
 * Concept manager for simple number literals, without units or semantic types. See new JIMTCoreScience
 * plug-in for the whole sheebang.
 * 
 * These are only necessary to enable derived ontologies to create simple literals without having to
 * pair them with new API validators: simply add Number to their superclass and any literal will be
 * passed through this.
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 * 
 */
public class NumberValidator implements LiteralValidator {


	public void declareType() {
		
		NumberValue.declareOperator(NumberValue.class, "=",  null, KnowledgeManager.Number().toString());
		NumberValue.declareOperator(NumberValue.class, "+",  KnowledgeManager.Number().toString(), KnowledgeManager.Number().toString());
		NumberValue.declareOperator(NumberValue.class, "-",  KnowledgeManager.Number().toString(), KnowledgeManager.Number().toString());
		NumberValue.declareOperator(NumberValue.class, "*",  KnowledgeManager.Number().toString(), KnowledgeManager.Number().toString());
		NumberValue.declareOperator(NumberValue.class, "/",  KnowledgeManager.Number().toString(), KnowledgeManager.Number().toString());
		NumberValue.declareOperator(NumberValue.class, "++", KnowledgeManager.Number().toString(), KnowledgeManager.Number().toString());
		NumberValue.declareOperator(NumberValue.class, "--", KnowledgeManager.Number().toString(), (String[])null);
		NumberValue.declareOperator(NumberValue.class, "<<", KnowledgeManager.Number().toString(), (String[])null);
		NumberValue.declareOperator(NumberValue.class, "<",  KnowledgeManager.Number().toString(), KnowledgeManager.Boolean().toString());
		NumberValue.declareOperator(NumberValue.class, "<=", KnowledgeManager.Number().toString(), KnowledgeManager.Boolean().toString());
		NumberValue.declareOperator(NumberValue.class, ">",  KnowledgeManager.Number().toString(), KnowledgeManager.Boolean().toString());
		NumberValue.declareOperator(NumberValue.class, "<<", KnowledgeManager.Number().toString(), KnowledgeManager.Boolean().toString());
		NumberValue.declareOperator(NumberValue.class, "==", KnowledgeManager.Number().toString(), KnowledgeManager.Boolean().toString());
		NumberValue.declareOperator(NumberValue.class, "!=", KnowledgeManager.Number().toString(), KnowledgeManager.Boolean().toString());
		
	}

	
    public IValue validate(String s, IConcept c, IOntology ontology) throws ThinklabValidationException {
 
    	NumberValue n = null;
	
    	double dd = 0.0;
    	
    	try {
    		dd = Double.parseDouble(s);
    	} catch (Exception e) {
    		throw new ThinklabValidationException(e);
    	}
    	
    	
    	try {
    		// FIXME use classtree
    		
			if (c.is(KnowledgeManager.get().getIntegerType()))
				n = new NumberValue(new Double(dd).intValue());
			else if (c.is(KnowledgeManager.get().getLongType()))
				n = new NumberValue(new Double(dd).longValue());
			else if (c.is(KnowledgeManager.get().getFloatType()))
				n = new NumberValue(new Double(dd).floatValue());
			else 
				n = new NumberValue(dd);
			
		} catch (ThinklabException e) {
			throw new ThinklabValidationException(e);
		}
		
		
		if (n != null) {
			n.setConceptWithValidation(c);
		}

		return n;
    }
}
