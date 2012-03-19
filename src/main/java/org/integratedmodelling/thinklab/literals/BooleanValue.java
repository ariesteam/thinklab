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
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.annotation.SemanticLiteral;
import org.integratedmodelling.thinklab.api.annotations.Literal;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IParseable;

@Literal(datatype="xsd:boolean", javaClass=java.lang.Boolean.class, concept=NS.BOOLEAN)
public class BooleanValue extends SemanticLiteral<Boolean> implements IParseable {
    
    public static ISemanticObject<Boolean> TRUE = new BooleanValue(true);
    public static ISemanticObject<Boolean> FALSE = new BooleanValue(false);
    
    private BooleanValue(IConcept c)  {
    	super(c);
    }
    
    public BooleanValue() {
        super(Thinklab.BOOLEAN);
        value = false;
    }

    public BooleanValue(boolean c)  {
        super(Thinklab.BOOLEAN);
        value = c;
    }

    public BooleanValue(String s) throws ThinklabException {
        parse(s);
    }

    public static BooleanValue wrap(boolean o) {
    	return new BooleanValue(o);
    }
    
    @Override
    public Object clone() {
    	BooleanValue ret = new BooleanValue(concept);
    	ret.value = value;
    	return ret;
    }
    
    @Override
    public void parse(String s) throws ThinklabException {
      value = parseBoolean(s);
    }
    
    /**
     * parse a string and see if it "means" true. Quite tolerant for now: will return true for
     * "true", "t", "1", and "yes", case-insensitive.
     * @param s
     * @return true if s means true
     */
    static public boolean parseBoolean(String s) {
    	String ss = s.toLowerCase().trim();
    	return (ss.equals("true") || ss.equals("yes") || ss.equals("t") || ss.equals("1"));
    }
    
    @Override
    public boolean asBoolean() {
        return value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    
    public Object truthValue() {
		return value;
	}

	@Override
	public boolean is(Object object) {
		// TODO Auto-generated method stub
		return false; // concept.is(object.getDirectType()) && value.equals(object.asBoolean());
	}

	@Override
	public String asText() {
		// TODO Auto-generated method stub
		return null;
	}

}
