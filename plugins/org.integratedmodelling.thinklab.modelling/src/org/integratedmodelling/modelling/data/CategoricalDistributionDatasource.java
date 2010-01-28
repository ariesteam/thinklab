package org.integratedmodelling.modelling.data;

import java.util.Arrays;
import java.util.HashMap;

import org.integratedmodelling.corescience.implementations.datasources.IndexedContextualizedDatasourceInt;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.random.IndexedCategoricalDistribution;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
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
	
	@Override
	public double[] getDataAsDoubles() throws ThinklabValueConversionException {

		/*
		 * FIXME this should be harmless but not necessary, as the ranking is now 
		 * done in the constructor.
		 */
		HashMap<IConcept, Integer> ranks = Metadata.rankConcepts(_type, this);		
		double[] ret = new double[this.data.length];
		double[] unc = new double[this.data.length];
		IConcept truecase = (IConcept) getMetadata(Metadata.TRUECASE);
		
		for (int i = 0; i < this.data.length; i++) {

			Pair<IConcept, Double> val = 
				getDistributionParameters(getProbabilities(i));
			
			IConcept c = val.getFirst();
			if (c == null) {
				ret[i] = Double.NaN;
				unc[i] = 1.0;
			} else if (ranks == null) {
				ret[i] = (double)data[i];
				unc[i] = val.getSecond();
			} else if (truecase != null) {
				
				/*
				 * default value for boolean is p(true)
				 */
				ret[i] = getProbability(i, truecase);
				unc[i] = val.getSecond();
				
			} else {
				ret[i] = (double)ranks.get(c);
				unc[i] = val.getSecond();
			}
		}
		
		setMetadata(Metadata.UNCERTAINTY, unc);
		
		return ret;
	}


	private Pair<IConcept, Double> getDistributionParameters(
			double[] probabilities) {
	
		IConcept c = valueMappings[0];
		double   v = probabilities[0];
		double   sh = 0;
		int nst = 0;
		
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
		}
		
		sh = (sh/Math.log((double)nst)) * -1.0;
		
		return new Pair<IConcept, Double>(c,sh);
	}

	public CategoricalDistributionDatasource(IConcept type, int size, IConcept[] valueMappings) throws ThinklabValidationException {
		
		super(type, size);
		this.sortedIndexes = new int[valueMappings.length];
		this.valueMappings = new IConcept[valueMappings.length];
		this.shuttle = new double[valueMappings.length];
		
		/*
		 * remap the values to ranks and determine how to rewire the input
		 */
		HashMap<IConcept, Integer> ranks = Metadata.rankConcepts(_type, this);
		int offset = 0; 
		if (getMetadata(Metadata.HASZERO) != null)
			offset = ((Boolean)getMetadata(Metadata.HASZERO)) ? 0 : 1;
		
		if (ranks != null && ranks.size() != valueMappings.length) {
			throw new ThinklabValidationException(
					"probabilistic discretization of type " + type + " differs from its logical definition");
		}
		
		if (ranks != null)
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
	public void addValue(Object o) {
		/*
		 * reorder values according to sorted order before inserting the distribution
		 */
		double[] ps = (double[])o;
		for (int i = 0; i < ps.length; i++) {
			shuttle[this.sortedIndexes[i]] = ps[i];
		}
		super.addValue(new IndexedCategoricalDistribution(shuttle));
	}
	
	
	/**
	 * If the distribution encoded in the states is the discretization of a continuous distribution,
	 * return the breakpoints of each numeric class. If either end is open, start and/or end the
	 * returned array with a NaN.
	 * 
	 * @return
	 * @throws ThinklabInappropriateOperationException if the distribution is not continuous or
	 * 	       there are gaps in the classes.
	 */
	public double[] getDistributionBreakpoints() throws ThinklabInappropriateOperationException {
	
		double[] ret = null;
		
		return ret;
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
	
}
