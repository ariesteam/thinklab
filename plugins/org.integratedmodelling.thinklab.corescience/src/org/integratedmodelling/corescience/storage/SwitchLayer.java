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
	T[] switchers = null;
	BitSet activation = null;
	int size = 0;
	
	/**
	 * Build a switch layer for a specific context
	 * @param ctx
	 */
	public SwitchLayer(ObservationContext ctx) {
		this(ctx.getMultiplicity(), null);
	}
	
	public SwitchLayer(int size, T[] switchers) {
		this.size = size;
		data = new byte[size];
		this.switchers = switchers;
		activation = new BitSet(size);
	}
	
	public T get(int i) {
		return data[i] == 0 ? null : switchers[data[i]-1];
	}
	
	public boolean isCovered() {
		return activation.cardinality() == size;
	}
	
}
