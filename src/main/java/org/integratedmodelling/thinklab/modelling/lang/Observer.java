package org.integratedmodelling.thinklab.modelling.lang;

import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabUnsupportedOperationException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.IScenario;
import org.integratedmodelling.thinklab.api.modelling.parsing.IExpressionDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IFunctionDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IObserverDefinition;

@Concept(NS.OBSERVER)
public abstract class Observer<T> extends ObservingObject<T> implements IObserverDefinition {

	IObserver _mediated = null;
	IAccessor _accessor = null;
	
	@Override
	public IContext getUnresolvedContext(IContext totalContext)  {
		return totalContext;
	}
	
	/**
	 * Add one observer with an optional conditional expression to contextualize the model to use. Creation
	 * of conditional observers if more than one observer is added or there are conditions is
	 * handled transparently.
	 * 
	 * @param observer
	 * @param expression
	 */
	@Override
	public void addMediatedObserver(IObserverDefinition odef, IExpressionDefinition edef) {
		
		IObserver observer = (IObserver)odef;
		IExpression expression = (IExpression)edef;
		
		if (_mediated == null && expression == null) {
			_mediated = observer;
		} else {
			if (_mediated == null) {
				_mediated = new ConditionalObserver();
			} else if (	!(_mediated instanceof ConditionalObserver)) {
				ConditionalObserver obs = new ConditionalObserver();
				obs.addObserver(null, (IObserverDefinition) _mediated);
				_mediated = obs;
			}
			((ConditionalObserver)_mediated).addObserver(edef, odef);
		}
	}
	
	public IObserver getMediated() {
		return _mediated;
	}

	@Override
	public IAccessor getAccessor() {
		return _accessor;
	}

	@Override
	public List<IObservation> observe(IContext context)
			throws ThinklabException {
		return null;
	}

	@Override
	public IObserver train(IContext context) throws ThinklabException {
		return this;
	}

	@Override
	public IObserver applyScenario(IScenario scenario) throws ThinklabException {
		return this;
	}

	@Override
	public void setAccessorGeneratorFunction(IFunctionDefinition function) {
		System.out.println("accessor " + function);
	}
	

	
}
