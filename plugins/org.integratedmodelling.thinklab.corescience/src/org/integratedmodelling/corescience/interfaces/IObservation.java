/**
 * IObservation.java
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

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

public interface IObservation {

	/**
	 * 
	 * @return
	 * @throws ThinklabException 
	 * @model
	 */
	public abstract IDataSource getDataSource() throws ThinklabException;
	
	/**
	 * Return the observable. It could be either a class or an instance. Can't be null.
	 * 
	 * @return the observable for this observation
	 */
	public abstract IKnowledgeSubject getObservable();
	
	/**
	 * Get the class of the main observable.
	 * @return
	 * @model
	 */
	public abstract IConcept getObservableClass();
	
	/**
	 * 
	 * @return
	 * @model
	 */
	public abstract IConceptualModel getConceptualModel() throws ThinklabException;
	

	/**
	 * Return the class of the observation instance we implement.
	 * @return
	 */
	public abstract IConcept getObservationClass();

	/**
	 * Obtain the observation context for this observation, recursing and compounding contingencies
	 * and dependencies. Use the passed workflow to notify dependencies and links.
	 * 
	 * @param workflow
	 * @param compound 
	 * @return
	 * @throws ThinklabException
	 */
	public IObservationContext getOverallObservationContext(IContextualizationWorkflow workflow) throws ThinklabException;
		
	/**
	 * Get the observation's own observation context, including extents and values only from the directly linked
	 * dependencies. No recursion happens.
	 * 
	 * @return a new observation context that's specific of the observation. 
	 * @throws ThinklabException 
	 */
	public IObservationContext getObservationContext() throws ThinklabException;
		
	/**
	 * Return the observation instance.
	 * @return
	 */
	public abstract IInstance getObservationInstance();

	/**
	 * Return a collection of all observations on which this one depends. 
	 * @return
	 */
	public abstract IObservation[] getDependencies();

	/**
	 * Return a collection of all observations that are contingent to this one. 
	 * @return
	 */
	public abstract IObservation[] getContingencies();

	/**
	 * Return the calculated observation state. Typically only available after contextualization.
	 * @return the state in the current context, which may well be null. Should throw an exception
	 * if the state makes no sense (e.g. no contextualization was done) but not if the state is 
	 * legitimately null.
	 * 
	 * @throws ThinklabException 
	 */
	public IObservationState getObservationState() throws ThinklabException;
	
	/**
	 * Return the observation context after the latest contextualization, or null if no
	 * contextualization happened. Goes with getObservationState() as far as timing
	 * and availability go. It's not the most elegant of things, but it allows
	 * simple retrieval of contextualization results (with the default workflow
	 * at least) without having to create a whole new observation structure.
	 * 
	 * @return
	 * @throws ThinklabException
	 */
	public IObservationContext getCurrentObservationContext() throws ThinklabException;
	
	/**
	 * Contextualization using a specified workflow. 
	 * 
	 * @param workflow
	 * @throws ThinklabException 
	 */
	public IValue contextualize(IContextualizationWorkflow workflow) throws ThinklabException;
	
	/**
	 * One-step contextualization using default workflow.

	 * @return an ObjectReferenceValue containing a new observation structure, whose states are calculated
	 * for the overall context. All datasources in the result are static and all mediators and links
	 * have been eliminated.
	 */
	public IValue contextualize() throws ThinklabException;


}
