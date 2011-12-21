/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.corescience.literals;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.literals.IRandomValue;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.integratedmodelling.thinklab.literals.NumberValue;
import org.integratedmodelling.thinklab.literals.ParsedLiteralValue;

import umontreal.iro.lecuyer.probdist.Distribution;
import umontreal.iro.lecuyer.probdist.DistributionFactory;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.rng.LFSR113;
import umontreal.iro.lecuyer.rng.RandomStream;

@InstanceImplementation(concept=CoreScience.RANDOM_VALUE)
public class DistributionValue extends ParsedLiteralValue implements IRandomValue {

	/*
	 * TODO not sure one per object is overkill or not. If needed, this can be made
	 * static and draw() synchronized.
	 */
	RandomStream stream = new LFSR113();
	
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
		WEIBULL,
		BINOMIAL,
		EMPIRICAL,
		GEOMETRIC,
		HYPERGEOMETRIC,
		LOGARITHMIC,
		NEGATIVE_BINOMIAL,
		POISSON
	}


	private Distribution distribution = null;
	private RandomVariateGen genN;
	
	public DistributionValue(Distributions distribution, double ... parameters) {
	}
	
	public DistributionValue(Distribution distribution) {
		this.distribution = distribution;
		this.genN = new RandomVariateGen(stream, distribution);
	}
	
	public DistributionValue(String literal) throws ThinklabValidationException {
		parseLiteral(literal);
		this.genN = new RandomVariateGen(stream, distribution);

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
		this.distribution = DistributionFactory.getDistribution(s);
	}

	@Override
	public double draw() {
		return genN.nextDouble();
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
