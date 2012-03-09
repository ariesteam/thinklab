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
package org.integratedmodelling.thinklab.metadata;

import java.util.HashMap;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.modelling.metadata.IMetadata;

/**
 * For now, simply a conceptualizable hash map that knows the DC constants. Use 
 * conceptualizable objects for the values, and everything will be OK.
 * 
 * @author Ferd
 *
 */
@Concept(NS.METADATA)
public class Metadata implements IMetadata {

	@Property(NS.METADATA_HAS_FIELD)
	public HashMap<String, Object> _md = new HashMap<String, Object>();
	
	@Override
	public Object get(String string) {
		return _md.get(string);
	}

	public void put(String key, Object value) {
		_md.put(key, value);
	}
}
