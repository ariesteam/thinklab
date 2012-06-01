package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.modelling.ModelResolver;

public class CompiledContext extends Context {
	
	public CompiledContext(IContext context) {
		super((Context) context);
	}

	/**
	 * Run the n-th incarnation
	 * 
	 * @param index
	 * @return
	 * @throws ThinklabException 
	 */
	public IObservation run(IModel model) throws ThinklabException {

		compile((Model)model);
		
		/*
		 * Get the accessor graph. Recursively
		 * call run() if we have transformers or non-parallel models, 
		 * redefining the context from their results and leaving only
		 * any serial accessors. Create states for all models we
		 * are handling.
		 */
		
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
		if (resolver.resolve(model, this))
			/* TODO get accessor graph and run it */;
		
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

//	private ModelRef compileModel(Model model, boolean isOptional) throws ThinklabException {
//
//		ModelRef ret = new ModelRef();
//		
//		_modelstruc.addVertex(ret);
//		
//		ret._model = model;
//		IContext unresolved = model.getObserver().getUnresolvedContext(this);
//		
//		if (unresolved != null) {
//
//			/*
//			 * TODO lookup first in the resolved models, use that index if found.
//			 */
//			for (Triple<ISemanticObject<?>, IContext, List<ISemanticObject<?>>> r : _resolved) {
//				if (model.getObservable().is(r.getFirst()) && unresolved.hasEqualExtents(r.getSecond()) == 0) {
//					/*
//					 * TODO use it, forget the rest
//					 */
//				}
//			}
//			
//			List<ISemanticObject<?>> resolved = 
//					Thinklab.get().getLookupKboxForNamespace(model.getNamespace()).
//						query(getResolutionQuery(model, unresolved));
//			
//			if (resolved == null) {
//				
//				if (!isOptional)
//					throw new ThinklabValidationException(
//						"cannot resolve initial states for model " + model + 
//						" using kbox " + Thinklab.get().getLookupKboxForNamespace(model.getNamespace()));
//				
//				return null;
//				
//			} else {
//				/*
//				 * the index of the result to use for resolution will be in _index.
//				 */
//				_resolved.add(new Triple<ISemanticObject<?>, IContext, List<ISemanticObject<?>>>(
//						model.getObservable(),
//						unresolved,
//						resolved));
//				ret._index = _resolved.size() - 1;
//			}
//			
//			/*
//			 * lookup in catalog first, kbox next;
//			 * if fail and !isOptional, 
//			 * 	fuck
//			 */
//		}
//		
//		if (/* mediated model */ false) {
//			/*
//			 * compile mediated and chain to us
//			 * flag mediation in dependency edge
//			 */
//		}
//		
//		for (Triple<IModel, String, Boolean> d : model.getDependencies()) {
//			
//			boolean opt = d.getThird();
//			
//			ModelRef mr = compileModel((Model) d.getFirst(), opt || isOptional);
//			
//			/**
//			 * if we are optional and we cannot do without the dependency, return 
//			 * null and let the upper level deal with our absence.
//			 */
//			if (mr == null && isOptional && !opt) {
//				return null;
//			}
//			
//			/**
//			 * if we get here with mr == null, the dependency is optional so we can
//			 * safely continue.
//			 */
//			if (mr != null) {
//				
//				mr._fname = d.getSecond();
//			
//				/*
//				 * ADD mr to graph and create dependency edge
//				 */
//				_modelstruc.addVertex(mr);
//				_modelstruc.addEdge(ret, mr);
//			}
//
//		}
//		
//		return ret;
//	}

	private IQuery getResolutionQuery(Model model, IContext unresolved) {
		// TODO Auto-generated method stub
		return null;
	}

}
