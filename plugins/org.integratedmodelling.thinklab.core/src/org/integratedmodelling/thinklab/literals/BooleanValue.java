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

@LiteralImplementation(concept="thinklab-core:Boolean")
public class BooleanValue extends Value implements IParseable {

    public boolean value;
    
    private BooleanValue(IConcept c)  {
    	super(c);
    }
    
    public BooleanValue() {
        super(KnowledgeManager.Boolean());
        value = false;
    }

    public BooleanValue(boolean c)  {
        super(KnowledgeManager.Boolean());
        value = c;
    }

    public BooleanValue(String s) throws ThinklabException {
        parse(s);
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
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isText() {
        return false;
    }

    @Override
    public boolean isBoolean() {
        return true;
    }
    
    @Override
    public boolean isClass() {
        return false;
    }
 
    @Override
    public boolean isObject() {
        return false;
    }
    
    @Override
    public boolean isLiteral() {
        return true;
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
	public Object demote() {
		return new Boolean(value);
	}

}
