package org.integratedmodelling.corescience.storage;

import java.util.ArrayList;
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
	ArrayList<T> switchers = new ArrayList<T>();
	BitSet activation = null;
	int size = 0;
	ObservationContext context = null;
	
	/**
	 * Build a switch layer for a specific context
	 * @param ctx
	 */
	public SwitchLayer(ObservationContext ctx) {
		this(ctx.getMultiplicity(), null);
		this.context = ctx;
	}
	
	public ObservationContext getContext() {
		return context;
	}
	
	public int size() {
		return size;
	}
	
	public SwitchLayer(int size, T[] switchers) {
		this.size = size;
		data = new byte[size];
		
		if (switchers != null)
			for (T sw : switchers)
				this.switchers.add(sw);
		
		activation = new BitSet(size);
	}
	
	public T get(int i) {
		return data[i] == 0 ? null : switchers.get(data[i]-1);
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
