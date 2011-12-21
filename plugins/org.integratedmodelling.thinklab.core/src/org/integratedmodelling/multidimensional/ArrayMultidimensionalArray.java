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
package org.integratedmodelling.multidimensional;

import java.util.Iterator;

import org.integratedmodelling.exceptions.ThinklabRuntimeException;

public class ArrayMultidimensionalArray {

	private Object storage = null;
	int length = 0;
	MultidimensionalCursor cursor;
	
	boolean isDouble = false;
	boolean isFloat = false;
	boolean isLong = false;
	boolean isByte = false;
	boolean isInt = false;
	boolean isObject = false;

	public ArrayMultidimensionalArray(Object data, int ... dimensions) {

		cursor = new MultidimensionalCursor(MultidimensionalCursor.StorageOrdering.ROW_FIRST);
		cursor.defineDimensions(dimensions);

		if (data instanceof double[]) {
			storage = data;
			isDouble = true;
			length = ((double[])storage).length;
		} else if (data instanceof float[]) {
			storage = data;
			isFloat = true;
			length = ((float[])storage).length;
		} else if (data instanceof long[]) {
			storage = data;
			isLong = true;
			length = ((long[])storage).length;
		} else if (data instanceof int[]) {
			storage = data;
			isInt = true;
			length = ((int[])storage).length;
		} else if (data instanceof byte[]) {
			storage = data;
			isByte = true;
			length = ((byte[])storage).length;
		} else if (data instanceof Object[]) {
			storage = data;
			isObject = true;
			length = ((Object[])storage).length;
		} else if (data instanceof ArrayMultidimensionalArray) {
			
			if (((ArrayMultidimensionalArray)data).isDouble) {
				storage = new double[cursor.getMultiplicity()];
				isDouble = true;
				length = ((double[])storage).length;
			} else if (((ArrayMultidimensionalArray)data).isFloat) {
				storage = new float[cursor.getMultiplicity()];
				isFloat = true;
				length = ((float[])storage).length;
			} else if (((ArrayMultidimensionalArray)data).isLong) {
				storage = new long[cursor.getMultiplicity()];
				isLong = true;
				length = ((long[])storage).length;
			} else if (((ArrayMultidimensionalArray)data).isInt) {
				storage = new int[cursor.getMultiplicity()];
				isInt = true;
				length = ((int[])storage).length;
			} else if (((ArrayMultidimensionalArray)data).isByte) {
				storage = new byte[cursor.getMultiplicity()];
				isByte = true;
				length = ((byte[])storage).length;
			} else if (((ArrayMultidimensionalArray)data).isObject) {
				storage = new Object[cursor.getMultiplicity()];
				isByte = true;
				length = ((byte[])storage).length;
			}
		} else {
			throw new ThinklabRuntimeException(
				"multiarray initialized with an object of unsupported type.");
		}
	}
	

	public class MultidimensionalIterator implements Iterator<Object> {

		int step;
		int current;
		private int dimension;
		private int index;
		private int[] indices;
		
		public boolean hasNext() {
			return index < cursor.getDimensionSize(dimension);
		}

		public Object next() {

			indices[dimension] = index;
			current = cursor.getElementOffset(indices);
			index ++;
			
			if (isDouble)
				return nextDouble();
			else if (isFloat)
				return nextFloat();
			else if (isLong)
				return nextLong();
			else if (isInt)
				return nextInt();
			else if (isByte)
				return nextByte();
			else if (isObject)
				return nextObject();
			
			// won't happen
			return null;
		}

		public void remove() {
			throw new ThinklabRuntimeException(
				"remove() cannot be called on an array iterator.");
		}
		
		public double nextDouble() {
			return ((double[])storage)[current];
		}

		public long nextLong() {
			return ((long[])storage)[current];
		}

		public int nextInt() {
			return ((int[])storage)[current];
		}

		public float nextFloat() {
			return ((float[])storage)[current];
		}

		public byte nextByte() {
			return ((byte[])storage)[current];
		}
		
		public Object nextObject() {
			return ((Object[])storage)[current];
		}

		public MultidimensionalIterator(int dimension, int[] indices) {
			this.index = 0;
			this.dimension = dimension;
			this.indices = indices;
		}
	}

	/**
	 * Return an iterator over a slice along the indicated dimension, with all other dimension
	 * offsets identified by the specified remaining ones. Note that the offsets passed must
	 * contain the exact number of dimensions, and the offsets paired to the actual dimension. The
	 * offset for the dimension of interest is ignored.
	 * @param dimension
	 * @param offsets
	 * @return
	 */
	public MultidimensionalIterator iterator(int dimension, int ... offsets) {
		return new MultidimensionalIterator(dimension, offsets);
	}

	public Object get(int ... indexes) {
		return getLinear(cursor.getElementOffset(indexes));
	}

	public int size() {
		return length;
	}

	public int size(int dimension) {
		return cursor.getDimensionSize(dimension);
	}

	public void set(int i, Object value) {
		
		if (isDouble)
			((double[])storage)[i]  = (Double)value;
		else if (isFloat)
			((float[])storage)[i]  = (Float)value;
		else if (isLong)
			((long[])storage)[i]  = (Long)value;
		else if (isInt)
			((int[])storage)[i]  = (Integer)value;
		else if (isByte)
			((byte[])storage)[i]  = (Byte)value;
		else if (isObject)
			((Object[])storage)[i]  = value;
	}
	
	public Object getLinear(int i) {
		
		if (isDouble)
			return ((double[])storage)[i];
		else if (isFloat)
			return ((float[])storage)[i];
		else if (isLong)
			return ((long[])storage)[i];
		else if (isInt)
			return ((int[])storage)[i];
		else if (isByte)
			return ((byte[])storage)[i];
		else if (isObject)
			return ((Object[])storage)[i];
		
		// won't happen
		return null;
	}
	
	/**
	 * Create a new array where the indicated dimension has been collapsed to size 1 and
	 * its values have been aggregated using the supplied aggregator.
	 * 
	 * @param dimensionIndex
	 * @param aggregator
	 * @return
	 */
	public ArrayMultidimensionalArray reduce(int dimensionIndex, IAggregator aggregator) {
		
		int[] dims = cursor.getExtents();
		dims[dimensionIndex] = 1;
		
		ArrayMultidimensionalArray ret = new ArrayMultidimensionalArray(this, dims);
		
		for (int i = 0; i < ret.size(); i++) {

			aggregator.reset();
			
			/*
			 * each value substituted by the aggregation of the other's data along the collapsed dimension
			 */
			for (MultidimensionalIterator it = 
				this.iterator(dimensionIndex, ret.cursor.getElementIndexes(i)); it.hasNext(); )
			  aggregator.add(it.next());
			
			ret.set(i, aggregator.getAggregatedValue());
		}
		
		return ret;
	}

    public static void main(String[] args) {
    	
    	double[] data = {
    			0.0,1.0,2.0,3.0,4.0,5.0,6.0,
    			7.0,8.0,9.0,10.0,11.0,12.0,13.0,
    			14.0,15.0,16.0,17.0,18.0,19.0,20.0};

    	ArrayMultidimensionalArray array = new ArrayMultidimensionalArray(data,7,3);
    	
    	for (int i = 0; i < array.size(1); i++) {
    		for (int j = 0; j < array.size(0); j++) {
    			System.out.print((j == 0 ? "\n" : " ")  + array.get(j, i));
    		}
    	}

    	System.out.println("\n\nSummed over 0: ");
    	ArrayMultidimensionalArray agg = array.reduce(0, new Sum());

    	for (int i = 0; i < agg.size(1); i++) {
    		for (int j = 0; j < agg.size(0); j++) {
    			System.out.print((j == 0 ? "\n" : " ")  + agg.get(j, i));
    		}
    	}
    	
    	System.out.println("\n\nAveraged over 1: ");
    	agg = array.reduce(1, new Average());

    	for (int i = 0; i < agg.size(1); i++) {
    		for (int j = 0; j < agg.size(0); j++) {
    			System.out.print((j == 0 ? "\n" : " ")  + agg.get(j, i));
    		}
    	}

    }
}
