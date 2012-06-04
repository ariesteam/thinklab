package org.integratedmodelling.thinklab.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.collections.Triple;
import org.integratedmodelling.common.HashableObject;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.annotation.SemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IComputingAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IMediatingAccessor;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.ISerialAccessor;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.ITopologicallyComparable;
import org.integratedmodelling.thinklab.modelling.interfaces.IModifiableState;
import org.integratedmodelling.thinklab.modelling.lang.Context;
import org.integratedmodelling.thinklab.modelling.lang.Model;
import org.integratedmodelling.thinklab.modelling.lang.Observer;
import org.integratedmodelling.thinklab.query.Queries;
import org.integratedmodelling.utils.graph.GraphViz;
import org.integratedmodelling.utils.graph.GraphViz.NodePropertiesProvider;
import org.jgrapht.DirectedGraph;
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
	IContext   _context;
	
	class ProbingAccessor implements ISerialAccessor {

		CElem _node;
		
		public ProbingAccessor(CElem node) {
			_node = node;
		}
		
		@Override
		public IConcept getStateType() {
			return _node.accessor.getStateType();
		}

		@Override
		public Object getValue(int index) {
			Object ret = ((ISerialAccessor)(_node.accessor)).getValue(index);
			if (_node.state != null) 
				((IModifiableState)(_node.state)).setValue(index, ret);
			return ret;
		}

		public void computeState(int index) {
			/*
			 *  just compute the value and not return it. Not necessary,
			 *  but I hate to call getValue() without assigning it.
			 */
			Object ret = ((ISerialAccessor)(_node.accessor)).getValue(index);
			if (_node.state != null) 
				((IModifiableState)(_node.state)).setValue(index, ret);			
		}
		
	}
	
	
	/*
	 * compilation element - the accessor graph is made of these.
	 * TODO we need a strategy for multiple observables - must notify
	 * them all to the accessor, then have a way to obtain them - a 
	 * getValue(observable, index)?
	 */
	class CElem extends HashableObject {
		
		public CElem(IAccessor accessor, IContext context, IModel model) {
			this.accessor = accessor;
			this.model = model;
			this.context = context;
		}

		/*
		 * may be null. For semantics and logging only.
		 */
		IModel model;
		
		/*
		 * not-null only when model isn't null and it has an observer.
		 * We put the accessor's output in here.
		 */
		IState state;
		
		/*
		 * always not null.
		 */
		IAccessor accessor;
		
		/*
		 * CONTEXT - may be different from node to node, either because only
		 * initializers are needed, or because a context transformer has been 
		 * processed.
		 */
		IContext context;
		
	}
	
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
		
		public DependencyEdge(boolean b, String formalName) {
			isMediation = b;
			this.formalName = formalName;
		}
		private static final long serialVersionUID = 2366743581134478147L;
		boolean isMediation = false;
		String formalName = null;

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
		_context = context;
		_modelstruc = null;
		
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

		// TODO REMOVE - debug
		dumpGraph(_modelstruc);

		return true;
	}

	
	public IObservation run() throws ThinklabException {

		DefaultDirectedGraph<CElem, DependencyEdge> accessorGraph = 
				buildAccessorGraph(_root, _modelstruc, _context);
		
		/*
		 * TODO remove --debug
		 */
		dumpGraph(accessorGraph);
		
		computeModel(accessorGraph);

		/*
		 * find state to return as root, or build a new observation in this context if observables don't have
		 * states associated
		 */
		
		return null;
	}
	
	/**
	 * This one only computes a graph of serial accessors, as the parallel and transforming ones invoke
	 * this recursively. So it doesn't have to worry about that at all.
	 * 
	 * @param cel
	 * @param accessorGraph
	 * @throws ThinklabException
	 */
	private void computeModel(DefaultDirectedGraph<CElem, DependencyEdge> graph) throws ThinklabException {

		/*
		 * TODO topological sorting of accessors, then loop
		 */
		
		/*
		 * first pass: notify all accessors of their dependencies and mediations, create
		 * all states, add needed context mappers to edges. 
//		 */
//		initializeAccessors(cel, graph);
//		
//		/*
//		 * main compute cycle
//		 */
//		ProbingAccessor main = new ProbingAccessor(cel);
//		for (int i = 0; i < cel.context.getMultiplicity(); i++) {
//			
//			/*
//			 * TODO honor listeners in context, stop if requested etc.
//			 */
//			
//			main.computeState(i);
//		}
//		
//		/*
//		 * compile dataset into context
//		 */
//		collectStates(cel, graph);
//		
	}

	/**
	 * Each accessor is wrapped in a probing other that sets the data in the state and returns it
	 * appropriately. The probe is passed to the upstream accessor instead of the original one.
	 * 
	 * @param cel
	 * @param graph
	 * @throws ThinklabException
	 */
	private void initializeAccessors(CElem cel, DefaultDirectedGraph<CElem, DependencyEdge> graph) 
			throws ThinklabException {

		for (DependencyEdge edge : graph.outgoingEdgesOf(cel)) {
			
			CElem target = graph.getEdgeTarget(edge);
			
			initializeAccessors(target, graph);
			
			if (edge.isMediation)
				((IMediatingAccessor)(cel.accessor)).addMediatedAccessor(new ProbingAccessor(target));
			else if (cel.accessor instanceof IComputingAccessor)
				((IComputingAccessor)(cel.accessor)).notifyDependency(edge.formalName, new ProbingAccessor(target));
			else
				throw new ThinklabValidationException("non-computing observer being given unexpected dependencies");
		}
		
		if (cel.model != null) {
			/*
			 * TODO which observable? If this is only for serial accessors, we should
			 * have only one state per node. But that's NOT TRUE - needs work.
			 */
			cel.state = cel.model.getObserver().createState(null, cel.context);
		}
	}

	private void collectStates(CElem cel,
			DefaultDirectedGraph<CElem, DependencyEdge> graph) {
		
		for (DependencyEdge edge : graph.outgoingEdgesOf(cel)) {
			collectStates(graph.getEdgeTarget(edge), graph);
		}
		
		if (cel.state != null)
			((Context)(cel.context)).addStateUnchecked(cel.state);
		}


	private Collection<CElem> getRoots(
			DefaultDirectedGraph<CElem, DependencyEdge> accessorGraph) {
		
		ArrayList<CElem> ret = new ArrayList<ModelResolver.CElem>();
		for (CElem r : accessorGraph.vertexSet()) {
			if (accessorGraph.incomingEdgesOf(r).isEmpty()) {
				ret.add(r);
			}
		}
		return ret;
	}


	/**
	 * Call after resolve() to retrieve the model graph for analysis or visualization.
	 * 
	 * @return
	 */
	public DefaultDirectedGraph<IModel, DependencyEdge> getModelGraph() {
		return _modelstruc;
	}
	
	/**
	 * basically it's one accessor per model following the dependency
	 * structure; observer mediation will chain
	 * other accessors without states.
	 * 
	 * All accessors get notified of the dependencies and their formal
	 * names.
	 * 
	 * @return
	 * @throws ThinklabException 
	 */
	public DefaultDirectedGraph<CElem, DependencyEdge> buildAccessorGraph(IModel root, DefaultDirectedGraph<IModel, 
			DependencyEdge> modelGraph, IContext context) throws ThinklabException {

		DefaultDirectedGraph<CElem, DependencyEdge> graph =
				new DefaultDirectedGraph<CElem, DependencyEdge>(DependencyEdge.class);

		/*
		 * build the accessor graph. It may contain more than one disconnected graphs if the top model
		 * is an identification with dependencies but no observer, so we don't store the top node.
		 */
		buildAccessorGraphInternal(root, graph, modelGraph, context);
		
		return graph;
	}
	
	
	/**
	 * TODO to deal with parallel and transforming accessors: 
	 * 
	 * 	keep a hash of observable sig -> state
	 *  when parallel accessor encountered, run it and put its process() result in hash
	 *  else at each dependency, look first if it's in the hash and use that state as accessor if so.
	 *  IN order to do so, the observable of the target must be in the edge
	 *  
	 *  If context is transformed, get the context from the accessor and this must become the new
	 *  context - to be floated up to return value. CElem must also contain the context for the final 
	 *  node.
	 *  
	 * @param model
	 * @param graph
	 * @param modelGraph
	 * @param context
	 * @return
	 * @throws ThinklabException
	 */
	private CElem buildAccessorGraphInternal(
			IModel model, DefaultDirectedGraph<CElem, DependencyEdge> graph, DefaultDirectedGraph<IModel, 
			DependencyEdge> modelGraph, IContext context) throws ThinklabException {
		
		CElem node = null;
		
		if (model.getDatasource() != null) {
			
			/*
			 * get the accessor from the DS and chain it to ours
			 */
			node = new CElem(model.getObserver().getAccessor(), context, null);

			if ( !(node.accessor instanceof IMediatingAccessor))
				throw new ThinklabValidationException("trying to mediate to a non-mediating observer");
			
			CElem target = new CElem(model.getDatasource().getAccessor(context), context, model);
			graph.addVertex(node);
			graph.addVertex(target);
			graph.addEdge(node, target, new DependencyEdge(true, null));
			
		} else if (model.getObserver() != null) {
			
			IAccessor accessor = model.getObserver().getAccessor();
			if (accessor != null) {
				node = new CElem(accessor, context, model);
				graph.addVertex(node);
			}
		}
		
		for (DependencyEdge edge : modelGraph.outgoingEdgesOf(model)) {
			
			if (edge.isMediation) {
				
				/*
				 * if we get here, the node must have had an observer, so node can't be null.
				 */
				
				/*
				 * NOTE: there may be more dep edges to the same model - which must
				 * result in ONE accessor being created but repeated in each CElem,
				 * and the edge must carry the observable - to later be called as
				 * getValue(observable, index). Mediated nodes should be invoked as
				 * getMediatedValue(index).
				 */
				
				/*
				 * get the accessor chain for the final observable
				 */
				CElem target = 
						buildAccessorGraphInternal(
								modelGraph.getEdgeTarget(edge), graph, modelGraph, context);
				
				/*
				 * loop along the chain of mediation until we find the final
				 * observable; then get the model for it, get its CElem and
				 * tie the last mediator to it.
				 */				
				CElem start = node;
				IObserver obs = model.getObserver();
				
				while (obs.getMediatedObserver() != null) {
					CElem targ = new CElem(obs.getMediatedObserver().getAccessor(), context, null);
					graph.addVertex(targ);
					graph.addEdge(start, targ, new DependencyEdge(true, null));
					obs = obs.getMediatedObserver();
					start = targ;
				}
				
				graph.addEdge(start, target, new DependencyEdge(true, null));
				
			} else {

				/*
				 * create accessor and notify dependency
				 */
				CElem target = buildAccessorGraphInternal(modelGraph.getEdgeTarget(edge), graph, modelGraph, context);
				if (node != null) {
					graph.addEdge(node, target, new DependencyEdge(false, edge.formalName));
				}
			}
		}
				
		return node;
	}

	private DefaultDirectedGraph<IModel, DependencyEdge> buildModelGraph() {

		DefaultDirectedGraph<IModel, DependencyEdge> graph =
				new DefaultDirectedGraph<IModel, DependencyEdge>(DependencyEdge.class);

		buildModelGraphInternal(_modHash.get(_rootObservable.getSignature()), _modHash.values(), graph);
		
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
			for (Triple<Object, String, Boolean> m : model.getDependencies()) {
				boolean opt = m.getThird();
				IModel resolved = resolveInternal((ISemanticObject<?>)(m.getFirst()), context, opt || isOptional);
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
				IModel resolved = resolveInternal((ISemanticObject<?>)(m.getFirst()), context, opt || isOptional);
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
				public IAccessor getAccessor() {
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
			buildModelGraphInternal(mm, models, graph);

		/*
		 * check that resulting graph is acyclic
		 */
		CycleDetector<IModel, DependencyEdge> cd = new CycleDetector<IModel, ModelResolver.DependencyEdge>(graph);
		
		return !cd.detectCycles();
	}

	private void buildModelGraphInternal(IModel model, Collection<IModel> models,
			DefaultDirectedGraph<IModel, DependencyEdge> graph) {
		
		graph.addVertex(model);
		
		for (Triple<Object, String, Boolean> m : model.getDependencies()) {

			for (ISemanticObject<?> obs : getObservables(m.getFirst())) {
				IModel dep = _modHash.get(((SemanticObject<?>)obs).getSignature());
				if (dep != null) {
					buildModelGraphInternal(dep, models, graph);
					graph.addEdge(model, dep, new DependencyEdge(false, m.getSecond()));
				}
			}
		}
		
		IObserver observer = model.getObserver();
		for (Triple<Object, String, Boolean> m : observer.getDependencies()) {
			for (ISemanticObject<?> obs : getObservables(m.getFirst())) {
				IModel dep = _modHash.get(((SemanticObject<?>)obs).getSignature());
				if (dep != null) {
					buildModelGraphInternal(dep, models, graph);
					graph.addEdge(model, dep, new DependencyEdge(false, m.getSecond()));
				}
			}
		}
		
		if (model.getDatasource() == null) {		
			ISemanticObject<?> obs = ((Observer<?>)observer).getFinalObservable();
			IModel dep = _modHash.get(((SemanticObject<?>)obs).getSignature());
			if (dep != null) {
				buildModelGraphInternal(dep, models, graph);
				graph.addEdge(model, dep, new DependencyEdge(true, null));
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


	private void dumpGraph(DirectedGraph<?, ?> graph) throws ThinklabResourceNotFoundException {
		
		GraphViz ziz = new GraphViz();
		ziz.loadGraph(graph, new NodePropertiesProvider() {
			
			@Override
			public int getNodeWidth(Object o) {
				return 40;
			}
			
			@Override
			public String getNodeId(Object o) {
				
				String id = "?";
				
				if (o instanceof IModel) {
				
					id = ((IModel)o).getId();
					if (ModelManager.isGeneratedId(id))
						id = "[" + ((IModel)o).getObservables().get(0).getDirectType() + "]";
				} else if (o instanceof IAccessor) {
					id = "Accessor (?)";
				} else if (o instanceof CElem) {
					id = 
						((CElem)o).accessor.toString() +
						(((CElem)o).model == null ? "" : " " + (((CElem)o).model.getObservables().get(0).getDirectType())) +
						" (" + ((CElem)o).hashCode() +
						")";
				}
				
				return id;
			}
			
			@Override
			public int getNodeHeight(Object o) {
				return 20;
			}

			@Override
			public String getNodeShape(Object o) {

				if (o instanceof IAccessor) {
					return HEXAGON;
				} else if (o instanceof CElem) {
					return ((CElem)o).accessor instanceof ISerialAccessor ? ELLIPSE : HEXAGON;
				} else if (o instanceof IModel) {
					return BOX3D;
				} 
				return BOX;
			}
			
		}, true);
		
		System.out.println(ziz.getDotSource());
	}
}
