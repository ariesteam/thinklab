package org.integratedmodelling.thinklab.modelling;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabUnsupportedOperationException;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.lang.parsing.IModelDefinition;
import org.integratedmodelling.thinklab.api.lang.parsing.IObserverDefinition;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IObserver;

public class Model extends ObservingObject implements IModelDefinition {

	IObserver _observer;
	
	@Override
	public void setObserver(IObserverDefinition observer) {
		_observer = (IObserver) observer;
	}

	@Override
	public IObserver getObserver() {
		return _observer;
	}

	@Override
	public IObservation contextualize(IContext context)
			throws ThinklabException {
		throw new ThinklabUnsupportedOperationException("models cannot be contextualized at the client side");
	}

	@Override
	public IList getObservable() {
		return getObservables().get(0);
	}


}
