package org.integratedmodelling.corescience.interfaces.data;

import org.integratedmodelling.corescience.interfaces.cmodel.IExtent;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * A dimensional data source is exposed to the context dimensions and must understand their type and
 * number before contextualization can proceed. Essentially, this means that things that are contextualized
 * in space need to know space unless they're not tagged as dimensional (e.g. a random number can be
 * spatialized without knowing space).
 * 
 * @author Ferdinando
 *
 */
public interface DimensionalDataSource {

	/**
	 * Called for each context dimension
	 * 
	 * @param dimension
	 * @param totalExtent
	 * @param multiplicity
	 * @throws ThinklabValidationException
	 */
	public void notifyContextDimension(IConcept dimension, IExtent totalExtent, int multiplicity)
		throws ThinklabValidationException;
	
	/**
	 * Called after all notifications have been called. Return self or a different datasource
	 * if that's appropriate.
	 */
	public IDataSource<?> validateDimensionality() throws ThinklabValidationException;
	
}
