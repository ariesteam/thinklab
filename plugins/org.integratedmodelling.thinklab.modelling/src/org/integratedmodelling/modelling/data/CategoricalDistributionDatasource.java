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
package org.integratedmodelling.modelling.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.IndexedContextualizedDatasourceInt;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.corescience.literals.IndexedCategoricalDistribution;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Pair;

/**
 * A datasource to hold contextualized distribution data. On the grounds that repeated values are
 * likely and big, we use indexing and hashing to memoize the distributions, and only store an index
 * in the data map.
 * 
 * @author Ferdinando
 *
 */
public class CategoricalDistributionDatasource extends
		IndexedContextualizedDatasourceInt<IndexedCategoricalDistribution> {

	IConcept[] valueMappings = null;
	int[] sortedIndexes = null;
	double[] shuttle = null;
	HashMap<IConcept, Integer> ranks = null;
	private double[] distributionBreakpoints;
	private IConcept[] rnk;
	boolean averageable = false;
	
	public static class DistributionParameters {
		public double mean;
		public double var;
		public double std;
		public double cv;
		public IConcept mostLikelyCategory;
		public double shannon;
		public double[] probabilities;
		public double[] min_values;
		public double[] max_values;
		public boolean isBinary = false;
		public IConcept[] categories;
	}
	

	/**
	 * Return a full descriptor of the distribution represented in this state value.
	 * 
	 * @param dist
	 * @return
	 */
	public DistributionParameters getDistribution(IndexedCategoricalDistribution dist) {
		
		DistributionParameters val = 
			getDistributionParameters(dist.data);
		
		val.probabilities = dist.data;

		IConcept[] cc = rnk;
		if (rnk == null && sortedIndexes != null) {
			cc = new IConcept[sortedIndexes.length];
			for (int i = 0; i < sortedIndexes.length; i++)
				cc[i] = valueMappings[sortedIndexes[i]];
		}
		val.categories = cc;
		val.isBinary = (IConcept) getMetadata().get(Metadata.TRUECASE) != null;
		
		return val;
	}
	
	@Override
	public double getDoubleValue(int i) throws ThinklabValueConversionException {
	
		Object dat = getValue(i);
		if (dat != null && !context.isCovered(i))
			dat = null;
		return (dat == null ? Double.NaN : ((IndexedCategoricalDistribution)dat).getMean());	
	}

	
	@Override
	public double[] getDataAsDoubles() throws ThinklabValueConversionException {
		
		boolean contp = false; 
		double[] ret = new double[this.data.length];
		double[] unc = new double[this.data.length];
		IConcept truecase = (IConcept) getMetadata().get(Metadata.TRUECASE);
		
		for (int i = 0; i < this.data.length; i++) {

			if (!context.isCovered(i)) {
				ret[i] = Double.NaN;
				unc[i] = Double.NaN;
				continue;
			}
			
			DistributionParameters val = 
				getDistributionParameters(getProbabilities(i));
			
			IConcept c = val.mostLikelyCategory;
			if (c == null) {
				ret[i] = Double.NaN;
				unc[i] = Double.NaN;
			} else if (ranks == null) {
				ret[i] = (double)data[i];
				unc[i] = val.shannon;
			} else if (truecase != null) {
				/*
				 * default value for boolean is p(true)
				 */
				ret[i] = getProbability(i, truecase);
				unc[i] = val.shannon;
				contp = true;
				
			} else {
								
				/*
				 * TODO allow passing a property to return the mean of the
				 * distribution (if there is one) instead of the most likely
				 * category rank. This should probably be the default if the 
				 * data encode a continuous distribution. Set contp=true in
				 * that case.
				 */
				if (averageable) {
					
					ret[i] = val.mean;
					unc[i] = val.cv;
					contp = true;
				} else {
					ret[i] = (double)ranks.get(c);
					unc[i] = val.shannon;
				}
			}
		}
		
		getMetadata().put(Metadata.UNCERTAINTY, unc);
		if (contp) {
			getMetadata().put(Metadata.CONTINUOUS, Boolean.TRUE);
			if (truecase != null)
				getMetadata().put(Metadata.THEORETICAL_DATA_RANGE, new double[]{0.0, 1.0});
		}
		return ret;
	}

	/**
	 * TODO should return the mean, not the most likely class, except if requested.
	 * @param probabilities
	 * @return
	 */
	private DistributionParameters getDistributionParameters(
			double[] probabilities) {
	
		DistributionParameters ret = new DistributionParameters();
		if (probabilities == null)
			return ret;

		IConcept c = valueMappings[0];
		double   v = probabilities[0];
		double   sh = 0;
		double mu = 0.0, mu2 = 0.0;
		int nst = 0;		

		if (averageable) {
			ret.max_values = new double[probabilities.length];
			ret.min_values = new double[probabilities.length];
		}
		
		/*
		 * compute Shannon's entropy along with the rest
		 * FIXME decide what to use for uncertainty
		 */
		for (int i = 0; i < probabilities.length; i++) {
			if (probabilities[i] > v) {
				v = probabilities[i];
				c = valueMappings[i];
			}
			if (probabilities[i] > 0.0) {
				sh += 
					probabilities[i] *
					Math.log(probabilities[i]);
				nst++;
			}
			
			if (averageable) {
				double midpoint = 
						(this.distributionBreakpoints[i] + 
								(this.distributionBreakpoints[i+1] - this.distributionBreakpoints[i])/2);
				
				mu += midpoint * probabilities[i];
				mu2 += midpoint * midpoint * probabilities[i];
				
				ret.min_values[i] = this.distributionBreakpoints[i];
				ret.max_values[i] = this.distributionBreakpoints[i+1];
			}
		}
		
		ret.shannon = (sh/Math.log((double)nst)) * -1.0;
		ret.mostLikelyCategory = c;
		ret.mean = mu;
		ret.var = mu2 - (mu*mu);
		ret.std = Math.sqrt(ret.var);
		ret.cv  = mu == 0.0 ? 0.0 : (ret.std/mu);
		return ret;
	}

	public CategoricalDistributionDatasource(
			IConcept type, int size, IConcept[] valueMappings, List<Pair<GeneralClassifier, 
			IConcept>> classifiers, ObservationContext context) throws ThinklabValidationException {
		
		super(type, size, context);
		this.sortedIndexes = new int[valueMappings.length];
		this.valueMappings = new IConcept[valueMappings.length];
		this.shuttle = new double[valueMappings.length];
		
		/*
		 * remap the values to ranks and determine how to rewire the input
		 * if necessary, use classifiers instead of lexicographic order to infer the 
		 * appropriate concept order
		 */
		ArrayList<GeneralClassifier> cls = new ArrayList<GeneralClassifier>();
		ArrayList<IConcept> con = new ArrayList<IConcept>();
		for (Pair<GeneralClassifier, IConcept> op: classifiers) {
			cls.add(op.getFirst());
			con.add(op.getSecond());
		}

		Pair<double[], IConcept[]> dst = 
			Metadata.computeDistributionBreakpoints(type, cls, con);		
		if (dst != null) {
			if (dst.getSecond()[0] != null) {
				this.rnk = dst.getSecond();
				this.distributionBreakpoints =  dst.getFirst();
			}
		}
		
		this.averageable = 
			this.distributionBreakpoints != null &&
			!Double.isInfinite(distributionBreakpoints[0]) &&
			!Double.isInfinite(distributionBreakpoints[distributionBreakpoints.length - 1]);
			
		if (rnk == null) {	
			this.ranks = Metadata.rankConcepts(_type, metadata);
		} else {
			this.ranks = Metadata.rankConcepts(_type, rnk, metadata);
		}
		
		// TODO check - change this back into offset = 0 if errors from BayesianTransformer
		int offset = 1; 
		if (getMetadata().get(Metadata.HASZERO) != null)
			offset = ((Boolean)getMetadata().get(Metadata.HASZERO)) ? 0 : 1;
		
		if (ranks == null)
			throw new ThinklabRuntimeException(
					"internal: probabilistic datasource: cannot determine classification from type " + 
					_type);

		if (ranks != null && ranks.size() != valueMappings.length) {
			throw new ThinklabValidationException(
					"probabilistic discretization of type " + type + " differs from its logical definition");
		}
			
		for (int i = 0; i < valueMappings.length; i++) {
			int n = ranks.get(valueMappings[i]) - offset;
			this.sortedIndexes[i] = n;
			this.valueMappings[n] = valueMappings[i];
		}
	}

	/**
	 * Return a distribution with the evidence set to the passed concept (1.0 probability for
	 * it, 0 for all others).
	 * 
	 * @param c
	 * @return
	 */
	public IndexedCategoricalDistribution setEvidence(IConcept c) {
		
		int i = 0;
		for (IConcept cc : valueMappings) {
			if (c.equals(cc))
				shuttle[i] = 1.0;
			else shuttle[i] = 0.0;
			i++;
		}
		return new IndexedCategoricalDistribution(shuttle);
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.implementations.datasources.IndexedContextualizedDatasourceInt#addValue(java.lang.Object)
	 */
	@Override
	public void setValue(int idx, Object o) {
		
		if (o == null)
			super.setValue(idx, null);
		else if (o instanceof IndexedCategoricalDistribution) {
			
			super.setValue(idx, (IndexedCategoricalDistribution)o);
			
		} else if (o instanceof IConcept) {
		
			super.setValue(idx, setEvidence((IConcept)o));
			
		} else {
			/*
			 * reorder values according to sorted order before inserting the distribution
			 */
			double[] ps = (double[])o;
			for (int i = 0; i < ps.length; i++) {
				shuttle[this.sortedIndexes[i]] = ps[i];
			}
			super.setValue(idx, new IndexedCategoricalDistribution(shuttle));
		}
	}

	
	/**
	 * If the distribution encoded in the states is the discretization of a continuous distribution,
	 * return the breakpoints of each numeric class. If either end is open, start and/or end the
	 * returned array with the appropriate Infinity. If this does not encode a continuous distribution,
	 * return null.
	 * 
	 * @return 
	 * @throws ThinklabInappropriateOperationException if the distribution is not continuous or
	 * 	       there are gaps in the classes.
	 */
	public double[] getDistributionBreakpoints() {
		return distributionBreakpoints;
	}

	/**
	 * Return the probabilities of all the states in value mappings
	 * @param n
	 * @return
	 */
	public double[] getProbabilities(int n) {
		
		if (getValue(n) == null)
			return null;
		
		return ((IndexedCategoricalDistribution)getValue(n)).data;
	}

	/**
	 * Return the possible states in the order they appear in the marginals
	 * @return
	 */
	public IConcept[] getStates() {
		return valueMappings;
	}
	
	/**
	 * Return the probability of a specific state, or zero if the state is not known to occur.
	 * Does not raise exceptions on unadmissible states.
	 * 
	 * @param n
	 * @param state
	 * @return
	 */
	public double getProbability(int n, IConcept state) {
		
		if (getValue(n) == null)
			return 0.0;
		
		int i = 0;
		for (; i < valueMappings.length; i++) {
			if (state.equals(valueMappings[i]))
					break;
		}
		return 
			i < valueMappings.length ? 
				((IndexedCategoricalDistribution)getValue(n)).data[i] :
				0.0;
	}
	
	public String toString() {
		return  
			"CDD[" +
			Arrays.toString(valueMappings) +
			" {" + 
			inverseMap /* + 
			" } -> " +
			Arrays.toString(data) + */ +
			"]";
	}

	public void addAllMetadata(HashMap<String, Object> hashMap) {
		if (hashMap != null)
			getMetadata().putAll(hashMap);
	}

	@Override
	public Object getValue(int offset) {
		Object ret = super.getValue(offset);
		if (ret != null)
			((IndexedCategoricalDistribution)ret).setRanges(distributionBreakpoints);
		return ret;
	}
	
	
	@Override
	public boolean isProbabilistic() {
		return true;
	}

	@Override
	public boolean isContinuous() {
		return Metadata.isContinuous(metadata);
	}

	@Override
	public boolean isNumeric() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCategorical() {
		return Metadata.isUnorderedClassification(metadata);
	}

	@Override
	public boolean isBoolean() {
		return Metadata.isBoolean(metadata);
	}
	
}
