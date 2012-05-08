package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IDataSource;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.parsing.IExpressionDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IFunctionDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IModelDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IObserverDefinition;
import org.integratedmodelling.thinklab.modelling.lang.datasources.ConstantDataSource;

@Concept(NS.MODEL)
public class Model extends ObservingObject<Model> implements IModelDefinition {

	IObserver _observer;
	IDataSource _datasource;
	IFunctionDefinition _datasourceDefinition;

	/* ------------------------------------------------------------------------------
	 * local methods
	 * ------------------------------------------------------------------------------
	 */
	
	/*
	 * Return the observer's accessor after publishing our dependencies, datasource etc.
	 * This function takes a CompiledContext reflecting the same info in the context
	 * passed to observe(), and modifies it by adding the accessor tree and the resolver
	 * for any unresolved dependencies along the model structure. 
	 */
	IAccessor getAccessor(CompiledContext context) {
		
		Observer<?> observer = (Observer<?>) _observer;

		/*
		 * see if we have a datasource and if so, give it to the observer
		 * so it can create an accessor for it.
		 */

		/*
		 * if we have dependencies, the observer should use them, not us.
		 */
		
		return observer.getAccessor();
	}
	
	/* ------------------------------------------------------------------------------
	 * public API
	 * ------------------------------------------------------------------------------
	 */
	
	
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
	public IObservation observe(IContext context)
			throws ThinklabException {
		
		CompiledContext cc = new CompiledContext(context);
		cc.compile(this);
		return cc.run();
	
	}

	@Override
	public ISemanticObject<?> getObservable() {
		return getObservables().get(0);
	}

	@Override
	public Model demote() {
		return this;
	}

	@Override
	public void setDataSource(IDataSource datasource) {
		_datasource = datasource;
	}

	@Override
	public void setDatasourceGeneratorFunction(IFunctionDefinition function) {
		_datasourceDefinition = function;
	}

	@Override
	public void setInlineState(Object state) {
		
		_datasource = new ConstantDataSource(state);
	}

	@Override
	public void defineObservable() {
		// TODO Auto-generated method stub
		
	}

}
