package org.integratedmodelling.thinklab.modelling.random;

import java.text.NumberFormat;
import java.util.Arrays;

/*
 * nothing but a double array with appropriate hash methods
 */
public class IndexedCategoricalDistribution implements Comparable<IndexedCategoricalDistribution> {

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
		return "[m=" + NumberFormat.getInstance().format(getMean())+"]";
	}

	public void setRanges(double[] distributionBreakpoints) {
		this.ranges = distributionBreakpoints;
	}

    public double[] getData() {
        return this.data;
    }

    public double[] getRanges() {
        return this.ranges;
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

	@Override
	public int compareTo(IndexedCategoricalDistribution o) {
		return ((Double)getMean()).compareTo(o.getMean());
	}

	/*
	 * return the coefficient of variation if we have ranges, or the shannon index if
	 * we don't.
	 */
	public double getUncertainty() {
		
		double ret = 0.0;
		if (ranges != null) {
			double mu = 0.0, mu2 = 0.0;
			for (int i = 0; i < data.length; i++) {
				double midpoint = (this.ranges[i] + (this.ranges[i+1] - this.ranges[i])/2);				
				mu += midpoint * data[i];
				mu2 += midpoint * midpoint * data[i]; 
			}
			ret = Math.sqrt(mu2 - (mu*mu));
			ret = mu == 0.0 ? Double.NaN : (ret/mu);
		} else {
			double sh = 0.0;
			int nst = 0;
			for (int i = 0; i < data.length; i++) {
				sh += 
					data[i] *
					Math.log(data[i]);
				nst++;
			}
			ret = (sh/Math.log((double)nst)) * -1.0;
		}
		return ret;
	}
	
}
