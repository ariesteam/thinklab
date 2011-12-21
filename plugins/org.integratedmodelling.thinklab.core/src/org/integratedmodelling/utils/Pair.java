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

import java.io.Serializable;
/**
  * Stupid generic pair class.
 */
public class Pair<T1,T2> implements Serializable
{

	private static final long serialVersionUID = 1L;
	protected T1 first = null;
	protected T2 second = null;
	/**
	 *  Pair constructor comment.
	 */
	public Pair()
	{
	}

	public Pair(T1 first, T2 second)
	{
		this.first = first;
		this.second = second;
	}

	public void setFirst(T1 newValue)
	{
		this.first = newValue;
	}

	public void setSecond(T2 newValue)
	{
		this.second = newValue;
	}
	
	public T1 getFirst()
	{
		return first;
	}

	public T2 getSecond()
	{
		return second;
	}

	
	public String toString()
	{
		return "{" + getFirst() + "," + getSecond() + "}";
	}
	
	@Override
	public boolean equals(Object obj) {

		return ((Pair<?,?>)obj).first.equals(first) && ((Pair<?,?>)obj).second.equals(second);
	}
	
	@Override
	public int hashCode() {
		return first.hashCode() + second.hashCode();
	}

	
	
}
