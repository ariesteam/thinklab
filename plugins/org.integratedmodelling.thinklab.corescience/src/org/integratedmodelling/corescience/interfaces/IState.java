/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.corescience.interfaces;

import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
/**
 * A State is the result of contextualizing a stateful observation. It can conceptualize back to the
 * semantic annotation for the observation it represents. States live in IContexts.
 * 
 * @author Ferdinando
 *
 */
public interface IState  extends /* IDataSource, */ IConceptualizable {
		
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
	
	public abstract boolean isProbabilistic();
	
	public abstract boolean isContinuous();
	
	public abstract boolean isNumeric();
	
	public abstract boolean isCategorical();
	
	public abstract boolean isBoolean();
}
