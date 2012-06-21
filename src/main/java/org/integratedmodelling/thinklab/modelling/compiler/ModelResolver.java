package org.integratedmodelling.thinklab.modelling.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.collections.Triple;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.annotation.SemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.ITopologicallyComparable;
import org.integratedmodelling.thinklab.modelling.debug.ModelGraph;
import org.integratedmodelling.thinklab.modelling.lang.Context;
import org.integratedmodelling.thinklab.modelling.lang.Model;
import org.integratedmodelling.thinklab.modelling.lang.Observer;
import org.integratedmodelling.thinklab.query.Queries;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * A resolver for models in a namespace. Will apply heuristics and configured rules to determine
 * the best model to observe a given observable. The rules are reconfigurable on a namespace
 * basis, with sensible global defaults.
 * 
 * Criteria for final scoring of model as of this revision (TODO/CHECK):
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
 * The resolve(Object) function will return a model that will observe the
 * passed object. If the passed object is a IModel, it will return the 
 * same model after computing the best strategy to observe its observable,
 * or null if there is no such strategy. After resolve() has returned
 * a non-null model, the model structure can be retrieved in the form
 * of a graph, using getModelStructure(). 
 * 
 * The structure can be visualized with the ModelGraph helper class and
 * passed to a Contextualizer for contextualization.
 * 
 * @author Ferd
 *
 */
public class ModelResolver {

	INamespace _namespace;
	IContext   _context;
	
	/**
	 * Create a model resolver for models in a given context, using the 
	 * model resolution strategy defined for the passed namespace.
	 * 
	 * @param namespace
	 */
	public ModelResolver(INamespace namespace, IContext context) {
		_namespace = namespace;
		_context = context;
	}
	
	public class DependencyEdge extends DefaultEdge {
		
		public DependencyEdge(boolean b, String formalName) {
			isMediation = b;
			this.formalName = formalName;
		}
		private static final long serialVersionUID = 2366743581134478147L;
		public boolean isMediation = false;
		public boolean isInitialization = false;
		public String formalName = null;
		public ISemanticObject<?> observable = null;

		@Override
		public boolean equals(Object edge) {
			return 
				edge instanceof DependencyEdge &&
				this.getSource().equals(((DependencyEdge)edge).getSource()) &&
				this.getTarget().equals(((DependencyEdge)edge).getTarget()) &&
				isMediation == ((DependencyEdge)edge).isMediation;
		}
	}

	DirectedGraph<IModel, DependencyEdge> _structure = null;

	/**
	 * Main entry point. You can resolve a model or another semantic object, which is
	 * interpreted to be the observable you want to observe. If this one returns
	 * true, you can call getAccessorGraph() to retrieve the actual model
	 * algorithm. If it returns null, there is no strategy to observe the
	 * rootModel in this context with the current model base.
	 * 
	 * @param rootModel
	 * @param context
	 * @throws ThinklabException 
	 */
	public IModel resolve(ISemanticObject<?> rootModel) throws ThinklabException {
		
		HashMap<String, IModel> resolved = new HashMap<String, IModel>();
		
		IModel ret = resolveInternal(rootModel, _context, resolved, false);
		
		if (ret != null) {
			
			/*
			 * if we have no errors, build the final model graph
			 * and store it for later use.
			 */
			_structure = buildModelGraph(ret, resolved);

			// TODO REMOVE - debug
			new ModelGraph(_structure).dump(true);
		}
	
		return ret;
	}

	public DirectedGraph<IModel, DependencyEdge> getModelStructure() {
		return _structure;
	}
	
	private DefaultDirectedGraph<IModel, DependencyEdge> buildModelGraph(IModel observable, HashMap<String, IModel> resolved) {

		DefaultDirectedGraph<IModel, DependencyEdge> graph =
				new DefaultDirectedGraph<IModel, DependencyEdge>(DependencyEdge.class);

		buildModelGraphInternal(observable, resolved, graph);
		
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
	public IModel resolveInternal(ISemanticObject<?> toResolve, IContext context, HashMap<String, IModel> models, boolean isOptional)
			throws ThinklabException {
		
		IModel ret = null;
		
		/*
		 * setup hash if we're calling this for the first time.
		 */
		if (models == null) {
			
			models = new HashMap<String, IModel>();
			
			/*
			 * fill in what we already know and is already harmonized with the context.
			 */
			for (IState state : context.getStates()) {
				models.put(((SemanticObject<?>)(state.getObservable())).getSignature(), promoteStateToModel(state));
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
			for (Triple<Object, String, Boolean> m : model.getDependencies()) {
				boolean opt = m.getThird();
				IModel resolved = resolveInternal((ISemanticObject<?>)(m.getFirst()), context, models, opt || isOptional);
				if (resolved == null && isOptional && !opt) {
					return null;
				} else {
					/*
					 * TODO log missing dependency
					 */
				}
			}

			/*
			 * resolve the dependencies of the observer. We inherit the coverage from theirs.
			 */
			IObserver observer = model.getObserver();
			for (Triple<Object, String, Boolean> m : observer.getDependencies()) {
				boolean opt = m.getThird();
				IModel resolved = resolveInternal((ISemanticObject<?>)(m.getFirst()), context, models, opt || isOptional);
				if (resolved == null && isOptional && !opt) {
					return null;
				} else {
					/*
					 * TODO log missing dependency
					 */
				}
			}
				
			/*
			 * if it has a datasource and we get here, it's a match and the observable is only to provide
			 * semantics, we don't need to resolve it.
			 */
			if (model.getDatasource() == null) {
				
				/*
				 * ask for whatever the observer needs to resolve the initial states, not necessarily
				 * the whole context.
				 */
				IModel endpoint = 
						resolveInternal(
								((Observer<?>)observer).getFinalObservable(), 
								observer.getUnresolvedContext(context), 
								models,
								false);
			
				if (endpoint == null) {
					/*
					 * TODO log missing dependency
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
			if (!isAcyclic(model, models)) {
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
			if (models.containsKey(sig) && isAcyclic(models.get(sig), models)) {
				return models.get(sig);
			}
			
			IContext coverage = new Context();
			ArrayList<IModel> mods = new ArrayList<IModel>();

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
				m = resolveInternal((ISemanticObject<?>) m, context, models, false);
				if (m == null)
					continue;
				
				/*
				 * don't use it at all if using it would create circular dependencies.
				 * TODO log strategy
				 */
				if (!isAcyclic(m, models))
					continue;

				/*
				 * add model to list
				 * TODO: merge extent metadata into coverage
				 */
				mods.add(m);
				
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
			if (mods.size() > 1) {
				ret = new Model(observable, mods);
			} else {
				ret = mods.size() == 0 ? null : mods.get(0);
			}	
		}

		/*
		 * if we found a model to resolve this observable, set it as
		 * the resolver for ALL the observables it can resolve.
		 */
		if (ret != null) {
						
			for (ISemanticObject<?> obs : ret.getObservables()) {
				models.put(((SemanticObject<?>)obs).getSignature(), ret);
			}
		}
		
		return ret;
	}	
	
	/*
	 * for simple handling throughout the algorithm, although later we'll just use the state as
	 * an accessor and throw away the model.
	 */
	private IModel promoteStateToModel(IState state) {
		
		/**
		 * A dumb model that simply publishes a state, meant to facilitate using a
		 * precomputed state in a model graph.
		 * 
		 * @author Ferd
		 */
		class StateModel extends Model {

			class StateObserver extends Observer<StateObserver> {

				IState _state;

				public StateObserver(IState state) {
					_state = state;
				}
				
				@Override
				public IAccessor getNaturalAccessor(IContext context) {
					return _state;
				}

				@Override
				public IState createState(ISemanticObject<?> observable, IContext context)	
						throws ThinklabException {
					return _state;
				}

				@Override
				public StateObserver demote() {
					return this;
				}
			}
			
			public StateModel(IState state) {
				addObserver(new StateObserver(state), null);
			}
			
		}
		
		return new StateModel(state);
	}

	private List<ISemanticObject<?>> getSuitableModels(SemanticObject<?> observable,
			IContext context) throws ThinklabException {
		
		List<Pair<IProperty, ITopologicallyComparable<?>>> coverageProperties = 
			((Context)context).getCoverageProperties();
		
		/*
		 * Start with the basic query for the observable.
		 * 
		 * TODO move the direct data requirement to the sorting criteria.
		 * 
		 */
		IQuery query = Queries.select(NS.MODEL).
			restrict(NS.HAS_DIRECT_DATA, Queries.is(true)).
			restrict(NS.HAS_OBSERVABLE, Queries.is(observable));

		/*
		 * restrict for coverage.
		 * 
		 * TODO this should be either the right coverage or
		 * no coverage at all, with a sorting criterion to ensure that those objects
		 * that declare coverage are considered first. This way it works only with
		 * data that are distributed in that same extent.
		 */
		for (Pair<IProperty, ITopologicallyComparable<?>> cp : coverageProperties) {
			query = query.restrict(cp.getFirst(), Queries.intersects(cp.getSecond()));
		}

		/*
		 * TODO sort by criteria configured for namespace (or sensible default).
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
	public boolean isAcyclic(IModel model, HashMap<String, IModel> models) {
		
		/*
		 * build array of currently accepted models and add the passed one
		 */
		HashSet<IModel> models2 = new HashSet<IModel>();
		for (IModel m : models.values()) {
			models2.add(m);
		}
		models2.add(model);
		
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
		for (IModel mm : models2)
			buildModelGraphInternal(mm, models, graph);

		/*
		 * check that resulting graph is acyclic
		 */
		CycleDetector<IModel, DependencyEdge> cd = new CycleDetector<IModel, ModelResolver.DependencyEdge>(graph);
		
		return !cd.detectCycles();
	}

	private void buildModelGraphInternal(IModel model, HashMap<String, IModel> models,
			DefaultDirectedGraph<IModel, DependencyEdge> graph) {
		
		graph.addVertex(model);

		for (Triple<Object, String, Boolean> m : model.getDependencies()) {

			for (ISemanticObject<?> obs : getObservables(m.getFirst())) {
				IModel dep = models.get(((SemanticObject<?>)obs).getSignature());
				if (dep != null) {
					buildModelGraphInternal(dep, models, graph);
					DependencyEdge edge = new DependencyEdge(false, m.getSecond());
					edge.observable = obs;
					graph.addEdge(model, dep, edge);
				}
			}
		}
		
		IObserver observer = model.getObserver();
		for (Triple<Object, String, Boolean> m : observer.getDependencies()) {
			for (ISemanticObject<?> obs : getObservables(m.getFirst())) {
				IModel dep = models.get(((SemanticObject<?>)obs).getSignature());
				if (dep != null) {
					buildModelGraphInternal(dep, models, graph);
					DependencyEdge edge = new DependencyEdge(false, m.getSecond());
					edge.observable = obs;
					graph.addEdge(model, dep, edge);
				}
			}
		}
		
		if (model.getDatasource() == null) {		
			ISemanticObject<?> obs = ((Observer<?>)observer).getFinalObservable();
			IModel dep = models.get(((SemanticObject<?>)obs).getSignature());
			if (dep != null) {
				buildModelGraphInternal(dep, models, graph);
				DependencyEdge edge = new DependencyEdge(true, null);
				edge.isInitialization = true;
				graph.addEdge(model, dep, edge);
			}
		}
		
	}
	
	private Collection<ISemanticObject<?>> getObservables(Object obj) {

		if (obj instanceof IModel)
			return ((IModel)obj).getObservables();
		if (obj instanceof ISemanticObject<?>) {
			return new ArrayList<ISemanticObject<?>>(Collections.singleton((ISemanticObject<?>)obj));
		}
		return new ArrayList<ISemanticObject<?>>();
	}


}
