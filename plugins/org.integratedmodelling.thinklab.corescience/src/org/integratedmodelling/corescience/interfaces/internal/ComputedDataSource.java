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
package org.integratedmodelling.corescience.interfaces.internal;

import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public interface ComputedDataSource {
    /**
     * Called for each stateful observable declared as a dependency in the observation. Must prepare
     * to use it according to the passed concept, or refuse it by throwing exceptions.
     * 
     * @param observable
     * @param type
     * @param register the index of the state in the register array passed to getValue()
     * @throws ThinklabValidationException
     */
    public void notifyDependency(IConcept observable, IConcept type, int register)  throws ThinklabValidationException;
    
    /**
     * Called once after all dependencies have been notified. Should ensure that all the needed 
     * dependencies have been notified. Has a chance to return a different
     * datasource (e.g. optimized) that will be used instead. Otherwise return self - never null.
     * 
     * @throws ThinklabValidationException
     */
    public IDataSource<?> validateDependencies() throws ThinklabValidationException;
    
}
