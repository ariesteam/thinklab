package org.integratedmodelling.corescience.interfaces.data;

import java.util.Collection;

import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * Anything that implements this is capable of returning the concept corresponding to the i-th 
 * data point. 
 * 
 * @author Ferdinando
 *
 */
public interface ICategoryData {

	/**
	 * Return the concept that most directly subsumes all the categories in the data source.
	 * 
	 * @return
	 */
	public abstract IConcept getConceptSpace();
	
	/**
	 * Return all the categories represented in the data source.
	 * 
	 * @return
	 */
	public abstract Collection<IConcept> getAllCategories();
	
	/**
	 * Return the category of data point n.
	 * 
	 * @param n
	 * @return
	 */
	public abstract IConcept getCategory(int n);
	
	/**
	 * Return an array of all data. Use only if strictly necessary.
	 * 
	 * @return
	 */
	public abstract IConcept[] getData();
	
}
