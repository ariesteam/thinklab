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

import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.corescience.interfaces.observation.IObservationState;
import org.integratedmodelling.corescience.observation.Observation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * A IContextualizationWorkflow object must be passed to the context generator in
 * order to calculate states of an observation structure over a specific context.
 * 
 * A state machine that implements the operations needed to contextualize an
 * observation structure. It works asynchronously by recording the sequence of
 * operations and the context of their executions as its functions are called. When
 * run() is called, the sequence is executed, whatever that means in a particular
 * implementation. Some implementations may actually calculate states and produce
 * a recording or another observation structure with the contextualized states.
 * Others may write code or generate a flowchart of the operations. 
 * 
 * @author Ferdinando Villa
 * @see ContextGenerator#computeStates
 */
public interface IContextualizationWorkflow {

	/**
	 * Notify an observation that will need to be part of the workflow.
	 * @param observation
	 */
	public abstract void addObservation(IObservation observation);
	
	
	/**
	 * Notify that destination observation depends on source observation.
	 * @param destination
	 * @param source
	 */
	public abstract void addObservationDependency(IObservation destination, IObservation source);
	
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
	 * Create an implementation of IObservationState to hold N states of the specified type.
	 * 
	 * @param observation
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IObservationState createObservationState(
			IObservation observation,
			IConcept stateType,
			long nStates)
		throws ThinklabException;
	
	/*
	 * Produce the states.
	 */
	public abstract IValue run(IObservation observation, IObservationContext context) throws ThinklabException;


	/**
	 * Must return true if the passed observation has been added before.
	 * @param observation
	 * @return
	 */
	public abstract boolean hasObservation(Observation observation);
	
	
}
