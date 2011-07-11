package org.integratedmodelling.corescience.interfaces;

import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
/**
 * A State is the result of contextualizing a stateful observation. It can conceptualize back to the
 * semantic annotation for the observation it represents. States live in IContexts.
 * 
 * @author Ferdinando
 *
 */
public interface IState extends IConceptualizable {
		
	public IConcept getValueType();
	
	/*
	 * Set the value at index. FIXME Should be protected, but for now needs to be public.
	 * @param index
	 * @param o
	 */
	public void setValue(int index, Object o);

	/**
	 * Return the unmodified object at given offset. Most times it will be a duplicate of
	 * super.getValue(offset, parameters) but should not make any modification. If data are 
	 * unknown (nodata), return null.
	 * 
	 * @param previousOffset
	 * @return
	 */
	public Object getValue(int offset);

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
	 * Just get the metadata.
	 *
	 * @return
	 */
	public Metadata getMetadata();

	/**
	 * Return the total number of states.
	 * @return
	 */
	public int getValueCount();

	/**
	 * Return the class of what our contents observe.
	 * @return
	 */
	public IConcept getObservableClass();

	/**
	 * States exist within a context, and must be able to
	 * return the context they are part of.
	 */
	public abstract IContext getObservationContext();

	/**
	 * Return a state with the given context dimension collapsed to one, and
	 * the data appropriately aggregated. Return self if the dimension is not
	 * in the context and throw an exception if data along that dimension cannot
	 * be aggregated. It's expected to handle the metadata appropriately, e.g. 
	 * modify the units if necessary.
	 * 
	 * @param concept
	 * @return
	 * @throws ThinklabException
	 */
	public IState aggregate(IConcept concept) throws ThinklabException;

	/**
	 * True if the state has more than one value over any spatial 
	 * dimension. 
	 * 
	 * @return
	 */
	public abstract boolean isSpatiallyDistributed();
	
	/**
	 * True if the state has more than one value over any temporal
	 * dimension.
	 * 
	 * @return
	 */
	public abstract boolean isTemporallyDistributed();
}
