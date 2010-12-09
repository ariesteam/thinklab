package org.integratedmodelling.corescience.interfaces.internal;

import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.utils.Polylist;

public interface TransformingObservation extends IObservation {

	
	/**
	 * Transform the observation and return the list definition of the new IObservation that takes its
	 * place in the compiler.
	 * 
	 * @return
	 */
	public Polylist transform(IInstance sourceObs, ISession session, IObservationContext context) throws ThinklabException;

	/**
	 * Return the class of the transformed observation
	 * @return
	 */
	public IConcept getTransformedObservationClass();
	
}
