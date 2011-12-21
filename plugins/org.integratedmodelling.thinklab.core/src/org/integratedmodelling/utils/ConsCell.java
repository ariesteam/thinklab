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
// author:  Robert Keller
// purpose: polycell used in implementation of Polylists

package org.integratedmodelling.utils;

	
/**
  *  NonEmptyList is the sub-class of List consisting of non-empty 
  *  lists.  Every NonEmptyList has a first and a rest.
 **/

class ConsCell
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

  ConsCell(Object First, Object Rest)
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
