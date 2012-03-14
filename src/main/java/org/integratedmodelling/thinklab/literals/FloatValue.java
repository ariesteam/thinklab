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
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.annotation.SemanticLiteral;
import org.integratedmodelling.thinklab.api.annotations.Literal;
import org.integratedmodelling.thinklab.api.lang.IParseable;

/**
 * @author Ferdinando Villa
 */
@Literal(datatype="xsd:float", javaClass=Float.class, concept=NS.FLOAT)
public class FloatValue extends SemanticLiteral implements IParseable {	
	
   public float value;
    
   public FloatValue() {
      super(Thinklab.FLOAT);
      value = 0;
   }
    
   public FloatValue(int i) {        
       super(Thinklab.FLOAT);
       value = i;
   }
   
   public FloatValue(double d) {
       super(Thinklab.FLOAT);
       value = (float)d;
   }

    public FloatValue(float f) {
        super(Thinklab.FLOAT);
        value = f;
    }
            
    /**
     * The Number concept manager will validate units and numbers when numbers are read from a string.
     * @param s
     * @throws ThinklabValidationException
     * @throws ThinklabNoKMException
     */
    public FloatValue(String s) throws ThinklabException {
        parse(s);
    }
    
    public FloatValue (long l) {
        super(Thinklab.FLOAT);
        value = l;
    }
    
    @Override
    public void parse(String s) throws ThinklabException {
    	
    	Float v = null;
		try {
			v = Float.parseFloat(s);
		} catch (Exception e) {
	 		throw new ThinklabValidationException("validation of number " + s + " failed");
	 	 }
		concept = Thinklab.FLOAT;
		value   = v;
    }

    public String toString() {
        return ""+value;
    }

	@Override
	public Object getObject() {
		return value;
	}

	@Override
	public boolean is(Object object) {
		return (object instanceof Number) && new Float(((Number)object).floatValue()).equals(value);
	}

	@Override
	public String asText() {
		return asString();
	}

}
