package org.integratedmodelling.corescience.interfaces.cmodel;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.utils.Polylist;

/**
 * Transforming conceptual models modify not only values, but also contexts and
 * possibly the whole observation structure that they represent. For this
 * reason, observations that have transforming conceptual models are
 * contextualized in stages, exposing all transforming observations to the
 * result of their contextualization. Whatever observation is returned by 
 * transformObservation will be used for contextualization instead of the
 * original one, and its dependencies ignored.
 * 
 * There are two phases in contextualization of transformers: first, the observation
 * is independently contextualized. In order to do so, the observation must provide
 * the contextualizer with the observation type and the conceptual model that it
 * wants to be seen as when contextualized, otherwise the operation would result in
 * an endless recursion. 
 * 
 * After that is done, the result of the contextualization is passed to transformObservation,
 * which must return an observation structure that will be assumed as already contextualized
 * (no further contextualization occurs below the transformed obs, but contextualization will
 * proceed by merging our new extents with those above us). 
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

	/**
	 * If we transform the obs into another, we want to return specs for the cm of the
	 * transformed observation (the one that is passed as input to transformObservation and results from 
	 * contextualizing the structure of the transformer). Essentially it says: contextualize this 
	 * obs AS IF it had this CM, and pass it to transformObservation for further transformation.
	 * 
	 * @return
	 */
	public abstract Polylist getTransformedConceptualModel();

	/**
	 * Return the type of observation of the transformed observation (the one that is passed as input to 
	 * transformObservation and results from contextualizing the structure of the transformer). Essentially it says: 
	 * contextualize this obs AS IF it was this type, then pass it to transformObservation for further transformation. 
	 */
	public abstract IConcept getTransformedObservationClass();
	
}
