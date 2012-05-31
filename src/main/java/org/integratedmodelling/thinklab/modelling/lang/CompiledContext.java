package org.integratedmodelling.thinklab.modelling.lang;

import java.util.ArrayList;
import java.util.List;

import org.integratedmodelling.collections.Triple;
import org.integratedmodelling.common.HashableObject;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.multidimensional.MultidimensionalCursor;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.modelling.ModelResolver;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * The class that does the actual work in contextualization.
 * 
 * @author Ferd
 *
 */
public class CompiledContext extends Context {

	class ModelRef extends HashableObject {
		
		IModel _model = null;
		int    _index = -1;
		IState _state = null;
		String _fname = null;
	}
	
	class DependencyEdge extends DefaultEdge {
		private static final long serialVersionUID = 2366743581134478147L;
		boolean isMediation = false;
	}
	
	/*
	 * the "mother" dependency graph, containing reference to resolved or resolvable 
	 * models. Created at compile() time and used to create actual
	 * model graphs for each possible solution.
	 * 
	 * Circular dependencies are possible but should be resolved on the actual
	 * model graphs, as each solution may bring in different dependencies. 
	 * Creating this queries the kbox for unresolved observables.
	 */
	DefaultDirectedGraph<ModelRef, DependencyEdge> _modelstruc = null;
	ModelRef _root = null;

	/*
	 * index for the model to choose in _resolved when computing the n-th 
	 * possible incarnation of this model.
	 */
	MultidimensionalCursor _cursor = null;

	/*
	 * table of resolved DB results for all references looked up. Indexed
	 * by _index in each node in _modelstruc. Contains triples of:
	 * 1. the observable resolved
	 * 2. the context required for that observable
	 * 3. the list of results that observe that observable in that context
	 * 
	 *  The result objects in (3) are always IModels; the observables (1) can be any semantic object.
	 */
	List<Triple<ISemanticObject<?>, IContext, List<ISemanticObject<?>>>> _resolved = 
			new ArrayList<Triple<ISemanticObject<?>, IContext, List<ISemanticObject<?>>>>();
	
	public CompiledContext(IContext context) {
		super((Context) context);
	}

	/**
	 * Run the n-th incarnation
	 * 
	 * @param index
	 * @return
	 */
	public IObservation run(int index) {

		
		DefaultDirectedGraph<IAccessor, DefaultEdge> graph = 
				new DefaultDirectedGraph<IAccessor, DefaultEdge>(DefaultEdge.class);

		/*
		 * create the accessor graph, retrieving resolved models from kbox
		 * and passing the accessor from their datasource to
		 * the model's accessor to resolve initial states. Recursively
		 * call run() if we have transformers or non-parallel models, 
		 * redefining the context from their results and leaving only
		 * any serial accessors. Create states for all models we
		 * are handling.
		 */
		IAccessor root = createAccessorGraph(_root, index);
		
		/*
		 * create new context with just our states and extents to pass
		 * to accessor visitor. If the context changes, the previous one
		 * will be set in its provenance records.
		 */

		/*
		 * run accessor visitor according to accessor nature, switching 
		 * contexts as appropriate if there are transformers
		 * in the tree.
		 */
		
		/*
		 * extract the observation corresponding to our observable and return it
		 */
		
		return null;
	}
	
	private IAccessor createAccessorGraph(ModelRef _root2, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Run the first possible realization, which is expected to be the "best" one if the queries
	 * are properly sorted.
	 * 
	 * @return
	 */
	public IObservation run() {
		return run(0);
	}
	
	/**
	 * Return the number of possible realizations for the compiled model. Will
	 * return 0 if the model has not been compiled (by calling compile()).
	 * 
	 * @return
	 */
	public int getRealizationCount() {
		return _cursor == null ? 0 : _cursor.getMultiplicity();
	}

	/**
	 * Create the model structure storing all models that we are capable of
	 * computing, not including those that are in the original model 
	 * specification but are not resolvable and optional.
	 * 
	 * @param model
	 * @throws ThinklabException
	 */
	public void compile(Model model) throws ThinklabException {

		ModelResolver resolver = new ModelResolver(model.getNamespace());
		resolver.buildModelGraph(model, this);
		
//		_modelstruc = 
//				new DefaultDirectedGraph<ModelRef, DependencyEdge>(DependencyEdge.class);
//		
//		/*
//		 * create the dependency graph, looking up
//		 * dependencies as necessary.
//		 */
//		_root = compileModel(model, false);
//		
//		/*
//		 * resolve or notify any circular dependencies, leaving an
//		 * acyclical accessor graph.
//		 */
//		CycleDetector<ModelRef, DependencyEdge> cd = 
//				new CycleDetector<ModelRef, DependencyEdge>(_modelstruc);
//		
//		if (cd.detectCycles()) {
//			
//		}
//		
//		/*
//		 * create cursor for alternative model structures
//		 */
//		_cursor = new MultidimensionalCursor();
//		int[] dims = new int[_resolved.size()];
//		int i = 0;
//		for (Triple<ISemanticObject<?>, IContext, List<ISemanticObject<?>>> l : _resolved) {
//			dims[i++] = l.getThird().size();
//		}
//		_cursor.defineDimensions(dims);
		
	}

	private ModelRef compileModel(Model model, boolean isOptional) throws ThinklabException {

		ModelRef ret = new ModelRef();
		
		_modelstruc.addVertex(ret);
		
		ret._model = model;
		IContext unresolved = model.getObserver().getUnresolvedContext(this);
		
		if (unresolved != null) {

			/*
			 * TODO lookup first in the resolved models, use that index if found.
			 */
			for (Triple<ISemanticObject<?>, IContext, List<ISemanticObject<?>>> r : _resolved) {
				if (model.getObservable().is(r.getFirst()) && unresolved.hasEqualExtents(r.getSecond()) == 0) {
					/*
					 * TODO use it, forget the rest
					 */
				}
			}
			
			List<ISemanticObject<?>> resolved = 
					Thinklab.get().getLookupKboxForNamespace(model.getNamespace()).
						query(getResolutionQuery(model, unresolved));
			
			if (resolved == null) {
				
				if (!isOptional)
					throw new ThinklabValidationException(
						"cannot resolve initial states for model " + model + 
						" using kbox " + Thinklab.get().getLookupKboxForNamespace(model.getNamespace()));
				
				return null;
				
			} else {
				/*
				 * the index of the result to use for resolution will be in _index.
				 */
				_resolved.add(new Triple<ISemanticObject<?>, IContext, List<ISemanticObject<?>>>(
						model.getObservable(),
						unresolved,
						resolved));
				ret._index = _resolved.size() - 1;
			}
			
			/*
			 * lookup in catalog first, kbox next;
			 * if fail and !isOptional, 
			 * 	fuck
			 */
		}
		
		if (/* mediated model */ false) {
			/*
			 * compile mediated and chain to us
			 * flag mediation in dependency edge
			 */
		}
		
		for (Triple<IModel, String, Boolean> d : model.getDependencies()) {
			
			boolean opt = d.getThird();
			
			ModelRef mr = compileModel((Model) d.getFirst(), opt || isOptional);
			
			/**
			 * if we are optional and we cannot do without the dependency, return 
			 * null and let the upper level deal with our absence.
			 */
			if (mr == null && isOptional && !opt) {
				return null;
			}
			
			/**
			 * if we get here with mr == null, the dependency is optional so we can
			 * safely continue.
			 */
			if (mr != null) {
				
				mr._fname = d.getSecond();
			
				/*
				 * ADD mr to graph and create dependency edge
				 */
				_modelstruc.addVertex(mr);
				_modelstruc.addEdge(ret, mr);
			}

		}
		
		return ret;
	}

	private IQuery getResolutionQuery(Model model, IContext unresolved) {
		// TODO Auto-generated method stub
		return null;
	}

}
