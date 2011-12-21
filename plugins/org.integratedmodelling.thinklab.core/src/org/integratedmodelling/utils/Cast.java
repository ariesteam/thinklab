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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Utility to reduce the ugliness of casting generic collections in Java. If you 
 * have say a Collection<A> (ca) that you know is a Collection<B extends A> and you need
 * a Collection<B>, do the following:
 * 
 * Collection<B> cb = new Cast<A,B>.cast(ca);
 * 
 * and type safety be damned. This will not generate any warning and will avoid 
 * any silly copy. Works for generic collections, arraylists and hashsets - add
 * more if needed. Needs something else for maps.
 * 
 * @author ferdinando.villa
 *
 * @param <B>
 * @param <T>
 */
public class Cast<B, T extends B> {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<T> cast(Collection<B> b) {
		return (Collection<T>)(Collection)b;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList<T> cast(ArrayList<B> b) {
		return (ArrayList<T>)(ArrayList)b;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HashSet<T> cast(HashSet<B> b) {
		return (HashSet<T>)(HashSet)b;
	}
}