package org.integratedmodelling.corescience.literals;

import java.util.Arrays;

/*
 * nothing but a double array with appropriate hash methods
 */
public class IndexedCategoricalDistribution {

	public double[] data = null;
	private double[] ranges;
	
	public IndexedCategoricalDistribution(int n) {
		data = new double[n];
	}

	public IndexedCategoricalDistribution(double[] data) {
		this.data = new double[data.length];
		for (int i = 0; i < data.length; i++)
			this.data[i] = data[i];
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof IndexedCategoricalDistribution)) {
			return false;
		}
		IndexedCategoricalDistribution other = (IndexedCategoricalDistribution) obj;
		if (!Arrays.equals(data, other.data)) {
			return false;
		}
		return true;
	}
	
	public String toString() {
		return Arrays.toString(data);
	}

	public void setRanges(double[] distributionBreakpoints) {
		this.ranges = distributionBreakpoints;
	}
	
	public double getMean() {

		double ret = 0.0;
		if (ranges != null) {
			for (int i = 0; i < data.length; i++) {
				double midpoint = (this.ranges[i] + (this.ranges[i+1] - this.ranges[i])/2);				
				ret += midpoint * data[i];
			}
		}
		return ret;
	}
	
}
