package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.metadata.IMetadata;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IDataSource;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.parsing.IExpressionDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IFunctionDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IModelDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IObserverDefinition;
import org.integratedmodelling.thinklab.interfaces.IStorageMetadataProvider;
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
	
	/**
	 * Ensure that we get stored if we have a non-trivial datasource and no 
	 * dependencies, so we can be used to resolve dangling references.
	 */
	@Override
	public IMetadata getStorageMetadata() {

		IMetadata ret = null;
		
		if (_datasource != null & _dependencies.size() == 0 &&
			_datasource instanceof IStorageMetadataProvider) {
			
			ret = new Metadata();
			
			((IStorageMetadataProvider)_datasource).addStorageMetadata(ret);			
		}
		
		return ret;
	}

	/*
	 * Return the observer's accessor after publishing our dependencies, datasource etc.
	 * This function takes a CompiledContext reflecting the same info in the context
	 * passed to observe(), and modifies it by adding the accessor tree and the resolver
	 * for any unresolved dependencies along the model structure. 
	 */
	IAccessor getAccessor(CompiledContext context) throws ThinklabException {
		
		Observer<?> observer = (Observer<?>) _observer;

		/*
		 * see if we have a datasource and if so, have it return the accessor and
		 * be done with it.
		 */
		if (_datasource != null)
			return _datasource.getAccessor(context);

		/*
		 * if we have dependencies, they're actually for a switching observer
		 * so give them to it.
		 */
		
		/*
		 * 
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
		
		IExpression func = Thinklab.get().resolveFunction(function.getId(), function.getParameters().keySet());
		try {
			if (func == null)
				throw new ThinklabValidationException("function " + function.getId() + " cannot be resolved");
			Object ds = func.eval(function.getParameters());
			if (! (ds instanceof IDataSource)) {
				throw new ThinklabValidationException("function " + function.getId() + " does not return a datasource");
			}
			_datasource = (IDataSource)ds;
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	@Override
	public void setInlineState(Object state) {
		_datasource = new ConstantDataSource(state);
	}

	@Override
	public void initialize() throws ThinklabException {
		
		/*
		 * this creates the observable if it was explicitly defined.
		 */
		super.initialize();
		
		if (_observer != null) {
			((Observer<?>)_observer).initialize();
		}

		/*
		 * see if we have any observable of our own; if not, take it
		 * from the observer, which must be single.
		 */
		if (_observables.size() == 0) {
			
			if (_observer == null)
				throw new ThinklabValidationException(
						"invalid model without either an observable or an observer");
			
			_observables.addAll(_observer.getObservables());
		}
	}

	
}
