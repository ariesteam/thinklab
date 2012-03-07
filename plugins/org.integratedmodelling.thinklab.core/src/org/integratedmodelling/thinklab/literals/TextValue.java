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
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.interfaces.annotations.LiteralImplementation;

@LiteralImplementation(concept="thinklab-core:Text")
public class TextValue extends Value implements IParseable {
    
    public String value;
    
    private TextValue(IConcept c) {
    	super(c);
    }
    
    public TextValue() {
        super(KnowledgeManager.Text());
        value = "";
    }
    
    public TextValue(String s)  {
        super(KnowledgeManager.Text());
        value = s;
    }
    
    public void wrap(Object o) {
    	value = (String)o;
    }


    @Override
    public Object clone() {
    	TextValue ret = new TextValue(concept);
    	ret.value = value;
    	return ret;
    }
    
    public boolean isNumber() {
        return false;
    }

    public boolean isText() {
        return true;
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
    
    public String asText()  {
        return value;
    }

    public String toString() {
        return value;
    }

    @Override
	public Object demote() {
		return value;
	}

	@Override
	public void parse(String s) throws ThinklabException {
		value = s;
	}

}
