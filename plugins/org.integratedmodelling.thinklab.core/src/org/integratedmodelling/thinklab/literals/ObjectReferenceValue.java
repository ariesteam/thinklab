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

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

public class ObjectReferenceValue extends Value {

	IInstance val;
	
    public ObjectReferenceValue() throws ThinklabNoKMException {
        super();
    }

    public ObjectReferenceValue(IInstance c) {
    	super(c.getDirectType());
    	val = c;
    }

    public ObjectValue asObject() throws ThinklabValueConversionException {
        ObjectValue ret = null;
        try {
            ret = new ObjectValue(val);
        } catch (ThinklabException e) {
            throw new ThinklabValueConversionException(e);
        }
        return ret;
    }

    public ObjectReferenceValue asObjectReference() throws ThinklabValueConversionException {
        return this;
    }

    
    public boolean isClass() {
        return false;
    }
 
    public boolean isObject() {
        return false;
    }
    
    public boolean isObjectReference() {
        return true;
    }
    
    public IInstance getObject() {
    	return val;
    }
    
	@Override
	public Object demote() {
		return val;
	}

}
