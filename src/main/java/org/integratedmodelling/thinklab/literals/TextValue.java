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
import org.integratedmodelling.thinklab.api.lang.IParseable;

@Literal(
	concept=NS.TEXT, 
	datatype="http://www.w3.org/2001/XMLSchema#string", 
	javaClass=String.class)
public class TextValue extends SemanticLiteral<String> implements IParseable {
    
    public TextValue() {
        super(Thinklab.TEXT, "");
    }
    
    public TextValue(String s)  {
        super(Thinklab.TEXT, s);
    }
    
    public TextValue(IConcept c, String s) {
    	super(c, s);
    }

	@Override
	public boolean is(Object object) {
		return object instanceof String && value.equals(object.toString());
	}

	@Override
	public void parse(String string) throws ThinklabException {
		value = string;
	}

	@Override
	public String toString() {
		return value;
	}
	
	@Override
	public String asText() {
		return value;
	}
}
