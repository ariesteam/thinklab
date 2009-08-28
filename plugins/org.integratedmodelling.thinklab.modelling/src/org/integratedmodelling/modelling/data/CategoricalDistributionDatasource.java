package org.integratedmodelling.modelling.data;

import java.util.Arrays;

import org.integratedmodelling.corescience.implementations.datasources.IndexedContextualizedDatasourceInt;
import org.integratedmodelling.modelling.random.IndexedCategoricalDistribution;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

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
			"[" + 
			Arrays.toString(valueMappings) +
			" {" + 
			inverseMap + 
			" } -> " +
			Arrays.toString(data) + 
			"]";
	}
	
}
