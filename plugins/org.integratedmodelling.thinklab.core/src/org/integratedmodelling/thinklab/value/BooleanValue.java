/**
 * BooleanValue.java
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

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

public class BooleanValue extends ParsedLiteralValue {

    public boolean value;
    
    private BooleanValue(IConcept c) throws ThinklabNoKMException {
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

    public BooleanValue(String s) throws ThinklabValidationException,
            ThinklabNoKMException {
        super(s);
    }
    
    @Override
    public IValue op(String op, IValue ... other) throws ThinklabInappropriateOperationException, ThinklabValueConversionException {
    	IValue ret = null;
    	if (op.equals("=")) {
    		try {
        		ret = clone();
				ret.setToCommonConcept(other[0].getConcept(), KnowledgeManager.get().getBooleanType());
			} catch (ThinklabException e) {
			}
    	} else throw new ThinklabInappropriateOperationException("concept values do not support operator " + op);
    	return ret;
    }
    
    @Override
    public IValue clone() {
    	BooleanValue ret = null;
		try {
			ret = new BooleanValue(concept);
	    	ret.value = value;
		} catch (ThinklabNoKMException e) {
		}
    	return ret;
    }
    
    @Override
    public void parseLiteral(String s) throws ThinklabValidationException {
        // TODO have the concept manager parse true/false, yes/no etc

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
    public BooleanValue asBoolean() throws ThinklabValueConversionException {
        return this;
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
