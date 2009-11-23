/**
 * IContextualizationWorkflow.java
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

import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

/**
 * Testing new approach to contextualization. Not used anywhere except for development.
 */
public interface IContextualizationCompiler {

	/**
	 * Should analyze the CMs along the passed observation structure and determine if
	 * it can be compiled successfully. Making it relatively fast will pay - will be
	 * called once per observation structure but all the available compilers will get
	 * called.
	 * 
	 * FIXME this should be implemented in AbstractCompiler using a callback on the
	 * conceptual model, which is the one that should accept compilers, not the other
	 * way around.
	 * 
	 * @param observation
	 * @return
	 */
	public boolean canCompile(IObservation observation);
	
	/**
	 * Notify an observation that will need to be part of the workflow.
	 * @param observation
	 */
	public abstract void addObservation(IObservation observation);
	
	/**
	 * Notify that destination observation depends on source observation.
	 * @param destination
	 * @param source
	 * @throws ThinklabException 
	 */
	public abstract void addObservationDependency(IObservation destination, IObservation source) throws ThinklabException;
	
	/**
	 * During context generation, each observation gets its own context that depends on the 
	 * overall one. This callback is passed the mediated context of each observation. Because
	 * observable classes must be unique in an observation structure, this is indexed by
	 * observable class rather than observation.
	 * 
	 * @param observable
	 * @param context
	 */
	public abstract void notifyContext(IConcept observable, IObservationContext context);
	
	/**
	 * Notify that the state of destination observation will be taken from the state of 
	 * source destination, involving possible mediation of conceptual models and extents so
	 * that the state of source is seen by destination under its own viewpoint.
	 *  
	 * @param destination
	 * @param source
	 */
	public abstract void addMediatedDependency(IObservation destination, IObservation source);

	/**
	 * Compile a contextualizer that can be run to produce a new observation with datasources that reflect
	 * states in the passed overall context.
	 */
	public abstract IContextualizer compile(IObservation observation, IObservationContext context) throws ThinklabException;

	/**
	 * Return all the observations we have been notified so far.
	 * @return
	 */
	public Collection<IObservation> getObservations();

	/*
	 * just a setter/getter - we need a way to inform the observation that the obs comes from a transformer, so no further
	 * compilation is necessary.
	 */
	public void setTransformedObservation(IConcept observable, IInstance instance);

	/*
	 * just set/get. Ugly, I know.
	 */
	public IInstance getTransformedObservation(IConcept observable);
	
}
