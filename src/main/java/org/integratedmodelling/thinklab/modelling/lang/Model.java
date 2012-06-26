package org.integratedmodelling.thinklab.modelling.lang;

import java.util.ArrayList;
import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.annotation.SemanticObject;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.metadata.IMetadata;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IDataSource;
import org.integratedmodelling.thinklab.api.modelling.IExtent;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.parsing.IExpressionDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IFunctionDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IModelDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IObserverDefinition;
import org.integratedmodelling.thinklab.interfaces.IStorageMetadataProvider;
import org.integratedmodelling.thinklab.modelling.compiler.Contextualizer;
import org.integratedmodelling.thinklab.modelling.compiler.ModelResolver;
import org.integratedmodelling.thinklab.modelling.lang.datasources.ConstantDataSource;

@Concept(NS.MODEL)
public class Model extends ObservingObject<Model> implements IModelDefinition {

	@Property(NS.HAS_OBSERVER)
	IObserver _observer;
	
	@Property(NS.HAS_DATASOURCE_DEFINITION)
	IFunctionDefinition _datasourceDefinition;

	@Property(NS.HAS_INLINE_STATE)
	Object _inlineState;
	
	ArrayList<IExtent> _allowedCoverage = new ArrayList<IExtent>();
	
	IDataSource _datasource;
	IContext _allowedContext = new Context();

	private boolean _initialized;
	
	
	/* ------------------------------------------------------------------------------
	 * local methods
	 * ------------------------------------------------------------------------------
	 */
	
	public Model() {}
	
	/*
	 * specialized constructor that will build one new model with a conditional
	 * composition of the passed observers. Used only after model resolution.
	 */
	public Model(SemanticObject<?> observable, ArrayList<IModel> models) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Ensure that we get stored if we have a non-trivial datasource and no 
	 * dependencies, so we can be used to resolve dangling references.
	 */
	@Override
	public IMetadata getStorageMetadata() {

		IMetadata ret = new Metadata();
		
		((Metadata)ret).put(NS.HAS_DIRECT_DATA, new Boolean(_datasource != null));
		
		if (_datasource != null & _dependencies.size() == 0 &&
			_datasource instanceof IStorageMetadataProvider) {			
			((IStorageMetadataProvider)_datasource).addStorageMetadata(ret);			
		} else if (_allowedCoverage.size() > 0) {
			for (IExtent e : _allowedCoverage) {
				if (e instanceof IStorageMetadataProvider) {
					((IStorageMetadataProvider)e).addStorageMetadata(ret);
				}
			}
		}
		
		return ret;
	}
	
	public List<IDependency> getObserverDependencies() {
		return _observer.getDependencies();
	}
	
	/* ------------------------------------------------------------------------------
	 * public API
	 * ------------------------------------------------------------------------------
	 */
	
	@Override
	public IDataSource getDatasource() {
		return _datasource;
	}
	
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
		
		IObservation ret = null;
		IContext ctx = new Context((Context) context);
		ModelResolver resolver = new ModelResolver(this.getNamespace(), ctx);
		IModel root = resolver.resolve(this);
		if (root != null) {
			Contextualizer ctxer = new Contextualizer(resolver.getModelStructure());
			ret = ctxer.run(root, ctx);
		}
		return ret;
	}

	@Override
	public List<ISemanticObject<?>> getObservables() {
		return _observables;
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
		_inlineState = state;
	}

	@Override
	public void initialize() throws ThinklabException {
		
		if (_initialized) 
			return;
		
		if (_namespace == null && _namespaceId != null) {
			_namespace = Thinklab.get().getNamespace(_namespaceId);
		}
		
		
		if (_namespace == null)
			System.out.println("zarzamora");
		/*
		 * we only need it in models and contexts for now.
		 */
		_namespaceId = _namespace.getId();
		
		/*
		 * this creates the observable if it was explicitly defined.
		 */
		super.initialize();

		if (_datasourceDefinition != null) {
			
			IExpression func = 
					Thinklab.get().resolveFunction(
							_datasourceDefinition.getId(), 
							_datasourceDefinition.getParameters().keySet());
			if (func == null)
				throw new ThinklabValidationException("function " + _datasourceDefinition.getId() + " cannot be resolved");
			Object ds = func.eval(_datasourceDefinition.getParameters());
			if (! (ds instanceof IDataSource)) {
				throw new ThinklabValidationException("function " + _datasourceDefinition.getId() + " does not return a datasource");
			}
			_datasource = (IDataSource)ds;
				
		} else if (_inlineState != null) {
			_datasource = new ConstantDataSource(_inlineState);

		}
		
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
			
			_observables.add(((Observer<?>)_observer).getFinalObservable());
		}
		
		_initialized = true;
	}

	@Override
	public IContext getCoverage() {
		return _allowedContext;
	}

	@Override
	public void addCoveredExtent(IExtent extent) throws ThinklabException {
		_allowedContext.merge(extent);
		_allowedCoverage.clear();
		for (IExtent e : _allowedContext.getExtents()) {
			_allowedCoverage.add(e);
		}
	}

	
}
