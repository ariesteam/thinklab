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
package org.integratedmodelling.corescience.interfaces.context;

import java.util.Collection;

import org.integratedmodelling.corescience.interfaces.cmodel.IExtent;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.LogicalConnector;
import org.integratedmodelling.utils.Polylist;

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
public interface IObservationContext extends Iterable<IObservationContext> {


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
	public Collection<IConcept> getDimensions();
	
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
	 * Return the description of an observation that can be used to create this extent.
	 * 
	 * @param c
	 * @return
	 * @throws ThinklabException 
	 */
	public Polylist conceptualizeExtent(IConcept c) throws ThinklabException;
	
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

	
	/**
	 * Return a new observation context that has all extents that we have substituted with
	 * the correspondent ones in the passed context.
	 * 
	 * @param context
	 * @return
	 */
	public IObservationContext remapExtents(IObservationContext context);
	
}
