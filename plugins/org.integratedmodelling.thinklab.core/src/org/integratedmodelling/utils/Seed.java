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
// purpose: a Seed is an object which can grow into something else by
//          calling a method

package org.integratedmodelling.utils;

	
/**
  * A Seed can grow into any other Object.
 **/

class Seed
  {
  Object value;


  /**
    * Create a Seed.
    *@param growable the Growable from which the final value is produced.
   **/

  Seed(Growable growable)
    {
    this.value = growable;
    }

  /**
    * grow the seed, iterating as long as the result is a Growable,
    * returning the final value.
   **/

  Object grow()
    {
    while( value instanceof Growable )
      {
      value = ((Growable)value).grow();
      }
    return value;
    }
  }
