package org.integratedmodelling.corescience.literals;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.literals.IRandomValue;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.integratedmodelling.thinklab.literals.NumberValue;
import org.integratedmodelling.thinklab.literals.ParsedLiteralValue;

import umontreal.iro.lecuyer.probdist.DiscreteDistribution;
import umontreal.iro.lecuyer.probdist.Distribution;
import umontreal.iro.lecuyer.probdist.DistributionFactory;

/**
 * A value that represents a discrete distribution.
 * 
 * @author Ferdinando Villa
 *
 */
@InstanceImplementation(concept=CoreScience.DISCRETE_DISTRIBUTION)
public class DiscreteDistributionValue extends ParsedLiteralValue implements IRandomValue {

	public enum Distributions {
		BINOMIAL,
		EMPIRICAL,
		GEOMETRIC,
		HYPERGEOMETRIC,
		LOGARITHMIC,
		NEGATIVE_BINOMIAL,
		POISSON
	}

	private DiscreteDistribution distribution = null;
	
	public DiscreteDistributionValue(Distributions distribution, double ... parameters) {
		
	}
	
	public DiscreteDistributionValue(String literal) throws ThinklabValidationException {
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
		this.distribution  = DistributionFactory.getDiscreteDistribution(s);
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
