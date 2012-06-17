package org.integratedmodelling.thinklab.modelling.lang;

import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.IScenario;
import org.integratedmodelling.thinklab.api.modelling.parsing.IExpressionDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IFunctionDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IObserverDefinition;
import org.integratedmodelling.thinklab.modelling.interfaces.IChainingAccessor;

@Concept(NS.OBSERVER)
public abstract class Observer<T> extends ObservingObject<T> implements IObserverDefinition {

	@Property(NS.HAS_MEDIATED_OBSERVER)
	IObserver _mediated = null;
	
	@Property(NS.HAS_ACCESSOR_FUNCTION)
	IFunctionDefinition _accessorGenerator = null;
	
	
	
	IAccessor _accessor = null;
	
	
	/**
	 * This one needs to be implemented by all derived observers. The result will be the
	 * accessor returned by (final) getAccessor, unless a 'with' spec has created another
	 * one. In this case, the 'with' accessor will be chained to the natural one if it is
	 * a IChainingAccessor so it will be able to use it.
	 * @param context TODO
	 * 
	 * @return
	 */
	public abstract IAccessor getNaturalAccessor(IContext context);
	
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
	
	@Override
	public IObserver getMediatedObserver() {
		return _mediated;
	}
	
	public Object getObservedObject() {
		return _mediated == null ? _observables.get(0) : _mediated;
	}

	@Override
	public final IAccessor getAccessor(IContext context) {
		
		IAccessor ret = _accessor;
		
		if (ret instanceof IChainingAccessor) {
			
			IAccessor na = getNaturalAccessor(context);
			if (na != null)
				((IChainingAccessor)ret).chain(na);
			
		} else if (ret == null) {
			ret = getNaturalAccessor(context);
		}
			
		return ret;
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
		_accessorGenerator = function;
	}

	@Override
	public void initialize() throws ThinklabException {

		super.initialize();

		if (_mediated != null)
			((Observer<?>)_mediated).initialize();
		
		/*
		 * define our mediated object if any - may be an observable to
		 * mediate, or an explicitly mediated observer (which has its
		 * own observer).
		 */
		
		if (_accessorGenerator != null) {
			
			IExpression func = 
					Thinklab.get().resolveFunction(
							_accessorGenerator.getId(), 
							_accessorGenerator.getParameters().keySet());
			if (func == null)
				throw new ThinklabValidationException("function " + _accessorGenerator.getId() + " cannot be resolved");
			Object ds = func.eval(_accessorGenerator.getParameters());
			if (! (ds instanceof IAccessor)) {
				throw new ThinklabValidationException("function " + _accessorGenerator.getId() + " does not return a datasource");
			}
			_accessor = (IAccessor)ds;
		}
	}

	public ISemanticObject<?> getFinalObservable() {

		return _mediated == null ? 
					_observables.get(0) : 
					((Observer<?>)_mediated).getFinalObservable();
	}
	
}
