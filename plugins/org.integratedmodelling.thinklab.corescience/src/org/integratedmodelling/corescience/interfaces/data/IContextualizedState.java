package org.integratedmodelling.corescience.interfaces.data;

import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;


public interface IContextualizedState extends IDataSource, IConceptualizable {

	public void addValue(Object o);

	/**
	 * This will return an array of the appropriate type without any further allocation.
	 * It's terrifying to use in Java, but just fine for dynamically typed embedded
	 * languages. 
	 * 
	 * @return
	 */
	public Object getData();
}
