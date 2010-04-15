/**
 * IExtent.java
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

import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * An Extent describes the topology of the observable
 * it's linked to. Conceptual models are capable of producing unions or intersections
 * of extents.
 * 
 * Extents must be conceptualizable and the result of conceptualizing
 * them must be an observation describing the extent.
 * 
 * @author Ferdinando Villa
 *
 */
public abstract interface IExtent extends IConceptualizable, ITopologicallyComparable {

	/**
	 * Return the total number of granules in this extent.
	 * 
	 * @return
	 */
	public int getTotalGranularity();
	
	/**
	 * Return the value of the granule indicated according to the natural order of
	 * the topology.
	 * 
	 * @param granule
	 * @return
	 * @throws ThinklabException 
	 */
	public IValue getState(int granule) throws ThinklabException;
	
	/**
	 * Return the value that is the union of all granules, aggregated in the way that makes
	 * sense for the particular conceptual domain.
	 * @deprecated use getAggregatedExtent().getState(0)
	 * @return
	 */
	public IValue getFullExtentValue();
	
	/**
	 * Return the 1-dimensional extent that corresponds to the full extent of our topology.
	 * @return
	 */
	public IExtent getAggregatedExtent();
	

	/**
	 * Return the n-th member of the ordered topology.
	 * @param granule
	 * @return
	 */
	public IExtent getExtent(int granule);
	
	/**
	 * merge with the passed extent of the same observable into a new extent and return it.
	 * Merging is always an intersection operation - return the largest common extent with 
	 * the finest common grain.
	 */
	public IExtent and(IExtent extent) throws ThinklabException;

	/**
	 * Add the passed extent so that the end result represents both. 
	 * 
	 * @param myExtent
	 * @return
	 */
	public IExtent or(IExtent myExtent);
	
	
	/**
	 * Return a copy of our extent constrained by the passed one. Constraining is also an 
	 * intersection but the grain in the final extent should become the same as the 
	 * constraining extent's.
	 * 
	 * @param extent
	 * @return
	 * @throws ThinklabException 
	 */
	public IExtent constrain(IExtent extent) throws ThinklabException;

	
	/**
	 * Return the transformation, if any, that will be necessary to operate on a 
	 * datasource that conforms to us so that it matches the passed extent.
	 * 
	 * @param mainObservable the observable for the main observation that owns the extent
	 * 		  (what the states mean)
	 * @param extent the extent we must adapt the datasource to
	 * @return a transformation to be passed to the datasource
	 * @throws ThinklabException 
	 */
	public IDatasourceTransformation getDatasourceTransformation(
			IConcept mainObservable, IExtent extent) throws ThinklabException;


	/**
	 * Create a string signature that has no spaces, represents the extent accurately,
	 * and is the same for extents that are equal. Used to cache data across runs and
	 * within runs of the same model.
	 * 
	 * @return
	 */
	public abstract String getSignature();



}