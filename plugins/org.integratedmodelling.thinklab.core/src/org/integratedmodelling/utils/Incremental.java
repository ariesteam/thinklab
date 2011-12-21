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
// purpose: Class Incremental of poly package

package org.integratedmodelling.utils;

public class Incremental extends Polylist
  {
  Object value;

  public Object first()
    {
    ensureGrown();
    return ((Polylist)value).first();
    }

  public Polylist rest()
    {
    ensureGrown();
    return ((Polylist)value).rest();
    }

  public boolean isEmpty()
    {
    ensureGrown();
    return ((Polylist)value).isEmpty();
    }

  public boolean nonEmpty()
    {
    ensureGrown();
    return ((Polylist)value).nonEmpty();
    }

  public String toString()
    {
    if( value instanceof Growable )
      return "...";
    else
      return ((Polylist)value).toString();
    }

  public Incremental(Growable growable)
    {
    value = growable;
    }

  public void ensureGrown()
    {
    while( value instanceof Growable )
      {
      value = ((Growable)value).grow();
      }
    }

  // use with caution!

  public boolean grown()
    {
    return !(value instanceof Growable);
    }

  public Polylist getList()
    {
    ensureGrown();
    return (Polylist)value;
    }
  }
