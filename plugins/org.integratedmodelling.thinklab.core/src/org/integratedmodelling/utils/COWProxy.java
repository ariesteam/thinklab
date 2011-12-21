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
package org.integratedmodelling.utils;

/**
 * Simple helper class to implement a copy-on-write pattern. No type safety (Java has no
 * const), just makes you aware of what you're doing with the two get methods. 
 * 
 * @author Ferdinando Villa
 *
 */
public abstract class COWProxy<T> {

	private T object;
	private boolean docopy = true;
	
	public COWProxy(T object) {
		this.object = object;
	}

	public COWProxy(T object, boolean doCopy) {
		this.object = object;
		docopy = doCopy; 
	}

	public abstract T clone(T object);

	public T getReadOnly() {
		return object;
	}
	
	public T getWritable() {
		if (docopy) 
			object = clone(object);
		return object;
	}
}
