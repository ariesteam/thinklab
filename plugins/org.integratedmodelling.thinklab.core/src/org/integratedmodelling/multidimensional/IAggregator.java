package org.integratedmodelling.multidimensional;

public interface IAggregator {

	public abstract void reset();

	public abstract void add(Object value);

	public abstract Object getAggregatedValue();

}