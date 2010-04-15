package org.integratedmodelling.multidimensional;

public interface IAggregator {

	public abstract void reset();

	public abstract void add(Object value);

	public abstract Object getAggregatedValue();

	public abstract Object getDistributedValue(Object value, float ratio);
	
}