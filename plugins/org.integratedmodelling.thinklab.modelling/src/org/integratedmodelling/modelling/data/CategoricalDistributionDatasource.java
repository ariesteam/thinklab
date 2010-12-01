package org.integratedmodelling.modelling.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.IndexedContextualizedDatasourceInt;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.random.IndexedCategoricalDistribution;
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
	
	class DistributionParameters {
		double mean;
		double std;
		IConcept mostLikelyCategory;
		double shannon;
	}
	
	@Override
	public double[] getDataAsDoubles() throws ThinklabValueConversionException {
		
		boolean contp = false; 
		double[] ret = new double[this.data.length];
		double[] unc = new double[this.data.length];
		IConcept truecase = (IConcept) getMetadata().get(Metadata.TRUECASE);
		
		for (int i = 0; i < this.data.length; i++) {

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
				} else {
					ret[i] = (double)ranks.get(c);
				}
				unc[i] = val.shannon;
			}
		}
		
		getMetadata().put(Metadata.UNCERTAINTY, unc);
		if (contp) {
			getMetadata().put(Metadata.CONTINUOUS, Boolean.TRUE);
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
	
		IConcept c = valueMappings[0];
		double   v = probabilities[0];
		double   sh = 0;
		double mu = 0.0;
		double std = 0.0;
		int nst = 0;
		
		DistributionParameters ret = new DistributionParameters();
		
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
			
			if (averageable)
				mu += (this.distributionBreakpoints[i] + 
					   (this.distributionBreakpoints[i+1] - this.distributionBreakpoints[i])/2)
					*		
				probabilities[i];
		}
		
		ret.shannon = (sh/Math.log((double)nst)) * -1.0;
		ret.mostLikelyCategory = c;
		ret.mean = mu;
		ret.std = std;
		
		return ret;
	}

	public CategoricalDistributionDatasource(
			IConcept type, int size, IConcept[] valueMappings, ArrayList<Pair<GeneralClassifier, 
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
		
		// GARY GARY GARY - please change this back into offset = 0 if you get errors from BayesianTransformer
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

	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.implementations.datasources.IndexedContextualizedDatasourceInt#addValue(java.lang.Object)
	 */
	@Override
	public void addValue(int idx, Object o) {
		/*
		 * reorder values according to sorted order before inserting the distribution
		 */
		double[] ps = (double[])o;
		for (int i = 0; i < ps.length; i++) {
			shuttle[this.sortedIndexes[i]] = ps[i];
		}
		super.addValue(idx, new IndexedCategoricalDistribution(shuttle));
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
		return ((IndexedCategoricalDistribution)getValue(n, null)).data;
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
		int i = 0;
		for (; i < valueMappings.length; i++) {
			if (state.equals(valueMappings[i]))
					break;
		}
		return 
			i < valueMappings.length ? 
				((IndexedCategoricalDistribution)getValue(n, null)).data[i] :
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
	
}
