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
 * In-place sorts an array of sortable data objects using selection
 * sort.  This shouldn't even really exist, except that QuickSorter is
 * broken because I am too stupid to get it working.
 **/
public class SelectionSorter implements Sorter
{
    private static SelectionSorter me = null;
    
    /** 
     * A static accessor so that this is a singleton
     **/
    public static Sorter get()
    {
	if (me==null) me = new SelectionSorter();
	return me;
    }
    

    
    /**
     * A helper class so that we can just implement sorting one way
     **/
    private final static class SortableArrayCollection
	implements SortableCollection
    {
	private Sortable[] data;
	
	public SortableArrayCollection (Sortable[] data) {
	    this.data = data;
	}

	public int getLength () {
	    return data.length;
	}

	public int getDefaultSortingMethod () {
	    return data[0].getDefaultSortingMethod ();
	}

	public boolean greaterThan (int a, int b, int method) {
	    return data[a].greaterThan (data[b], method);
	}

	public void swap (int a, int b, int method) {
	    Sortable t = data[a];
	    data[a] = data[b];
	    data[b] = t;
	}
    }
    
    
    
    /**
     * Sorts some Sortable data using Selection sort
     **/
    public void sort (Sortable data[])
    {
	sort(new SortableArrayCollection (data));
    }
    
    
    public void sort (Sortable data[], int method)
    {
	if( data.length < 2 )
	    return;
	sort(new SortableArrayCollection (data), method);
    }


    public void sort (SortableCollection data)
    {
    	if(data.getLength() >0) {
			sort (data, data.getDefaultSortingMethod ());
    	}
    }


    public void sort (SortableCollection data, int method)
    {
	// So, how does this work again?  Scan for the lowest element
	// and swap it with the one in front.
	int len = data.getLength ();
	for (int i = 0; i < len; ++i)
	    select (data, i, method, len);
    }
    
    
    private void select (SortableCollection data, int start, int m, int alen)
    {
	int min = start;
	
	for (int i=start+1; i < alen; ++i)
	    if (data.greaterThan (min, i, m))
		min = i;
	
	if (min != start)
	    data.swap (min, start, m);
    }
}

