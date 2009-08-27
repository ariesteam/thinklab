package org.integratedmodelling.modelling.data;

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

	public CategoricalDistributionDatasource(IConcept type, int size) {
		super(type, size);
	}

}
