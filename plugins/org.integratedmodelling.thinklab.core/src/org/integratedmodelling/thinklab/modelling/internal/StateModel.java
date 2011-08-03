package org.integratedmodelling.thinklab.modelling.internal;

import org.integratedmodelling.thinklab.api.knowledge.IConcept;

/**
 * This one identifies a model capable of describing a state. Such models are
 * conceptualizable and must identify a compatible observation type(s) for query 
 * and conceptualization. They also must be capable of producing a query for
 * the observations they describe.
 * 
 * @author Ferd
 *
 */
public interface StateModel {

	/**
	 * Return the type of observation that we can deal with if we need to
	 * be paired to data from a kbox. If we're compatible with more than
	 * one type, build a union of types in the model manager session and 
	 * return that.
	 * 
	 * @return
	 */
	public abstract IConcept getCompatibleObservationType();
}
