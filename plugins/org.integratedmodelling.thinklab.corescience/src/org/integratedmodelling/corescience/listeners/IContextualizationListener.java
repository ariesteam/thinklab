package org.integratedmodelling.corescience.listeners;

import org.integratedmodelling.corescience.interfaces.observation.IObservation;

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

	public abstract void onContextualization(IObservation obs);
}
