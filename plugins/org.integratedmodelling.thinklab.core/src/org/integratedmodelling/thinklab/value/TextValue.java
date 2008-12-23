/**
 * TextValue.java
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
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IValue;

public class TextValue extends Value {
    
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
    
    @Override
    public IValue op(String op, IValue ... other) throws ThinklabInappropriateOperationException, ThinklabValueConversionException {
    	IValue ret = null;
    	if (op.equals("=")) {
    		try {
        		ret = clone();
				ret.setToCommonConcept(other[0].getConcept(), KnowledgeManager.get().getRootConcept());
			} catch (ThinklabNoKMException e) {
			}
    	} else {
    		
    		if (other != null && !(other[0] instanceof TextValue) )
    			throw new ThinklabValueConversionException("text value operator applied to non-text " + other[0].getConcept());
    		
    		TextValue onum = (TextValue)other[0];

    		if (op.equals("+")) {
    			ret = new TextValue(value + onum.value);
    		} else {
    			throw new ThinklabInappropriateOperationException("text values do not support operator " + op);
    		}
			ret.setToCommonConcept(other[0].getConcept(), KnowledgeManager.Text());
    	}
    	return ret;
    }
    
    @Override
    public IValue clone() {
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
    
    public TextValue asText() throws ThinklabValueConversionException {
        return this;
    }

    public String toString() {
        return value;
    }

    @Override
	public Object demote() {
		return value;
	}

}
