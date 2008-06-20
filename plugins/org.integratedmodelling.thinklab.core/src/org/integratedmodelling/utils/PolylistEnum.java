/**
 * PolylistEnum.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 Robert Keller and www.integratedmodelling.org
 * @author    Robert Keller (original, polya package)
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
// author:  Robert Keller
// purpose: Polylist enumeration class of polya package

package org.integratedmodelling.utils;


/**
  *  PolylistEnum is an enumeration class for the class Polylist.  It 
  *  implements the interface java.util.Enumeration, i.e. the methods:
  *  hasMoreElements() and nextElement().  
 **/

public class PolylistEnum implements java.util.Enumeration
  {
  Polylist L;			// current list
 
  /**
    *  PolylistEnum constructs a PolylistEnum from a Polylist.
   **/

  public PolylistEnum(Polylist L)	// constructor
    {
    this.L = L;
    }


  /**
    *  hasMoreElements() indicates whether there are more elements left in 
    *  the enumeration.
   **/

  public boolean hasMoreElements()
    {
    return L.nonEmpty();
    }


  /**
    *  nextElement returns the next element in the enumeration.
   **/

  public Object nextElement() 
    {
    if( L.isEmpty() )
      throw new 
        java.util.NoSuchElementException("No next element in Polylist");

    Object result = L.first();
    L = L.rest();
    return result;
    }
  }

