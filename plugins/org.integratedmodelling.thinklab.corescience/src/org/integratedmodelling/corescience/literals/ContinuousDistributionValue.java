package org.integratedmodelling.corescience.literals;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.literals.IRandomValue;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.integratedmodelling.thinklab.literals.NumberValue;
import org.integratedmodelling.thinklab.literals.ParsedLiteralValue;

import umontreal.iro.lecuyer.probdist.ContinuousDistribution;
import umontreal.iro.lecuyer.probdist.Distribution;
import umontreal.iro.lecuyer.probdist.DistributionFactory;

@InstanceImplementation(concept=CoreScience.CONTINUOUS_DISTRIBUTION)
public class ContinuousDistributionValue extends ParsedLiteralValue implements IRandomValue {

	public enum Distributions {
		ANDERSON_DARLING,
		BETA,
		BETA_SYMMETRIC,
		CAUCHY,
		CHI,
		CHI_SQUARE,
		CRAMER_VONMISES,
		ERLANG,
		EXPONENTIAL,
		EXTREME_VALUE,
		FATIGUE_LIFE,
		FISHER_F,
		FOLDED_NORMAL,
		GAMMA,
		HALF_NORMAL,
		HYPERBOLIC_SECANT,
		INVERSE_GAUSSIAN,
		KOLMOGOROV_SMIRNOV,
		LAPLACE,
		LOGISTIC,
		LOG_LOGISTIC,
		LOG_NORMAL,
		NAKAGAMI,
		NORMAL,
		NORMAL_INVERSE,
		PARETO,
		PASCAL,
		PEARSON5,
		PEARSON6,
		PIECEWISE_LINEAR_EMPIRICAL,
		POWER,
		RAYLEIGHT,
		STUDENT,
		TRIANGULAR,
		TRUNCATED,
		UNIFORM,
		WATSON_G,
		WATSON_U,
		WEIBULL
	}


	private ContinuousDistribution distribution = null;
	
	public ContinuousDistributionValue(Distributions distribution, double ... parameters) {
		
	}
	
	public ContinuousDistributionValue(String literal) throws ThinklabValidationException {
		parseLiteral(literal);
	}
	
	@Override
	public BooleanValue asBoolean() throws ThinklabValueConversionException {
		// TODO for some distributions, this makes sense
		throw new ThinklabValueConversionException("wrong conversion attempted: distribution -> boolean");
	}

	@Override
	public NumberValue asNumber() throws ThinklabValueConversionException {
		return new NumberValue(distribution.getMean());
	}

	@Override
	public void parseLiteral(String s) throws ThinklabValidationException {
		this.distribution = DistributionFactory.getContinuousDistribution(s);
	}

	@Override
	public double draw() {
		// TODO Auto-generated method stub
		return 0.0;
	}

	@Override
	public Distribution getDistribution() {
		return distribution;
	}

	@Override
	public double getMean() {
		return distribution.getMean();
	}

	@Override
	public double getStandardDeviation() {
		return distribution.getStandardDeviation();
	}

	@Override
	public double getVariance() {
		return distribution.getVariance();
	}

}
