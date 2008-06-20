/**
 * SortableCollection.java
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
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
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
 * This interface is even more abstract than Sortable.  It abstracts
 * the nature of the collection and just treats it as a sequence that
 * is controlled by this interface.  It is assumed that each item in
 * the collection can be referred to by a number, and that every
 * non-negative number less than the number of items in the collection
 * refers to a unique element.
 **/
public interface SortableCollection
{
    /**
     * Returns the number of elements in the sortable collection.
     **/
    public int getLength ();

    
    /** 
     * Returns the default sorting method for this SortableCollection
     * type.
     **/
    public int getDefaultSortingMethod();
    

    /** Returns true iff, in a list ordered according to the predicate
     * specified by method, the object with index a is greater than
     * index b.
     *
     * @param test The object to test
     * @param method The sorting predicate to use
     **/
    public boolean greaterThan (int a, int b, int method);

    
    /**
     * Instructs the collection that the object with index a should
     * have its position swapped with that of the object at index b.
     * Hereafter, the sorter will assume that the object formerly
     * associated with index a will now be associated with index b,
     * and vice versa.
     **/
    public void swap (int a, int b, int method);
}

