package org.integratedmodelling.corescience.interfaces.internal;

import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;

/**
 * Trainable observations can be fed with expected outputs as well as input
 * dependencies, and produce a new observation with updated internal 
 * configuration or dependent states that gives the best approximation of
 * the outputs.
 * 
 * 
 * 
 * @author Ferdinando Villa
 *
 */
public interface TrainableObservation extends IObservation {
	
	/**
	 * Train the observation and return a new observation with adjusted
	 * internal configuration or dependencies.
	 * 
	 * @return
	 */
	public IInstance train(IInstance sourceObs, ISession session, IObservationContext context) throws ThinklabException;
	
}
