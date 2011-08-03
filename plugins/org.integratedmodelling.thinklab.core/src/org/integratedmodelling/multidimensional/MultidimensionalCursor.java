/**
 * MultidimensionalCursor.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCoreSciencePlugin.
 * 
 * ThinklabCoreSciencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCoreSciencePlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.multidimensional;

import java.util.ArrayList;

import org.integratedmodelling.collections.Triple;

public class MultidimensionalCursor {

	public enum StorageOrdering {
		ROW_FIRST,   // the first dimension in defineDimensions() varies slowest in internal ordering
		COLUMN_FIRST;
	}
	
	/**
	 * At this stage we would only need a vector, but this can come in handy to support
	 * descending dimensions later.
	 * @author Ferdinando Villa
	 *
	 */
	private class StorageOrder {
		
		public ArrayList<Integer> ordering = new ArrayList<Integer>();
		
		void set(int dimensions, StorageOrdering order) {
			
			ordering.clear();
			
			for (int i = 0; i < dimensions; i++) {
				if (order == StorageOrdering.ROW_FIRST) {
					ordering.add(dimensions - 1 - i);
				} else if (order == StorageOrdering.COLUMN_FIRST) {
					ordering.add(i);
				}
			}
		}	    
	}
	
	int multiplicity; 
	int dimensions;
    ArrayList<Integer> extents = new ArrayList<Integer>();
    ArrayList<Integer> strides = new ArrayList<Integer>();
    StorageOrdering    storageOrderType;
    StorageOrder       storageOrder = new StorageOrder();
    int[] ordering = null;

    public MultidimensionalCursor(StorageOrdering order) {
    	multiplicity = 0;
    	dimensions = 0;
    	storageOrderType = order;
    }
    
    public MultidimensionalCursor() {
    	this(StorageOrdering.ROW_FIRST);
    }
    
    public MultidimensionalCursor(MultidimensionalCursor cursor) {
    	multiplicity = cursor.multiplicity;
    	dimensions = cursor.dimensions;
    	storageOrderType = cursor.storageOrderType;
    	extents = cursor.extents;
    	storageOrder = cursor.storageOrder;
    	ordering = cursor.ordering;
    	strides = cursor.strides;
    }

    public void reset() {
    	multiplicity = 0;
    	extents.clear();
    	strides.clear();
    }

    private int initializeStrides() {

    	int stride = 1; 
    	multiplicity = 1;
    	strides.clear(); 
    	for (int n = 0; n != dimensions; ++n)
    	  strides.add(0);
    	ordering = new int[dimensions];
    	storageOrder.set(dimensions, storageOrderType);
    	for (int n = 0; n != dimensions; ++n) {
    		ordering[n] = storageOrder.ordering.get(n);
    		strides.set(storageOrder.ordering.get(n), stride);
    	    stride *= extents.get(storageOrder.ordering.get(n));
    	    multiplicity *= extents.get(storageOrder.ordering.get(n));
    	}
    	    	
    	return multiplicity;
    }

    /**
     * FIXME I'm not sure this works.
     * @param offset
     * @return
     */
    public int[] getElementIndexes(int offset) {
    	
    	int[] ret = new int[dimensions];
    	int rest = offset;
    	
    	if (dimensions == 0)
    		return ret;
    	
    	if (storageOrderType == StorageOrdering.COLUMN_FIRST) {
    		for (int i = dimensions - 1; i > 0; i--) {
    			ret[i] = offset/strides.get(i);
    			rest -= ret[i] * strides.get(i);
    		}
    		ret[0] = rest;
    	} else {
    		for (int i = 0; i < dimensions-1; i++) {
    			ret[i] = offset/strides.get(i);
    			rest -= ret[i]*strides.get(i);
    		}
    		ret[dimensions-1] = rest;
    	}
    	
    	return ret;
    }
   
    /**
     * 
     * @param indices
     * @return
     */
    public int getElementOffset(int ... indices)
    {
    	int offset = 0;
    	for (int n = 0; n < dimensions; ++n)
    		offset += indices[n] * strides.get(n);
    	return offset;
    }

    
    /**
	 * returns the three offsets needed to implement a strided scanner - start
	 * offset, past end offset, and stride - over a specified dimension. Must be
	 * passed the dimension int and a vector with all the remaining offsets -
	 * the one from dimension dim is ignored.
	 */
    public Triple<Integer, Integer, Integer> getStridedOffsets(int dimension, int[] intes) {
    	
    	intes[dimension] = 0; 
    	int ofs = getElementOffset(intes);
    	intes[dimension] = 1; 
    	int stp = getElementOffset(intes) - ofs;
    	intes[dimension] = extents.get(dimension); 
    	int end = getElementOffset(intes); 
    	return new Triple<Integer, Integer, Integer>(ofs, end, stp);
    }
    
    /**
     * 
     * @param extents
     * @return
     */
    public int defineDimensions(int ... extents) {

    	reset();
    	
    	dimensions = extents == null ? 0 : extents.length;

    	if (extents != null)
    		for (int ii : extents)
    			this.extents.add(ii);

    	return initializeStrides();
    }


    /** the extent of the specified dimension. We only support
	  0-based extents for now. */
    public int getDimensionSize(int nDim) {
    	return extents.get(nDim);
    }

    /**
     * 
     * @return
     */
    public int getDimensionsCount() {
    	return dimensions;
    }

    public int[] getExtents() {
    	int[] ret = new int[extents.size()];
    	int i = 0;
    	for (int ex : extents) {
    		ret [i++] = ex;
    	}
    	return ret;
    }
    
    /**
     * 
     * @return
     */
    public int getMultiplicity() {
    	return multiplicity;
    }
    
    public static void main(String[] args) {
    	
    	int[][] data = {
    			{0,1,2,3,4,5,6},
    			{7,8,9,10,11,12,13},
    			{14,15,16,17,18,19,20}};
    	
    	MultidimensionalCursor md = 
    		new MultidimensionalCursor(MultidimensionalCursor.StorageOrdering.COLUMN_FIRST);
    	
    	// x size (cols), y size (rows)
    	System.out.println("dimensions are x = " + data[0].length + 
    						" (columns) * y=" + data.length + " (rows)");
    	
    	int size = md.defineDimensions(data[0].length, data.length);
    	
    	System.out.println("strides = " + md.strides);
    	System.out.println("orderin = " + md.storageOrder.ordering);
    	
    	for (int i = 0; i < size; i++) {
    		int[] xy = md.getElementIndexes(i);	
    		System.out.println("order " + i + "-> (" + xy[0] + "," + xy[1] + ")");
    		System.out.println("\t -> " + data[xy[1]][xy[0]]);
    	}
    }
		
}
