/**
 * polycell.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 Robert Keller and www.integratedmodelling.org
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
// purpose: polycell used in implementation of Polylists

package org.integratedmodelling.utils;

	
/**
  *  NonEmptyList is the sub-class of List consisting of non-empty 
  *  lists.  Every NonEmptyList has a first and a rest.
 **/

class polycell
  {
  private Object First;
  private Object Rest;


  /**
    *  first() returns the first element of a NonEmptyList.
   **/ 

  Object first()
    {
    return First;
    }


  /**
    *  rest() returns the rest of a NonEmptyList.
   **/ 

  Polylist rest()
    {
    if( Rest instanceof Seed ) 
      {
      Rest = ((Seed)Rest).grow();
      }
    return (Polylist)Rest;
    }


  /**
    *  polycell is the constructor for the cell of a Polyist, 
    *  given a First and a Rest.
    *
    *  Use static method cons of class Polylist to avoid using 'new' 
    *  explicitly.
   **/ 

  polycell(Object First, Object Rest)
    {
    this.First = First;
    this.Rest = Rest;
    }


  /**
    *  setFirst() sets the first element of a NonEmptyList.
   **/ 

  void setFirst(Object value)
    {
    First = value;
    }
  }  // class polycell
