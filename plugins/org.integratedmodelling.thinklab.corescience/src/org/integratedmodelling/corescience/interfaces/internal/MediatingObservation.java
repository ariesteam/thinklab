package org.integratedmodelling.corescience.interfaces.internal;

import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.thinklab.exception.ThinklabException;

public interface MediatingObservation extends IndirectObservation {

	public abstract IStateAccessor getMediator(IndirectObservation observation, IObservationContext context)
		throws ThinklabException;
	
}
