package org.integratedmodelling.corescience.interfaces;

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


}
