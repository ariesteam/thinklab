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


/*
 * Copyright (C) 1999-2003 Russell Heywood @ devtools.org
 * All Rights Reserved.
 *
 * It is illegal to distribute this file under conditions other than
 * those expressly permitted in the GNU General Public License.  You
 * should have received a copy of the GPL with the distribution
 * containing this source file.
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 * 
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version. 
 */

/** 
 * This class serves as a helper for sorting strings using the JDK's
 * compareTo operator.  Just construct some of these, bind whatever
 * values you need to them, and sort using any of the joyous sorting
 * methods.
 **/
public class StringSortable implements Sortable
{
    /** 
     * The thing to actually sort by
     **/
    public String key;
    
    
    /** 
     * The value that was hung upon this StringSortable.
     **/
    public Object value;
    
    
    /** 
     * Constructs a StringSortable with the given String key and
     * Object value.  The value is never touched by StringSortable,
     * and can be anything (even null.)
     *
     * @param key The string to sort by
     *
     * @param val A convenience ref that you can hang additional data
     * off of.
     **/
    public StringSortable (String key, Object val)
    {
	this.key = key;
	this.value = val;
    }
    
    /** 
     * Returns the default sorting method for this Sortable type.  For
     * strings, there's just one method.
     **/
    public int getDefaultSortingMethod()
    {
	return 0;
    }
    
    
    
    /** 
     * Returns false iff, in a list ordered according to the predicate
     * specified by method, this object is greater than the test object.
     *
     * @param test The object to test
     * @param method The sorting predicate to use
     */
    public boolean greaterThan( Object test, int method )
    {
	try {
	    boolean res = (key.compareTo(((StringSortable)test).key) < 0);
	    return res;
	    
	} catch (ClassCastException cce) {
	    throw new IllegalArgumentException
		("Can only sort StringSortables.");
	}
    }
    
    
    /** 
     * For your convenience: constructs an array of StringSortables
     * out of a JDK Enumeration of Strings.  The values are all null.
     **/
    public static Sortable[] convertEnumeration (java.util.Enumeration e)
    {
	StringSortable[] rv = new StringSortable[1024];
	int i = 0;
	
	while (e.hasMoreElements()) {
	    if (i==rv.length) {
		StringSortable[] t = new StringSortable[rv.length*2];
		System.arraycopy (rv, 0, t, 0, i);
		rv = t;
	    }
	    
	    rv[i++] = new StringSortable((String)e.nextElement(),null);
	}
	
	Sortable[] f = new Sortable[i];
	System.arraycopy (rv, 0, f, 0, i);
	return f;
    }
    
    
    /** 
     * For your convenience: constructs an array of StringSortables
     * out of a JDK Hashtable whose keys are all Strings.  The values
     * are set to the mapped Hashtable values.
     **/
    public static Sortable[] convertHashtable (java.util.Hashtable h)
    {
	Sortable[] rv = convertEnumeration (h.keys());
	
	for (int i=0; i<rv.length; ++i)
	    ((StringSortable)rv[i]).value = h.get(((StringSortable)rv[i]).key);
	
	return rv;
    }
    
    
    /** 
     * For your convenience: constructs an array of StringSortables
     * out of an array of Strings.  The values of the StringSortables
     * are set to null.
     **/
    public static Sortable[] convertArray (String[] s)
    {
	Sortable[] rv = new Sortable[s.length];
	
	for (int i=0; i<s.length; ++i)
	    rv[i] = new StringSortable (s[i],null);
	
	return rv;
    }
    
    
    
    /**
     * For your convenience: in-place sorts the given array of
     * Strings, using the given Sorter.
     **/
    public static void sortArray (String[] array, Sorter s)
    {
	Sortable[] data = convertArray (array);
	s.sort (data);
	for (int i=0; i < data.length; ++i)
	    array[i] = ((StringSortable)data[i]).key;
    }
}




