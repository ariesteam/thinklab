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
package org.integratedmodelling.thinklab.interfaces.storage;

import java.util.Map;

import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IValue;

/**
 * Used only for proxying within the Clojure subsystem.
 * 
 * An object that can be passed to a kbox to define and compute fields that will be accessible for query at the object
 * level. According to implementation, these can be computed from the actual objects inserted.
 * 
 * @author Ferdinando
 *
 */
public interface IMetadataExtractor {

	/**
	 * 
	 * @param field
	 * @param object
	 * @return
	 */
	public abstract Map<String, IValue> extractMetadata(IInstance object);

}
