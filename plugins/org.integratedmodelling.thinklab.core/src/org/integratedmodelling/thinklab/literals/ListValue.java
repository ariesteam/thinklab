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

import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.MalformedListException;
import org.integratedmodelling.utils.Polylist;

public class ListValue extends ParsedLiteralValue {

    protected Polylist value;
    
    public ListValue() throws ThinklabNoKMException {
        super();
    }

    public ListValue(IConcept c) throws ThinklabNoKMException {
        super(c);
    }

    public ListValue(String s) throws ThinklabValidationException {
        try {
            value = Polylist.parse(s);
        } catch (MalformedListException e) {
            throw new ThinklabValidationException(e);
        }
    }
    
    public ListValue(Polylist s) {
        value = s;
    }
    
    @Override
    public void parseLiteral(String s) throws ThinklabValidationException {
        try {
            value = Polylist.parse(s);
        } catch (MalformedListException e) {
            throw new ThinklabValidationException(e);
        }        
    }
 
    public Polylist getList() {
        return value;
    }
    
    public boolean isList() {
        return true;
    }
    
    public String toString() {
    	return value == null ? "nil" : (value + " [" + getConcept() + "]");
    }
    
	@Override
	public Object demote() {
		return value;
	}


}
