/**
 * MeasurementValue.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCoreSciencePlugin.
 * 
 * ThinklabCoreSciencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCoreSciencePlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.corescience.literals;

import javax.measure.Measure;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.NumberValue;
import org.integratedmodelling.thinklab.literals.ParsedLiteralValue;
import org.jscience.physics.amount.Amount;

/**
 * <p>A specialized Value that holds numbers with optional units, and has all math operations with automatic unit conversion.</p>
 * <p>We keep it simple for now, and represent numbers internally as doubles, no matter what their actual semantic type is. The 
 * type is remembered and conversions are done after operations if necessary. Being Java, I'm not sure we want to go to the extent
 * we'd go in C++ to maintain efficiency in number operations.</p>
 * 
 * FIXME must probably be redone from scratch and used to construct whole observations with 
 * default observables. This is where we can use a units ontology.
 *
 * @author Ferdinando Villa
 */
public class MeasurementValue extends ParsedLiteralValue {

    public double value;
    
    private Amount<?> measure;
    
   public MeasurementValue() throws ThinklabException {
      super(KnowledgeManager.get().getNumberType());
      value = 0.0;
   }
    
   public MeasurementValue(int i) throws ThinklabException {        
       super(KnowledgeManager.get().getIntegerType());
       value = i;
   }
   
   public MeasurementValue(double d) throws ThinklabException {
       super(KnowledgeManager.get().getDoubleType());
       value = d;
   }

    public MeasurementValue(float f) throws ThinklabException {
        super(KnowledgeManager.get().getFloatType());
        value = f;
    }
        
    /**
     * The Number concept manager will validate units and numbers when numbers are read from a string.
     * @param s
     * @throws ThinklabValidationException
     * @throws ThinklabNoKMException
     */
    public MeasurementValue(String s) throws ThinklabException {
        super(s);
    }
    
    public MeasurementValue(IConcept c, String s) throws ThinklabException {
    	super(s);
    	setConceptWithValidation(c);
    }
    
    public MeasurementValue (long l) throws ThinklabException {
        super(KnowledgeManager.get().getLongType());
        value = l;
    }
    
    public boolean isNumber() {
        return true;
    }

    public boolean isText() {
        return false;
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
    
    public NumberValue asNumber() throws ThinklabValueConversionException {
        NumberValue ret = new NumberValue(value);
        ret.setConceptWithoutValidation(concept);
        return ret;
    }

    public boolean isLong() {
        boolean ret = false;
        try {
        	// FIXME use classtree
            ret = concept.is(KnowledgeManager.get().getLongType());
        } catch (ThinklabException e) {
        }
        return ret;
    }
    
    public boolean isFloat() {
        boolean ret = false;
        try {
            ret = concept.is(KnowledgeManager.get().getFloatType());
        } catch (ThinklabException e) {
        }
        return ret;
    }

    public boolean isDouble() {
        boolean ret = false;
        try {
            ret = concept.is(KnowledgeManager.get().getDoubleType());
        } catch (ThinklabException e) {
        }
        return ret;
    }

    public boolean isInteger() {
        boolean ret = false;
        try {
            ret = concept.is(KnowledgeManager.get().getIntegerType());
        } catch (ThinklabException e) {
        }
        return ret;
    }
    
    public double asDouble() {
        return (double)value;
    }
    
    public double asLong() {
        return (long)value;
    }
    
    public double asFloat() {
        return (float)value;
    }
    
    public double asInteger() {
        return (int)value;
    }

    @Override
    public IValue clone() {
    	MeasurementValue ret = null;
    	try {
			/* there may be a faster way, but this will preserve the semantic types when
			 * we reimplement the UnitFormat.
			 */
			ret = new MeasurementValue(concept,measure.toString());
    	} catch (ThinklabException e) {
		}
    	return ret;
    }
    
    @Override
    public void parseLiteral(String s) throws ThinklabValidationException {

    	measure = Amount.valueOf(s);
    }

    public String toString() {
    	return measure.toString();
    }

	@Override
	public Object demote() {
		return value;
	}

}
