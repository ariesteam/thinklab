package org.integratedmodelling.corescience.storage;

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
	 
	public SwitchLayer(int size, T[] switchers) {
		data = new byte[size];
		this.switchers = switchers;
	}
	
	public T get(int i) {
		return data[i] == 0 ? null : switchers[data[i]-1];
	}
	
}
