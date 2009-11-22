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
package org.integratedmodelling.corescience.interfaces.observation;

import java.util.Collection;

import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IContextualizationCompiler;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.listeners.IContextualizationListener;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

/**
 * TODO this will need to have state (datasource) access methods, throwing
 * exceptions unless contextualized.
 * 
 * @author Ferdinando
 * 
 */
public interface IObservation {

	/**
	 * 
	 * @return
	 * @throws ThinklabException
	 * @model
	 */
	public abstract IDataSource<?> getDataSource() throws ThinklabException;

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
	 * 
	 * @return
	 * @model
	 */
	public abstract IConceptualModel getConceptualModel()
			throws ThinklabException;

	/**
	 * Return the class of the observation instance we implement.
	 * 
	 * @return
	 */
	public abstract IConcept getObservationClass();

	/**
	 * Get the observation's own observation context, including extents and
	 * values only from the directly linked dependencies. No recursion happens.
	 * 
	 * @return a new observation context that's specific of the observation.
	 * @throws ThinklabException
	 */
	public IObservationContext getObservationContext() throws ThinklabException;

	/**
	 * Return the observation instance of which this is the Java peer
	 * implementations.
	 * 
	 * @return
	 */
	public abstract IInstance getObservationInstance();

	/**
	 * Return a collection of all observations on which this one depends.
	 * 
	 * @return
	 */
	public abstract IObservation[] getDependencies();

	/**
	 * Return a collection of all observations that are contingent to this one.
	 * 
	 * @return
	 */
	public abstract IObservation[] getContingencies();

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
	 * Return a dependent or contingent observation of the passed observable, or
	 * null if it can't be found. Explores the whole structure of dependencies
	 * and contingencies recursively, stopping at mediators and not considering
	 * extents.
	 * 
	 * @param observable
	 * @return
	 */
	public abstract IObservation getObservation(IConcept observable);

	/**
	 * Return the contextualized state of a dependent or contingent observation
	 * of the passed observable, or null if the observation can't be found or it
	 * hasn't been contextualized. Explores the whole structure of dependencies
	 * and contingencies recursively, stopping at mediators and not considering
	 * extents.
	 * 
	 * @param observable
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract IContextualizedState getState(IConcept observable) throws ThinklabException;

	/**
	 * Contingencies do not arise as the product of contextualization but
	 * as the result of creating observations from models that need more
	 * than one observation structures. As such, it must be possible to
	 * add contingencies programmatically. The implementation must ensure
	 * that the contingency is linked both to the OWL and to the Java
	 * sides.
	 * @param instance
	 * @throws ThinklabException 
	 */
	public abstract void addContingency(IInstance instance) throws ThinklabException;

	/**
	 * If the observation has been given a formal name for linking in code or other
	 * reference, return it, otherwise return null. This is linked to the annotation
	 * property observation:hasFormalName.
	 *  
	 * @return
	 */
	public abstract String getFormalName();
	
	/**
	 * Return true if the passed observation has been contextualized in the given context.
	 * If so, we have already computed states for this context and compilation can be
	 * skipped. Must ensure that the context matches in all its dimensions.
	 * 
	 * @param context
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract boolean isContextualized(IObservationContext context) throws ThinklabException;
	
}
