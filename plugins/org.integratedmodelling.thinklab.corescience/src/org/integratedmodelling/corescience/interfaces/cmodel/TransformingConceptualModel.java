package org.integratedmodelling.corescience.interfaces.cmodel;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

/**
 * Transforming conceptual models modify not only values, but also contexts and
 * possibly the whole observation structure that they represent. For this
 * reason, observations that have transforming conceptual models are
 * contextualized in stages, exposing all transforming observations to the
 * result of their contextualization first, depth first, then removing all outgoing 
 * edges from the dependency structure and contextualizing the resulting
 * structure until no more transformations remain.
 * 
 * @author Ferdinando Villa
 * 
 */
public interface TransformingConceptualModel {

	/**
	 * Receives the contextualized observation and gets a chance to transform into 
	 * another, as similar or different as it wants.
	 * 
	 * @param inst
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IInstance transformObservation(IInstance inst)
			throws ThinklabException;

	
	
}
