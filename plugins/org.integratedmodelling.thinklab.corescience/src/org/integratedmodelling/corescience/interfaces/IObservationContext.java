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

import java.io.PrintStream;
import java.util.Collection;

import org.integratedmodelling.corescience.listeners.IContextualizationListener;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;

public interface IObservationContext extends IContext, IConceptualizable {

	/**
	 * Get the observation we represent
	 * @return
	 */
	public IObservation getObservation();

	/**
	 * Get the state of the given concept.
	 * 
	 * @param concept
	 * @return
	 */
	public abstract IState getState(IConcept concept);

	/**
	 * Return all the states that do not describe topology extents.
	 * @return
	 */
	public abstract Collection<IState> getStates();

	
	/**
	 * Return all the states that describe topology extents.
	 * @return
	 */
	public abstract Collection<IExtent> getExtents();

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
	 * Return the number of dimensions along the extent
	 * @return
	 */
	public int getNumberOfDimensions();
	
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
	 * Return an array with the size of each extent in order of contextualization.
	 * @return
	 */
	public int[] getDimensionSizes();
	
	/**
	 * Produce the contextualized observation corresponding to the state of
	 * the merged contexts we represent.
	 * @param session
	 * @param listeners
	 * @return
	 * @throws ThinklabException
	 */
	public void run(
			ISession session, 
			Collection<IContextualizationListener> listeners) throws ThinklabException;
	
	/**
	 * Return true if the context is the empty set, i.e. the intersection of all topologies is
	 * empty. Should be checked before doing anything with the context.
	 * 
	 * @return
	 */
	public boolean isEmpty();
	
	/**
	 * Produce a listing of the contextualization strategy
	 * @param out
	 */
	public void dump(PrintStream out);

	/**
	 * 
	 * @return
	 */
	public Collection<IConcept> getStateObservables();

}
