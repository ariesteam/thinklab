package org.integratedmodelling.corescience.interfaces.internal;

import java.util.Map;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * These can be inserted in a context to modify a state during contextualization.
 * 
 * @author ferdinando.villa
 *
 */
public interface IContextTransformation {

	public abstract Object transform(
			Object original, 
			IContext context, 
			int stateIndex, 
			Map<?,?> parameters);
	
	/**
	 * The transformation must be able to return a fresh
	 * copy of itself, serving as a factory, so that implementations
	 * can rely on state encountered for caching context information.
	 * 
	 * @return
	 */
	public abstract IContextTransformation newInstance();
	
	/**
	 * Return the observable class this applies to.
	 * @return
	 */
	public abstract IConcept getObservableClass();
}
