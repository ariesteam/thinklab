package org.integratedmodelling.thinklab.modelling.random;

import umontreal.iro.lecuyer.probdist.Distribution;
import umontreal.iro.lecuyer.probdist.DistributionFactory;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.rng.LFSR113;
import umontreal.iro.lecuyer.rng.RandomStream;

/**
 * Easy to use distribution for random value extraction. Use with one of the provided string 
 * constants. See <a href="http://statistik.wu-wien.ac.at/unuran/doc/unuran.html#KeysDistr">here</a> 
 * for details on the initialization parameters.
 * 
 * Proxies an SSJ object quite literally.
 * 
 * @author Ferd
 *
 */
public class DistributionValue {

	/*
	 * TODO finish distributions
	 */
	RandomStream stream = new LFSR113();
	
//	public final static String  ANDERSON_DARLING = "";
	public final static String	BETA = "beta";
//	public final static String	BETA_SYMMETRIC = "";
	public final static String	CAUCHY = "cauchy";
	public final static String	CHI = "chi";
	public final static String	CHI_SQUARE = "chisquare";
//	public final static String	CRAMER_VONMISES = "";
//	public final static String	ERLANG = "";
	public final static String	EXPONENTIAL = "exponential";
//	public final static String	EXTREME_VALUE = "";
//	public final static String	FATIGUE_LIFE = "";
	public final static String	FISHER_F = "F";
//	public final static String	FOLDED_NORMAL = "";
	public final static String	GAMMA = "gamma";
	public final static String	HALF_NORMAL = "";
//	public final static String	HYPERBOLIC_SECANT = "";
//	public final static String	INVERSE_GAUSSIAN = "";
//	public final static String	KOLMOGOROV_SMIRNOV = "";
	public final static String	LAPLACE = "laplace";
	public final static String	LOGISTIC = "logistic";
//	public final static String	LOG_LOGISTIC = "";
	public final static String	LOG_NORMAL = "lognormal";
//	public final static String	NAKAGAMI = "";
	public final static String	NORMAL = "normal";
//	public final static String	NORMAL_INVERSE = "";
	public final static String	PARETO = "pareto";
//	public final static String	PASCAL = "";
//	public final static String	PEARSON5 = "";
//	public final static String	PEARSON6 = "";
//	public final static String	PIECEWISE_LINEAR_EMPIRICAL = "";
	public final static String	POWER = "powerexponential";
	public final static String	RAYLEIGHT = "rayleigh";
	public final static String	STUDENT = "student";
	public final static String	TRIANGULAR = "triangular";
//	public final static String	TRUNCATED = "";
	public final static String	UNIFORM = "uniform";
//	public final static String	WATSON_G = "";
//	public final static String	WATSON_U = "";
	public final static String	WEIBULL = "weibull";
	public final static String	BINOMIAL = "binomial";
	public final static String	GEOMETRIC = "geometric";
	public final static String	HYPERGEOMETRIC = "hypergeometric";
	public final static String	LOGARITHMIC = "logarithmic";
	public final static String	NEGATIVE_BINOMIAL = "negativebiniomal";
	public final static String	POISSON  = "poisson";

	private Distribution distribution = null;
	private RandomVariateGen genN;
	
	public DistributionValue(String distribution, double ... parameters) {
		
		String dp = "";
		for (double d : parameters) {
			dp += (dp.isEmpty() ? "" : ", ") + d;
		}
		this.distribution  = DistributionFactory.getDistribution(distribution + "(" + dp + ")");
		this.genN = new RandomVariateGen(stream, this.distribution);
	}
	
	public double draw() {
		return genN.nextDouble();
	}

	public Distribution getDistribution() {
		return distribution;
	}

	public double getMean() {
		return distribution.getMean();
	}

	public double getStandardDeviation() {
		return distribution.getStandardDeviation();
	}

	public double getVariance() {
		return distribution.getVariance();
	}
	
}
