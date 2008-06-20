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
package org.integratedmodelling.utils.multidimensional;

import java.util.ArrayList;

import org.integratedmodelling.utils.Triple;

public class MultidimensionalCursor {

	
	public enum StorageOrdering {
		C,
		FORTRAN;
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
				if (order == StorageOrdering.C) {
					ordering.add(dimensions - 1 - i);
				} else if (order == StorageOrdering.FORTRAN) {
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

    public MultidimensionalCursor(StorageOrdering order) {
    	multiplicity = 0;
    	dimensions = 0;
    	storageOrderType = order;
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
    	storageOrder.set(dimensions, storageOrderType);
    	for (int n = 0; n != dimensions; ++n) {
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
    	int n = dimensions - 1;
    	for (int i = dimensions - 1; i > 0; i--) {
    		
    		ret[n--] = offset/strides.get(i);
    		rest -= ret[n+1] * strides.get(i);
    	}
    	
    	ret[0] = rest;
    	
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
    	for (int n = 0; n != dimensions; ++n)
    		offset += indices[n]* strides.get(n);
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

    /**
     * 
     * @return
     */
    public int getMultiplicity() {
    	return multiplicity;
    }
    
		
}
