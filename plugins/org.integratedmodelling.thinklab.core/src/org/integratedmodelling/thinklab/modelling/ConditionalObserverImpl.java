package org.integratedmodelling.thinklab.modelling;

import java.util.ArrayList;
import java.util.List;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.lang.model.ObservingObject;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IObserver;

public class ConditionalObserverImpl extends ObserverImpl {

	ArrayList<Pair<IObserver,IExpression>> _observers;
	IConcept _stateType = null;

	public List<Pair<IObserver,IExpression>> getObservers() {
		return _observers;
	}
	
	public ConditionalObserverImpl(ObservingObject bean) {
		super(bean);
	}

	@Override
	protected IAccessor getAccessor(IContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getStateType() {
		
		if (_stateType == null) {

			/*
			 * TODO ensure that all observers have a common state type but
			 * promoting deterministic to probabilistic if necessary.
			 */
		}
		return _stateType;
	}

	public void addObserver(IObserver observer, IExpression expression) {
		_observers.add(new Pair<IObserver, IExpression>(observer, expression));
	}


}
