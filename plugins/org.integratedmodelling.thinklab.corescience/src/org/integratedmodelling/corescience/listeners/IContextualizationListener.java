package org.integratedmodelling.corescience.listeners;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservation;

/**
 * An array of contextualization listeners can be passed to Compiler.contextualize() to be notified
 * of "stepping stones" in model computation. Because contextualization is done in parallel except
 * when context changes happen, the listener callback is invoked only when observations that transform
 * their context (whose conceptual model is an instance of TransformingConceptualModel are
 * contextualized. 
 * 
 * See bug TLC-37.
 * 
 * @author Ferdinando
 * @date Aug 26, 2009
 */
public interface IContextualizationListener {

	/**
	 * Called after an observation has been contextualized. The contextualized obs
	 * is the second parameter; the first is the original one that produced it, and
	 * the third is the context of contextualization.
	 * 
	 * @param obs
	 * @param iObservation
	 * @param observationContext
	 */
	public abstract void onContextualization(
			IObservation original, IObservation obs, ObservationContext context);

	/**
	 * Called before a TransformerObservation is transformed. The contextualized obs
	 * (the one passed to transform()) is the second parameter; the first is the original one that produced it, and
	 * the third is the context of contextualization (pre-transformation, i.e. the original
	 * context set in the source obsevation).
	 * 
	 * @param obs
	 * @param iObservation
	 * @param observationContext
	 */
	public abstract void postTransformation(IObservation original,
			IObservation obs, ObservationContext context);

	/**
	 * Called after a TransformerObservation is transformed. The transformed obs
	 * (the result of transform()) is the second parameter; the first is the original one that produced it, and
	 * the third is the context of the transformed observation.
	 * 
	 * @param obs
	 * @param iObservation
	 * @param observationContext
	 */
	public abstract void preTransformation(IObservation original,
			IObservation obs, ObservationContext context);
}
