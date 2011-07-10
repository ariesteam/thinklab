/**
 * 
 */
package org.integratedmodelling.multidimensional;

import org.integratedmodelling.exceptions.ThinklabRuntimeException;

public class Sum implements IAggregator {

	double acc = 0.0;
	
	@Override
	public void add(Object value) {
		if (value instanceof Double) {
			acc += (Double)value;
		} else if (value instanceof Float) {
			acc += (double)(Float)value;
		} else if (value instanceof Long) {
			acc += (double)(Long)value;
		} else if (value instanceof Integer) {
			acc += (double)(Integer)value;
		} else if (value instanceof Byte) {
			acc += (double)(Byte)value;
		} else {
			throw new ThinklabRuntimeException("cannot accumulate non-numeric values.");
		}
	}

	@Override
	public Object getAggregatedValue() {
		return new Double(acc);
	}

	@Override
	public void reset() {
		acc = 0.0;
	}

	@Override
	public Object getDistributedValue(Object value, float ratio) {
		return ((Number)value).doubleValue()/ratio;
	}
	
}