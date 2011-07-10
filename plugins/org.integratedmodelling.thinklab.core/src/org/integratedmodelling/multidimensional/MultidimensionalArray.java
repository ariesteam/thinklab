/**
 * MultidimensionalArray.java
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
import java.util.Iterator;

import org.integratedmodelling.collections.Triple;

public class MultidimensionalArray<T> {

	private ArrayList<T> storage = new ArrayList<T>();
	MultidimensionalCursor cursor;
	
	/**
	 * Aggregators are supplied to the multidimensional iterator when a dimension must be collapsed
	 * to size 1.
	 * 
	 * @author Ferdinando
	 *
	 * @param <T>
	 */
	public interface Aggregator<T> {
		
		public abstract void reset();
		
		public abstract void add(T value);
		
		public abstract T getAggregatedValue();
	}
	
	public class MultidimensionalIterator implements Iterator<T> {

		int step;
		int current;
		
		ArrayList<T> storage;
		
		public boolean hasNext() {
			return (current + step) < storage.size();
		}

		public T next() {
			current += step;
			return current < storage.size() ? storage.get(current) : null;
		}

		public void remove() {
			storage.remove(current);
		}
		
		protected MultidimensionalIterator(int offset, int step, int current, ArrayList<T> data) {
			this.current = offset + current;
			this.step = step;
			storage = data;
		}
	}

	/**
	 * Return an iterator over the whole array, regardless of internal structuring. Note
	 * that the storage order is Fortran-like.
	 * @return
	 */
	public Iterator<T> iterator() {
		return storage.iterator();
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

		Triple<Integer, Integer, Integer> stride = 
			cursor.getStridedOffsets(dimension, offsets);
		
		return new MultidimensionalIterator(stride.getFirst(), 0, stride.getThird(), storage);
	}
	
	/**
	 * This constructor is fairly expensive for large arrays as it preallocates the whole
	 * structure. The bad news is that there is no other constructor.
	 * @param dimensions
	 */
	public MultidimensionalArray(int ... dimensions) {
	
		cursor = new MultidimensionalCursor(MultidimensionalCursor.StorageOrdering.ROW_FIRST);
		cursor.defineDimensions(dimensions);
		storage.ensureCapacity(cursor.getMultiplicity());
	}
	
	public T get(int ... indexes) {
		return storage.get(cursor.getElementOffset(indexes));
	}

	public int size() {
		return cursor.multiplicity;
	}

	public void set(int i, T value) {
		storage.add(i, value);
	}
	
	/**
	 * Create a new array where the indicated dimension has been collapsed to size 1 and
	 * its values have been aggregated using the supplied aggregator.
	 * 
	 * @param dimensionIndex
	 * @param aggregator
	 * @return
	 */
	public MultidimensionalArray<T> collapse(int dimensionIndex, Aggregator<T> aggregator) {
		
		int[] dims = cursor.getExtents();
		dims[dimensionIndex] = 1;
		
		MultidimensionalArray<T> ret = new MultidimensionalArray<T>(dims);
		
		for (int i = 0; i < ret.size(); i++) {

			aggregator.reset();
			
			/*
			 * each value substituted by the aggregation of the other's data along the collapsed dimension
			 */
			for (Iterator<T> it = this.iterator(dimensionIndex, this.cursor.getElementIndexes(i));
				it.hasNext(); )
				aggregator.add(it.next());
			
			ret.set(i, aggregator.getAggregatedValue());
		}
		
		return ret;
	}

	
}
