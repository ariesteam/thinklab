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

import java.util.HashMap;

import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

public interface IObservation extends IConceptualizable {
	
	/**
	 * 
	 * @return
	 * @throws ThinklabException
	 * @model
	 */
	public abstract IDataSource<?> getDataSource();

	/**
	 * Return the observable instance. Can't be null. If this observation is a
	 * mediator and doesn't have an observable, scan the mediation chain until
	 * one is found.
	 * 
	 * @return the observable for this observation
	 */
	public abstract IInstance getObservable();

	/**
	 * Get the class of the main observable. If this observation is a mediator
	 * and doesn't have an observable, scan the mediation chain until one is
	 * found.
	 * 
	 * @return
	 * @model
	 */
	public abstract IConcept getObservableClass();

	/**
	 * Return the observation instance of which this is the Java peer
	 * implementations.
	 * 
	 * @return
	 */
	public abstract IInstance getObservationInstance();

	/**
	 * Return a collection of all observations on which this one depends except
	 * the extents.
	 * 
	 * @return
	 */
	public abstract IObservation[] getDependencies();

	/**
	 * Return a collection of all observations that may contribute to define the 
	 * states of this one, accumulating states over the union of their extents.
	 * 
	 * @return
	 */
	public abstract IObservation[] getContingencies();

	
	/**
	 * Return a collection of all extent observation that this one depends on.
	 * 
	 * @return
	 */
	public abstract Topology[] getTopologies();

	/**
	 * If this observation is acting as a mediator for another, return it. If
	 * it's a mediator, the datasource should be ignored and the observable may
	 * be null.
	 * 
	 * @return
	 */
	public IObservation getMediatedObservation();

	/**
	 * If this observation is being mediated by another, return the mediator.
	 * 
	 * @return
	 */
	public IObservation getMediatorObservation();

	/**
	 * Return true if this observation is part of a chain of mediation and is
	 * not the last one in the chain. Mediated observations can share
	 * observables with the ones that mediate them, but are second-class
	 * observations and their states are not visible after contextualization.
	 * 
	 * @return
	 */
	public abstract boolean isMediated();

	/**
	 * Return true if this observation is mediating another in a mediation
	 * chain. Mediated observations can share observables with the ones that
	 * mediate them, but are second-class observations and their states are not
	 * visible after contextualization.
	 * 
	 * This is implemented as (getMediatedObservation() != null), provided in
	 * the interface for completeness.
	 * 
	 * @return
	 */
	public abstract boolean isMediator();


	/**
	 * Return the extent observation for the passed concept if we have it, or null.
	 * @param observable
	 * @return
	 */
	public abstract IObservation getExtent(IConcept observable);

	/**
	 * True if it can be run in a context that goes beyond the natural context of
	 * the observation or its dependencies, meaning that no-data values will occur
	 * across the context and that large scaling errors are possible. True by default.
	 *  
	 * @return
	 */
	boolean acceptsContextExtrapolation();

	/**
	 * True if it can be run in a context with disconnected topologies, e.g. spatial
	 * or temporal holes. 
	 * 
	 * @return
	 */
	boolean acceptsDiscontinuousTopologies();

	
	/**
	 * Observations have metadata, like models and every modeling object.
	 * @return
	 */
	public abstract HashMap<String, Object> getMetadata();


}
