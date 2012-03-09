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
import org.integratedmodelling.lang.Semantics;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.interfaces.annotations.LiteralImplementation;
import org.integratedmodelling.thinklab.knowledge.SemanticLiteral;

/**
 * <p>A specialized Value that holds numbers and has all math operations with automatic unit conversion.</p>
 * <p>We keep it simple for now, and represent numbers internally as doubles, no matter what their actual semantic type is. The 
 * type is remembered and conversions are done after operations if necessary. Being Java, I'm not sure we want to go to the extent
 * we'd go in C++ to maintain efficiency in number operations.</p>
 * @author Ferdinando Villa
 */
@LiteralImplementation(concept="thinklab-core:Number")
public class NumberValue extends SemanticLiteral implements IParseable {	
	
    public double value;
    
   public NumberValue() {
      super(Thinklab.NUMBER);
      value = 0.0;
   }
    
   public NumberValue(int i) {        
       super(Thinklab.NUMBER);
       value = i;
   }
   
   public NumberValue(double d) {
       super(Thinklab.DOUBLE);
       value = d;
   }

    public NumberValue(float f) {
        super(Thinklab.FLOAT);
        value = f;
    }
        
    public void wrap(Object o) {
    	value = ((Number)o).doubleValue();
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
        super(Thinklab.LONG);
        value = l;
    }
    
    @Override
    public void parse(String s) throws ThinklabException {
    	
    	Double v = null;
		try {
			v = Double.parseDouble(s);
		} catch (Exception e) {
	 		throw new ThinklabValidationException("validation of number " + s + " failed");
	 	 }
		concept = Thinklab.NUMBER;
		value   = v;
    }

    public String toString() {
        return ""+value;
    }

	public void assign(double d) {
		value = d;
	}

	@Override
	public Semantics getSemantics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean is(ISemanticObject object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String asText() {
		// TODO Auto-generated method stub
		return null;
	}

}
