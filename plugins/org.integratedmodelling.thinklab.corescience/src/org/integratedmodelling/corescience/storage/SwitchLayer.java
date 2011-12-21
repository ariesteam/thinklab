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
package org.integratedmodelling.corescience.storage;

import java.util.BitSet;

import org.integratedmodelling.corescience.context.ObservationContext;

/**
 * An array of indexes that can be used to map different regions of an observation context to 
 * different observations.
 * 
 * @author Ferdinando
 *
 */
public class SwitchLayer<T> {

	byte[] data = null;
	BitSet activation = null;
	int size = 0;
	ObservationContext context = null;
	
	/**
	 * Build a switch layer for a specific context
	 * @param ctx
	 */
	public SwitchLayer(ObservationContext ctx) {
		this(ctx.getMultiplicity());
		this.context = ctx;
	}
	
	public ObservationContext getContext() {
		return context;
	}
	
	public int size() {
		return size;
	}
	
	public void set(int index, byte b) {
		data[index] = b;
		activation.set(index);
	}
	
	public SwitchLayer(int size) {
		this.size = size;
		data = new byte[size];
		activation = new BitSet(size);
	}
	
	public T get(int i, T[] switchers) {
		return data[i] == 0 ? null : switchers[data[i]-1];
	}
		
	public boolean isCovered() {
		return activation.cardinality() == size;
	}

	/**
	 * Set the i-th region to the passed ID.
	 * 
	 * @param i
	 * @param ctidx
	 */
	public void set(int i, int ctidx) {
		activation.set(i);
		data[i] = (byte)ctidx;
	}
	
}