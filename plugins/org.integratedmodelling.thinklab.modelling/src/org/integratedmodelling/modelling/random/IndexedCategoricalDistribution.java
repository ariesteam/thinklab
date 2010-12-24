package org.integratedmodelling.modelling.random;

import java.util.Arrays;

import org.integratedmodelling.utils.Pair;

/*
 * nothing but a double array with appropriate hash methods
 */
public class IndexedCategoricalDistribution {

	public double[] data = null;
	
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
	
	/**
	 * Pass the values of the distributed classified data (a double array of values at each
	 * breakpoint, size n+1, or the actual values, size n) and return the mean and standard 
	 * deviation. Takes values at midpoint if breakpoints are passed.
	 */
	public Pair<Double, Double> stats(double[] values) {
		
		double m = 0.0, std = 0.0;
		
		return new Pair<Double, Double>(m,std);
	}
	
}
