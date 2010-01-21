package org.integratedmodelling.corescience.interfaces;

import java.io.File;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;

@SuppressWarnings("unchecked")
public interface IState extends IDataSource, IConceptualizable {
		
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

	/**
	 * States are visualized and stored so they carry metadata to aid the
	 * process. Metadata should be set by the createContextualizedStorage
	 * method in the corresponding conceptual model. A catalog of metadata
	 * ID is in org.integratedmodelling.corescience.metadata.Metadata and its
	 * plugin-specific derivatives.
	 * 
	 * @param id
	 * @param o
	 */
	public void setMetadata(String id, Object o);

	/**
	 * Just get the metadata. A property object interface should suffice.
	 * @param id
	 * @return
	 */
	public Object getMetadata(String id);

	/**
	 * Return the total number of states.
	 * @return
	 */
	public int getTotalSize();

	
}
