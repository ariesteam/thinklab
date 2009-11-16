package org.integratedmodelling.modelling.data;

import java.util.Arrays;
import java.util.HashMap;

import org.integratedmodelling.corescience.implementations.datasources.IndexedContextualizedDatasourceInt;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.random.IndexedCategoricalDistribution;
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
	
	@Override
	public double[] getDataAsDoubles() throws ThinklabValueConversionException {

		HashMap<IConcept, Integer> ranks = Metadata.rankConcepts(_type);		
		double[] ret = new double[this.data.length];
		double[] unc = new double[this.data.length];
		
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


	public CategoricalDistributionDatasource(IConcept type, int size, IConcept[] valueMappings) {
		super(type, size);
		this.valueMappings = valueMappings;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.implementations.datasources.IndexedContextualizedDatasourceInt#addValue(java.lang.Object)
	 */
	@Override
	public void addValue(Object o) {
		super.addValue(new IndexedCategoricalDistribution((double[])o));
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
