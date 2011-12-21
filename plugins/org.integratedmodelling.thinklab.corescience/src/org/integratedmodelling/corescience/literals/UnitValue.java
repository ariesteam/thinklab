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
package org.integratedmodelling.corescience.literals;


import javax.measure.unit.Unit;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.TextValue;
import org.integratedmodelling.thinklab.literals.Value;

/**
 * Unit is both a parsed literal and a conceptual model.
 * @author Ferdinando Villa
 *
 */
public class UnitValue extends Value implements IUnitValue {
    
    protected Unit<?> unit;
    
    protected UnitValue() {
    }
    
    public UnitValue(IConcept c, String s) throws ThinklabException {
        super(KnowledgeManager.get().getTextType());
        unit = Unit.valueOf(s);
        setConceptWithValidation(c);
    }
    
    @Override
    public IValue clone() {
    	IValue ret = null;
    	try {
			ret = new UnitValue(concept, toString());
		} catch (ThinklabException e) {
		}
		return ret;
    }
    
    public boolean isNumber() {
        return false;
    }

    /* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.value.Value#setConceptWithValidation(org.integratedmodelling.ima.core.interfaces.IConcept)
	 */
	@Override
	public void setConceptWithValidation(IConcept concept) throws ThinklabValidationException {

		boolean ok = concept.is(CoreScience.MeasurementModel());		
		
		if (ok)
			super.setConceptWithoutValidation(concept);
		else
			throw new ThinklabValidationException("internal: " + concept + " is not appropriate type for a unit");
	}

	public boolean isText() {
        return false;
    }
    
    public boolean isLiteral() {
        return true;
    }

    public boolean isBoolean() {
        return false;
    }
    
    public boolean isClass() {
        return false;
    }
 
    public boolean isObject() {
        return false;
    }
    
    public TextValue asText() throws ThinklabValueConversionException {
    	return new TextValue(this.toString());
    }

    public String toString() {
        return unit.toString();
    }
    
    public Unit<?> getUnit() {
    	return unit;
    }
    
	@Override
	public Object demote() {
		return unit;
	}


}
