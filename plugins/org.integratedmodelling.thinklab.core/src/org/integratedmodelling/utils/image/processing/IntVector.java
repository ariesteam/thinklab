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
package org.integratedmodelling.utils.image.processing ;

import java.io.Serializable;

/**
 * A fast dynamic array of primitive integers
 *
 * @author <A href=http://www.ctr.columbia.edu/~dzhong>Di Zhong (Columbia University)</a>
 */
public class IntVector extends Object implements Serializable{

  /**
	 * The actual integer array where one can get values
	 */
	public int [] v=null ;

	private int inc ;
	private int size ;
	private int n ;

	/**
	 * @param i_inc step size of buffer increasement
	 * @param i_size initial buffer size
	 */
	public IntVector(int i_inc, int i_size) {
		inc=i_inc ;
		size=i_size ;
		n=0 ;
		v=new int[size] ;
	}

	public IntVector() {
		size=80; inc=200 ;
		n=0 ;
		v=new int[size] ;
	}

  /**
	 * Number of integers in the array
	 */
	public int number() {
	  return n ;
	}

  /**
	 * Add an integer to the array
	 */
	public void add(int value) {

		if(n>=size) {
			int[] tmp=new int[size+inc] ;
      System.arraycopy(v, 0, tmp, 0, size);
			size+=inc ;
			v=tmp ;
		}
	  v[n++]=value ;

	}
}
