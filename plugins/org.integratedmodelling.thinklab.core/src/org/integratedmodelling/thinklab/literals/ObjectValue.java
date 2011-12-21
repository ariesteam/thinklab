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
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

/**
 * An ObjectValue contains an object (IInstance) by value, not by reference, represented as a list. This way we
 * can pass instances around without carrying the ontology or session with us. A session must be passed if the
 * object needs to be retrieved as such. 
 * @author villa
 */
public class ObjectValue extends ListValue {

    public ObjectValue() throws ThinklabNoKMException {
        super();
    }

    public ObjectValue(IInstance c) throws ThinklabException {
        super(c.toList(null));
        concept = c.getDirectType();
    }

    public ObjectValue asObject() throws ThinklabValueConversionException {
        return this;
    }

    public IInstance getObject(ISession s) throws ThinklabException {
       return s.createObject(value);
    }
    
    public boolean isClass() {
        return false;
    }
 
    public boolean isObject() {
        return true;
    }
}
