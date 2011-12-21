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

import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * A IContext is a named template to build an IObservationContext for an observation 
 * structure. The useful class that implements this is in the modelling plugin, which binds it to
 * the "defcontext" clojure form and allows it to contain models that make it start with known
 * states besides the extents.
 * 
 * @author ferdinando.villa
 *
 */
public interface IContext {

	/**
	 * Contexts have an ID, although that only matters for those that are
	 * created using Clojure forms in the modelling plugin.
	 * 
	 * @return
	 */
	public abstract String getId();

	/**
	 * Return all the states that describe topology extents.
	 * @return
	 */
	public abstract Collection<IExtent> getExtents();
	
	public abstract int getMultiplicity();

	public abstract int getMultiplicity(IConcept concept) throws ThinklabException;

	/**
	 * Return the extent for a specific topology observable, or null if not there.
	 * 
	 * @param observable
	 * @return
	 */
	public abstract IExtent getExtent(IConcept observable);
	
	/**
	 * True if all the extent states correspondent to the passed index are 
	 * defined in all dimensions (meaning there is a correspondend topology
	 * granule). If this is false for any extent, states using this context
	 * will have a no-data value at that index.
	 *  
	 * @param index
	 * @return
	 */
	public abstract boolean isCovered(int index);
	
	/**
	 * Return the extent for a specific topology observable, or null if not there.
	 * 
	 * @param observable
	 * @return
	 */
	public abstract IState getState(IConcept observable);
	
	/**
	 * Return a newly initialized IObservationContext to be used to contextualize the passed
	 * observation. This will build the common context and contextualization strategy for the
	 * obs, so it may be a long operation.
	 * 
	 * @param observation
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract IObservationContext getObservationContext(IObservation observation) throws ThinklabException;

	/**
	 * Return true if all the extents in this context are either absent in the passed one, or if present
	 * don't determine a null context when intersected.
	 * 
	 * @param context
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract boolean intersects(IContext context) throws ThinklabException;

	/**
	 * 
	 * @return
	 */
	public IExtent getTime();

	/**
	 * 
	 * @return
	 */
	public IExtent getSpace();

	/**
	 * 
	 * @return
	 */
	public abstract Collection<IState> getStates();

	/**
	 * Return a new context without states but with the same extents, transformations etc. of ours.
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract IContext cloneExtents() throws ThinklabException;


	/**
	 * Return a new context with the given dimension collapsed to its 
	 * total extent (1 state only), thereby eliminating the 
	 * distribution in that dimension. IState has an aggregate() function that
	 * will create the correspondent aggregated state.
	 * 
	 * @param dimension
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IContext collapse(IConcept dimension) throws ThinklabException;


}
