package org.integratedmodelling.corescience.interfaces.cmodel;

import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public interface MediatingConceptualModel {

	/**
	 * Return an accessor that will be notified only the state of the other observation we're mediating.
	 * 
	 * @param conceptualModel the mediated conceptual model.
	 * @param stateType
	 * @param context
	 * @return
	 */
	public abstract IStateAccessor getMediator(
			IConceptualModel conceptualModel, IConcept stateType,
			IObservationContext context) throws ThinklabException;
}
