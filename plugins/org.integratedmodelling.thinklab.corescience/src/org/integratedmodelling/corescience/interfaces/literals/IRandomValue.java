package org.integratedmodelling.corescience.interfaces.literals;

import umontreal.iro.lecuyer.probdist.Distribution;

public interface IRandomValue {

	public abstract Distribution getDistribution();
	
	public double draw();
	
	public double getMean();
	
	public double getStandardDeviation();
	
	public double getVariance();
	
}
