package org.integratedmodelling.modelling.random;

import java.util.Arrays;

/*
 * nothing but a double array with appropriate hash methods
 */
public class IndexedCategoricalDistribution {

	public double[] data = null;
	
	public IndexedCategoricalDistribution(int n) {
		data = new double[n];
	}

	public IndexedCategoricalDistribution(double[] data) {
		this.data = data;
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
	

	
}
