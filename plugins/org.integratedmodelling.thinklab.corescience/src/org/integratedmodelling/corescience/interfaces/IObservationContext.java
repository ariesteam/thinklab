/**
 * IObservationContext.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCoreSciencePlugin.
 * 
 * ThinklabCoreSciencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCoreSciencePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.corescience.interfaces;

import java.util.Collection;

import org.integratedmodelling.corescience.exceptions.ThinklabContextValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.utils.LogicalConnector;

/**
 * An object that merges all extents from an observation structure and can generate and
 * coordinate the sequence of context states that expresses the overall extent and all 
 * dependent observations.
 * 
 * In contrast, an IObservationContextState represent the set of states for one atomic contextual
 * state. IObservationContext can create an iterable sequence of all the IObservationContextStates
 * that result from compounding and intersecting extents across an observation structure.
 * 
 * @author Ferdinando Villa
 *
 */
public interface IObservationContext {

	/**
	 * Recursively descend through contingencies and dependencies of a top-level observation, 
	 * merging contexts as we go along to build an overall context.
	 * 
	 * @param observation
	 * @param dimension 
	 * @param connector
	 * @param isConstraint
	 * @throws ThinklabException 
	 */
	public void mergeExtent(IObservation observation, IConcept dimension, LogicalConnector connector, boolean isConstraint) 
		throws ThinklabException;

	/**
	 * Return the total number of states in the context.
	 * @return
	 */
	public int getMultiplicity();
	
	/**
	 * Return the total number of states along one specific dimension.
	 * @param dimension
	 * @return
	 * @throws ThinklabException 
	 */
	public int getMultiplicity(IConcept dimension) throws ThinklabException;
	
	/**
	 * Return the least general concept of each separate dimension of the overall context,
	 * in the appropriate order for contextualization.
	 * 
	 * @return
	 */
	public Collection<IConcept> getContextDimensions();
	
	/**
	 * Return the specific concept of the dimension that is the passed concept, 
	 * or null if not there. Throw an exception if more than one dimensions exist for that
	 * concept.
	 * @param concept
	 * @return
	 */
	public IConcept getDimension(IConcept concept) throws ThinklabException;
	
	/**
	 * Get the extent along the specified dimension. Should simply return null if the extent isn't there.
	 * @param c
	 * @return
	 * @throws ThinklabException
	 */
	public IExtent getExtent(IConcept c);
	
	/**
	 * Return an iterator over all context states in proper order of contextualization. Each
	 * state is an instance of an IObservationContext, with one value for each dimension.
	 * 
	 * We pass a workflow because it may have requests about the context state
	 * generation. Any implementations should always allow for null here.
	 * 
	 * @param workflow the workflow that is contextualizing us, or null.
	 * 
	 * @return an iterator producing all the context states for a particular observation.
	 */
	public IContextStateGenerator getContextStates(IContextualizationWorkflow workflow);

	/**
	 * Return the total number of extent dimensions
	 * @return
	 */
	public int size();

	/**
	 * Return an array with the size of each extent in order of contextualization.
	 * @return
	 */
	public int[] getDimensionSizes();
	
}
