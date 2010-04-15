/**
 * 
 */
package org.integratedmodelling.multidimensional;

/**
 * Not intended as merely an averager, but as the handler of an intensive physical
 * property (meaning the aggregation will be an average, but the distribution will 
 * leave the value unchanged).
 * 
 * @author Ferdinando
 *
 */
public class Average extends Sum {
	int n = 0;

	@Override
	public void add(Object value) {
		super.add(value);
		n++;
	}

	@Override
	public Object getAggregatedValue() {
		return (Double)(super.getAggregatedValue())/(double)n;
	}

	@Override
	public void reset() {
		super.reset();
		n = 0;
	}
	
	@Override
	public Object getDistributedValue(Object value, float ratio) {
		return ((Number)value).doubleValue();
	}
}