package org.integratedmodelling.thinklab.modelling;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabUnsupportedOperationException;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.parsing.IExpressionDefinition;
import org.integratedmodelling.thinklab.api.lang.parsing.IModelDefinition;
import org.integratedmodelling.thinklab.api.lang.parsing.IObserverDefinition;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IObserver;

public class Model extends ObservingObject implements IModelDefinition {

	IObserver _observer;
	
	@Override
	public void addObserver(IObserverDefinition odef, IExpressionDefinition edef) {
		
		IObserver observer = (IObserver)odef;
		IExpression expression = (IExpression)edef;
		
		if (_observer == null && expression == null) {
			_observer = observer;
		} else {
			if (_observer == null) {
				_observer = new ConditionalObserver();
			} else if (	!(_observer instanceof ConditionalObserver)) {
				ConditionalObserver obs = new ConditionalObserver();
				obs.addObserver(null, (IObserverDefinition) _observer);
				_observer = obs;
			}
			((ConditionalObserver)_observer).addObserver(edef, odef);
		}
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
	public ISemanticObject<?> getObservable() {
		return getObservables().get(0);
	}


}
