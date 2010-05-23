package org.integratedmodelling.corescience.interfaces;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;

@SuppressWarnings("unchecked")
public interface IState extends IDataSource, IConceptualizable {
		
	public void addValue(int index, Object o);

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
	 * get a single double for the given index.
	 */
	public double getDoubleValue(int index) throws ThinklabValueConversionException;
	
	/**
	 * Just get the metadata. A property object interface should suffice.
	 * @param id
	 * @return
	 */
	public Metadata getMetadata();

	/**
	 * Return the total number of states.
	 * @return
	 */
	public int getTotalSize();

	/**
	 * Return the class of what our contents observe.
	 * @return
	 */
	public IConcept getObservableClass();

	
	/**
	 * Contexts are passed to IObservation.createState(), and the state must be able to
	 * return the context it represents.
	 */
	public abstract ObservationContext getObservationContext();

	
	/**
	 * Return the unmodified object at given offset. Most times it will be a duplicate of
	 * getValue() but should not make any modification. If data are unknown, return null.
	 * @param previousOffset
	 * @return
	 */
	public Object getDataAt(int offset);
}
