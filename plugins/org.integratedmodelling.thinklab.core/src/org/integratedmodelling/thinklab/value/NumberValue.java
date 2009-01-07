/**
 * NumberValue.java
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
package org.integratedmodelling.thinklab.value;

import org.integratedmodelling.thinklab.KnowledgeTree;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * <p>A specialized Value that holds numbers and has all math operations with automatic unit conversion.</p>
 * <p>We keep it simple for now, and represent numbers internally as doubles, no matter what their actual semantic type is. The 
 * type is remembered and conversions are done after operations if necessary. Being Java, I'm not sure we want to go to the extent
 * we'd go in C++ to maintain efficiency in number operations.</p>
 * @author Ferdinando Villa
 */
public class NumberValue extends ParsedLiteralValue {	
	
    public double value;
    
    private NumberValue(IConcept c) throws ThinklabNoKMException {
    	super(c);
    }
    
   public NumberValue(){
      super(KnowledgeManager.Number());
      value = 0.0;
   }
    
   public NumberValue(int i) {        
       super(KnowledgeManager.Integer());
       value = i;
   }
   
   public NumberValue(double d) {
       super(KnowledgeManager.Double());
       value = d;
   }

    public NumberValue(float f) {
        super(KnowledgeManager.Float());
        value = f;
    }
        
    /**
     * The Number concept manager will validate units and numbers when numbers are read from a string.
     * @param s
     * @throws ThinklabValidationException
     * @throws ThinklabNoKMException
     */
    public NumberValue(String s) throws ThinklabException {
        super(s);
    }
    
    public NumberValue (long l) {
        super(KnowledgeManager.Long());
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
        return this;
    }

    /**
     * Return the POD object that matches the number type.
     * @return
     * @throws ThinklabException 
     */
    public Object getPODValue()  {
    	
    	if (concept.is(KnowledgeManager.IntegerType()))
    		return asInteger();
    	else if (concept.is(KnowledgeManager.LongType())) 
    		return asLong();
    	else if (concept.is(KnowledgeManager.FloatType()))
    		return asFloat();
    	else if (concept.is(KnowledgeManager.DoubleType())) 
    		return asDouble();
    	return null;
    }
    
    public boolean isLong() {
        return concept.is(KnowledgeManager.Long());
    }
    
    public boolean isFloat() {
        return concept.is(KnowledgeManager.Float());
    }

    public boolean isDouble() {
        return concept.is(KnowledgeManager.Double());
    }

    public boolean isInteger() {
        return concept.is(KnowledgeManager.Integer());
    }
    
    public double asDouble() {
        return (double)value;
    }
    
    public long asLong() {
        return (long)value;
    }
    
    public float asFloat() {
        return (float)value;
    }
    
    public int asInteger() {
        return (int)value;
    }
    
    
    @Override
    public IValue op(String op, IValue ... other) throws ThinklabInappropriateOperationException, ThinklabValueConversionException {

    	IValue ret = null;
    	if (op.equals("=")) {
    		try {
        		ret = clone();
				ret.setToCommonConcept(other[0].getConcept(), KnowledgeManager.get().getNumberType());
			} catch (ThinklabException e) {
			}
    	} else {
    		
    		if (other != null && !(other[0] instanceof NumberValue) )
    			throw new ThinklabValueConversionException("number value operator applied to non-number " + other[0].getConcept());
    		
    		NumberValue onum = (NumberValue)other[0];
    		
    		if (op.equals("+")) {
    			ret = new NumberValue(this.asDouble() + onum.asDouble());
    		} else if (op.equals("-")) {
    			ret = new NumberValue(this.asDouble() - onum.asDouble());    			
    		} else if (op.equals("*")) {
    			ret = new NumberValue(this.asDouble() * onum.asDouble());
    		} else if (op.equals("/")) {
    			ret = new NumberValue(this.asDouble() / onum.asDouble());
    		} else if (op.equals("%")) {
    			ret = new NumberValue(this.asInteger() % onum.asInteger());
    		} else if (op.equals("++")) {
    			ret = new NumberValue(this.asDouble() + 1);
    		} else if (op.equals("--")) {
    			ret = new NumberValue(this.asDouble() - 1);
    		} else if (op.equals("<<")) {
    			ret = new NumberValue(this.asInteger() << onum.asInteger());
    		} else {
    			throw new ThinklabInappropriateOperationException("number values do not support operator " + op);
    		}
    		
    		if (other != null)
    			ret.setToCommonConcept(other[0].getConcept(), KnowledgeManager.Number());
    	}
    	
    	return ret;
    }
    
    @Override
    public IValue clone() {
    	NumberValue ret = null;
    	try {
			ret = new NumberValue(concept);
			ret.value = value;
			
    	} catch (ThinklabNoKMException e) {
		}
    	return ret;
    }
    
    @Override
    public void parseLiteral(String s) throws ThinklabValidationException {
    	IValue v = null;
		try {
			v = KnowledgeManager.get().validateLiteral(KnowledgeManager.get().getNumberType(), s, null);
		} catch (ThinklabException e) {
		}
		
    	if (v == null) {
    		throw new ThinklabValidationException("validation of number " + s + " failed");
    	}
    	
    	try {
	    	concept = v.getConcept();
			value   = v.asNumber().value;
    	} catch (ThinklabValueConversionException e) {
    		throw new ThinklabValidationException(e);
		}
    }

    public String toString() {
        return Double.toString(value);
    }

	public void assign(double d) {
		value = d;
	}

	@Override
	public Object demote() {
		return value;
	}

}
