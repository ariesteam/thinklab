package org.integratedmodelling.corescience.interfaces.internal;

import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

public interface TransformingObservation extends IObservation {

	/**
	 * Return the context that this observation will have once transformed in the 
	 * passed context. Should not actually perform the transformation if at all possible.
	 * If that is necessary in order to know the context, it should store the result and
	 * return when transform() is called. The preferred way is to compute the transformation 
	 * when transform() is called, so that context extraction can be done quickly.
	 * 
	 * @return
	 * @throws ThinklabException
	 */
	public IObservationContext getTransformedContext(IObservationContext context)
		throws ThinklabException;
	
	/**
	 * Transform the observation and return the new IObservation that takes its
	 * place in the compiler.
	 * 
	 * @return
	 */
	public IInstance transform(IInstance sourceObs, ISession session, IObservationContext context) throws ThinklabException;

	/**
	 * Return the class of the transformed observation
	 * @return
	 */
	public IConcept getTransformedObservationClass();
	
}
