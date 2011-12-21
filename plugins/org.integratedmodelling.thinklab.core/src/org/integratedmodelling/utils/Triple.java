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
public class Triple<T1,T2, T3> implements Serializable
{
	private static final long serialVersionUID = 8017164648757388295L;
	protected T1 first = null;
	protected T2 second = null;
    protected T3 third = null;
	/**
	 *  Pair constructor comment.
	 */
	public Triple()
	{
	}
	/**
	 *  This method was created in VisualAge.
	 *
	 * @param  first java.lang.Object
	 * @param  second java.lang.Object
	 */
	public Triple(T1 first, T2 second, T3 third)
	{
		this.first = first;
		this.second = second;
        this.third = third;
	}
	/**
	 *  This method was created in VisualAge.
	 *
	 * @param  newValue java.lang.Object
	 */
	public void setFirst(T1 newValue)
	{
		this.first = newValue;
	}
	/**
	 *  This method was created in VisualAge.
	 *
	 * @param  newValue java.lang.Object
	 */
	public void setSecond(T2 newValue)
	{
		this.second = newValue;
	}
    /**
     *  This method was NOT created in VisualAge.
     *
     * @param  newValue java.lang.Object
     */
    public void setThird(T3 newValue)
    {
        this.third = newValue;
    }
	/**
	 *  This method was created in VisualAge.
	 *
	 * @return  java.lang.Object
	 */
	public T1 getFirst()
	{
		return first;
	}
	/**
	 *  This method was created in VisualAge.
	 *
	 * @return  java.lang.Object
	 */
	public T2 getSecond()
	{
		return second;
	}
    
    public T3 getThird() {
        return third;
    }
	/**
	 *  This method was created in VisualAge.
	 *
	 * @return  java.lang.String
	 */
	public String toString()
	{
		return "{" + getFirst() + "," + getSecond() + "," + getThird() + "}";
	}
}
