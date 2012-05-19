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
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.lang.IParseable;

/**
 * @author Ferdinando Villa
 */
@Literal(
		datatype="http://www.w3.org/2001/XMLSchema#double", 
		javaClass=Double.class, 
		concept=NS.DOUBLE)
public class DoubleValue extends SemanticLiteral<Double> implements IParseable {	
	
   public DoubleValue() {
      super(Thinklab.DOUBLE, Double.NaN);
   }
    
   public DoubleValue(IConcept c, Double d) {
	   super(c, d);
   }
   
   public DoubleValue(IConcept c, Integer i) {        
       super(c, i.doubleValue());
   }
   
   public DoubleValue(IConcept c, Float f) {
	   super(c, f.doubleValue());
   }
            
    public DoubleValue(String s) throws ThinklabException {
    	parse(s);
    }
    
    public DoubleValue (IConcept c, Long l) {
        super(c, l.doubleValue());
    }
    
    @Override
    public void parse(String s) throws ThinklabException {
    	
    	Double v = null;
		try {
			v = Double.parseDouble(s);
		} catch (Exception e) {
	 		throw new ThinklabValidationException("validation of number " + s + " failed");
	 	 }
		concept = Thinklab.DOUBLE;
		value   = v;
    }

    public String toString() {
        return ""+value;
    }

	@Override
	public boolean is(Object object) {
		return (object instanceof Number) && new Double(((Number)object).doubleValue()).equals(value);
	}

	@Override
	public String asText() {
		return asString();
	}

}
