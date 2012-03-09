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
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.interfaces.annotations.LiteralImplementation;
import org.integratedmodelling.thinklab.knowledge.SemanticLiteral;

/**
 * @author Ferdinando Villa
 */
@LiteralImplementation(concept=NS.NUMBER)
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
