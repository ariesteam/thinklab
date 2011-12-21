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
package org.integratedmodelling.thinklab.literals;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.interfaces.annotations.LiteralImplementation;

/**
 * <p>A specialized Value that holds numbers and has all math operations with automatic unit conversion.</p>
 * <p>We keep it simple for now, and represent numbers internally as doubles, no matter what their actual semantic type is. The 
 * type is remembered and conversions are done after operations if necessary. Being Java, I'm not sure we want to go to the extent
 * we'd go in C++ to maintain efficiency in number operations.</p>
 * @author Ferdinando Villa
 */
@LiteralImplementation(concept="thinklab-core:Number")
public class NumberValue extends Value implements IParseable {	
	
    public double value;
    
    private NumberValue(IConcept c)  {
    	super(c);
    }
    
   public NumberValue() {
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
        parse(s);
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
    
    public NumberValue asNumber() {
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
    	
    	return asDouble();
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
    public Object clone() {
    	NumberValue ret = null;
    	ret = new NumberValue(concept);
    	ret.value = value;

    	return ret;
    }
    
    @Override
    public void parse(String s) throws ThinklabException {
    	
    	Double v = null;
		try {
			v = Double.parseDouble(s);
		} catch (Exception e) {
	 		throw new ThinklabValidationException("validation of number " + s + " failed");
	 	 }
		concept = KnowledgeManager.Number();
		value   = v;
    }

    public String toString() {
        return ""+value;
    }

	public void assign(double d) {
		value = d;
	}

	@Override
	public Object demote() {
		return value;
	}

}
