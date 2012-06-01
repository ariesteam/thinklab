package org.integratedmodelling.thinklab.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.integratedmodelling.collections.Triple;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.annotation.SemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.modelling.lang.Context;
import org.integratedmodelling.thinklab.modelling.lang.Model;
import org.integratedmodelling.thinklab.modelling.lang.Observer;
import org.integratedmodelling.thinklab.query.Queries;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * A resolver for models in a namespace. Will apply heuristics and configured rules to determine
 * the best model to observe a given observable. Such configuration is (potentially) namespace- and
 * project- specific, with global defaults.
 * 
 * Criteria for final score as of this revision:
 * 
 * 1. user-defined quality
 * 2. community-defined "hits" (choices when user is choosing) -- unimplemented
 * 3. whether resolved or not (data or model)
 * 4. fine-grainedness re: context
 * 5. coverage in context (static)
 * 6. procedural detail
 * 7. timeliness/age or distance from period median if time not in context.
 * 8. date of last ranking/update
 * 
 * @author Ferd
 *
 */
public class ModelResolver {

	INamespace _namespace;
	
	/**
	 * Create a model resolver using the model resolution strategy defined for the passed
	 * namespace.
	 * 
	 * @param namespace
	 */
	public ModelResolver(INamespace namespace) {
		_namespace = namespace;
	}
	
	class DependencyEdge extends DefaultEdge {
		
		public DependencyEdge(boolean b) {
			isMediation = b;
		}
		private static final long serialVersionUID = 2366743581134478147L;
		boolean isMediation = false;

		@Override
		public boolean equals(Object edge) {
			return 
				edge instanceof DependencyEdge &&
				this.getSource().equals(((DependencyEdge)edge).getSource()) &&
				this.getTarget().equals(((DependencyEdge)edge).getTarget()) &&
				isMediation == ((DependencyEdge)edge).isMediation;
		}
	}

	DefaultDirectedGraph<IModel, DependencyEdge> _modelstruc = null;
	IModel _root = null;
	SemanticObject<?> _rootObservable = null;
	
	private HashMap<String, IModel> _modHash;

	/**
	 * Main entry point. You can resolve a model or another semantic object, which is
	 * interpreted to be the observable you want to observe. If this one returns
	 * true, you can call getAccessorGraph() to retrieve the actual model
	 * algorithm. If it returns false, there is no strategy to observe the
	 * rootModel in this context with the current model base.
	 * 
	 * @param rootModel
	 * @param context
	 * @throws ThinklabException 
	 */
	public boolean resolve(ISemanticObject<?> rootModel, IContext context) throws ThinklabException {

		/*
		 * this makes us reentrant
		 */
		_modHash = null;
		
		/*
		 * store the top observable as a key to rebuild the model graph from the
		 * accumulated resolvers.
		 */
		_rootObservable = (SemanticObject<?>)( 
				rootModel instanceof IModel ? 
						((IModel)rootModel).getObservables().get(0) : 
						rootModel);
		
		/*
		 * go for it; if we can't resolve the model, there's no way we can observe anything.
		 */
		if ((_root = resolveInternal(rootModel, context, false)) == null) {
			return false;
		}
		
		/*
		 * if we have no errors, build the final graph and we can inspect it later.
		 */
		_modelstruc = buildModelGraph();
		
		return true;
	}
	
	private DefaultDirectedGraph<IModel, DependencyEdge> buildModelGraph() {

		DefaultDirectedGraph<IModel, DependencyEdge> graph =
				new DefaultDirectedGraph<IModel, DependencyEdge>(DependencyEdge.class);

		buildGraphInternal(_modHash.get(_rootObservable.getSignature()), _modHash.values(), graph);
		
		return graph;
	}

	/**
	 * Get the best model for the semantic object passed. If the passed object
	 * is a model, resolve all its dependencies, file its observables and return it. 
	 * Otherwise we assume the argument is an observable and look for models that
	 * observe it. If coverage of the best model for that observable is incomplete, we may build 
	 * a model that merges more than one, in order of score, to cover the context
	 * as fully as possible, stopping whenever coverage of the context is complete or
	 * we have no more models to cover it.
	 * 
	 * @param toResolve
	 * @return
	 * @throws ThinklabException 
	 */
	public IModel resolveInternal(ISemanticObject<?> toResolve, IContext context, boolean isOptional)
			throws ThinklabException {
		
		IModel ret = null;
		
		/*
		 * setup hash if we're calling this for the first time.
		 */
		if (_modHash == null) {
			
			_modHash = new HashMap<String, IModel>();
			
			/*
			 * fill in what we already know and is already harmonized with the context.
			 */
			for (IState state : context.getStates()) {
				_modHash.put(((SemanticObject<?>)(state.getObservable())).getSignature(), promoteStateToModel(state));
			}
		}
		
		if (toResolve instanceof Model) {
			
			IModel model = (Model)toResolve;

			/*
			 * if it is not covered by definition, return null right away. Otherwise compute
			 * how much of the context is covered and store it for later compounding.
			 */
			
			/*
			 * resolve all model dependencies and behave according to their optional status.
			 */
			for (Triple<IModel, String, Boolean> m : model.getDependencies()) {
				boolean opt = m.getThird();
				IModel resolved = resolveInternal((Model)(m.getFirst()), context, opt || isOptional);
				if (resolved == null && isOptional && !opt) {
					return null;
				}
			}

			/*
			 * resolve the dependencies of the observer. We inherit the coverage from theirs.
			 */
			IObserver observer = model.getObserver();
			for (Triple<IModel, String, Boolean> m : observer.getDependencies()) {
				boolean opt = m.getThird();
				IModel resolved = resolveInternal((Model)(m.getFirst()), context, opt || isOptional);
				if (resolved == null && isOptional && !opt) {
					return null;
				}
			}
				
			/*
			 * if it has a datasource and we get here, it's a match and the observable is only to provide
			 * semantics, we don't need to resolve it.
			 */
			if (model.getDatasource() == null) {
				
				IModel endpoint = 
						resolveInternal(((Observer<?>)observer).getFinalObservable(), context, false);
			
				if (endpoint == null) {
					/*
					 * TODO log unresolvable dependency
					 */
					return null;
				}
				
				/*
				 * TODO adjust coverage
				 */
			}
			
			/*
			 * check that we remain acylic
			 */
			if (!isAcyclic(model)) {
				/*
				 * TODO log why model won't fit
				 */
				return null;
			}
					
			
			/*
			 * if we get here, model's OK and we can use it.
			 */
			ret = model;
						
			
		}  else {
			
			/*
			 * observable: here is where we can get in trouble if we pick another
			 * complex model to resolve the observable, and we don't check for all
			 * problems that this may entail (e.g. circular dependencies).
			 */
			SemanticObject<?> observable = (SemanticObject<?>) toResolve;
			String sig = observable.getSignature();
			
			/*
			 * if we already have a strategy, use that unless it creates circular dependencies.
			 * If it does, we will look for another way to observe this that allows a computable
			 * model structure.
			 * 
			 * TODO log strategy
			 */
			if (_modHash.containsKey(sig) && isAcyclic(_modHash.get(sig))) {
				return _modHash.get(sig);
			}
			
			IContext coverage = new Context();
			ArrayList<IModel> models = new ArrayList<IModel>();

			/*
			 * scan models in order of decreasing quality.
			 * loop until we get the best coverage; stop if/when we get to 100%
			 * TODO log strategy
			 */			
			for (ISemanticObject<?> mo : getSuitableModels(observable, context)) {
				
				IModel m = (IModel)mo;
				
				/*
				 * resolve the model and move to the next if we can't use it
				 */
				m = resolveInternal((ISemanticObject<?>) m, context, false);
				if (m == null)
					continue;
				
				/*
				 * don't use it at all if using it would create circular dependencies.
				 * TODO log strategy
				 */
				if (!isAcyclic(m))
					continue;

				/*
				 * add model to list
				 * TODO: merge extent metadata into coverage
				 */
				models.add(m);
				
				/*
				 * compute coverage in all required dimensions, using
				 * extent metadata; break if context is fully covered.
				 */
				if (coverage.isCovered(IContext.ALL_EXTENTS))
					break;
			}
			
			/*
			 * finalize choice: if we need more than one model to cover the
			 * context, put their observers into a new conditional observer
			 * for a new model.
			 * 
			 * TODO: compute and store total coverage
			 */
			if (models.size() > 1) {
				ret = new Model(observable, models);
			} else {
				ret = models.size() == 0 ? null : models.get(0);
			}	
		}

		/*
		 * if we found a model to resolve this observable, set it as
		 * the resolver for ALL the observables it can resolve.
		 */
		if (ret != null) {
						
			for (ISemanticObject<?> obs : ret.getObservables()) {
				_modHash.put(((SemanticObject<?>)obs).getSignature(), ret);
			}
		}
		
		return ret;
	}

	/*
	 * for simple handling throughout the algorithm, although later we'll just use the state as
	 * an accessor and throw away the model.
	 */
	private IModel promoteStateToModel(IState state) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<ISemanticObject<?>> getSuitableModels(SemanticObject<?> observable,
			IContext context) throws ThinklabException {
		
		/*
		 * TODO obviously this is an incomplete query. Needs coverage and sorting criteria.
		 */
		IQuery query = Queries.select(NS.MODEL).
			restrict(NS.HAS_DIRECT_DATA, Queries.is(true)).
			restrict(NS.HAS_OBSERVABLE, Queries.is(observable));
		
		/*
		 * query all models and build score based on metadata
		 * 
		 * to search: get each extent in context, and if it is a storage metadata
		 * provider, ask its metadata and find the (single) property that points to a topologically
		 * comparable literal if any. If found, use that in a Queries.covers(..) query - or
		 * something like that.
		 * 
		 * START WITH DATA ONLY, WHEN IT WORKS GRADUATE TO OTHERS.
		 */
		
		/*
		 * must be covered at least some or not specify coverage at all (which 
		 * gives them a lower score)
		 */
		
		return Thinklab.get().getLookupKboxForNamespace(_namespace).query(query);
	}

	/**
	 * Try out the scenario of using the given model for its observable. Analyze the
	 * resulting graph and return whether it creates circular dependencies.
	 * 
	 * @param iModel
	 * 
	 * @return true if NO circular dependencies are created by using this model for the observable.
	 */
	public boolean isAcyclic(IModel model) {
		
		/*
		 * build array of currently accepted models and add the passed one
		 */
		HashSet<IModel> models = new HashSet<IModel>();
		for (IModel m : _modHash.values()) {
			models.add(m);
		}
		models.add(model);
		
		/*
		 * start at the root observable and resolve dependencies into graph as
		 * we go, ignoring unresolved nodes.
		 */
		DefaultDirectedGraph<IModel, DependencyEdge> graph =
				new DefaultDirectedGraph<IModel, DependencyEdge>(DependencyEdge.class);
		
		/*
		 * we do this for all models, and they should all get linked up properly eventually. At this
		 * point, the model that resolves the root observable may be undefined if we start from
		 * the observable.
		 */
		for (IModel mm : models)
			buildGraphInternal(mm, models, graph);

		/*
		 * check that resulting graph is acyclic
		 */
		CycleDetector<IModel, DependencyEdge> cd = new CycleDetector<IModel, ModelResolver.DependencyEdge>(graph);
		
		return !cd.detectCycles();
	}

	private void buildGraphInternal(IModel model, Collection<IModel> models,
			DefaultDirectedGraph<IModel, DependencyEdge> graph) {
		
		graph.addVertex(model);
		
		for (Triple<IModel, String, Boolean> m : model.getDependencies()) {

			for (ISemanticObject<?> obs : m.getFirst().getObservables()) {
				IModel dep = _modHash.get(((SemanticObject<?>)obs).getSignature());
				if (dep != null) {
					buildGraphInternal(dep, models, graph);
					graph.addEdge(model, dep, new DependencyEdge(false));
				}
			}
		}
		
		IObserver observer = model.getObserver();
		for (Triple<IModel, String, Boolean> m : observer.getDependencies()) {
			for (ISemanticObject<?> obs : m.getFirst().getObservables()) {
				IModel dep = _modHash.get(((SemanticObject<?>)obs).getSignature());
				if (dep != null) {
					buildGraphInternal(dep, models, graph);
					graph.addEdge(model, dep, new DependencyEdge(false));
				}
			}
		}
		
		if (model.getDatasource() == null) {		
			ISemanticObject<?> obs = ((Observer<?>)observer).getFinalObservable();
			IModel dep = _modHash.get(((SemanticObject<?>)obs).getSignature());
			if (dep != null) {
				buildGraphInternal(dep, models, graph);
				graph.addEdge(model, dep, new DependencyEdge(true));
			}
		}
		
	}
	
}
