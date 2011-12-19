package org.integratedmodelling.modelling.interfaces;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * These are simple aggregators of a state over its whole extent. In order to be used, they need
 * to be tagged with the @Aggregator annotation and explicitly called wy ID in a model command. They will
 * be applied only to the concepts  subsumed by the target concepts specified in the
 * annotation.
 * 
 * @author Ferd
 *
 */
public interface IStateAggregator {

	public double aggregate(IState state, IContext context) throws ThinklabException;
	
}
