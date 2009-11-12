package org.integratedmodelling.corescience.interfaces.data;

import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;

@SuppressWarnings("unchecked")
public interface IContextualizedState extends IDataSource, IConceptualizable {
		
	public void addValue(Object o);

	/**
	 * This will return an array of the appropriate type without any further allocation.
	 * It's terrifying to use in Java, but just fine for dynamically typed embedded
	 * languages. 
	 * 
	 * @return
	 */
	public Object getRawData();

	/**
	 * Should endeavor to return doubles as long as it's not entirely meaningless. Many 
	 * procedures will require doubles and the more are supported, the more can be done
	 * with all scientific plugins.
	 * 
	 * @return
	 */
	public double[] getDataAsDoubles() throws ThinklabValueConversionException;
}
