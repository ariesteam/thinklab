package org.integratedmodelling.thinklab.modelling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.integratedmodelling.thinklab.annotation.SemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.modelling.lang.Context;
import org.integratedmodelling.thinklab.modelling.lang.Model;
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
	
	public ModelResolver(INamespace namespace) {
		_namespace = namespace;
	}
	
	class ModelRef {
		
		IModel _model = null;
		int    _index = -1;
		IState _state = null;
		String _fname = null;
		String _observableSig = null;
		
		public ModelRef(IModel model) {
			
		}
		
		/*
		 * can be compared with a semantic object (observable)
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object arg0) {

			return (arg0 instanceof ModelRef && ((ModelRef)arg0)._observableSig.equals(_observableSig))
					||
					(arg0 instanceof SemanticObject<?> && ((SemanticObject<?>)arg0).getSignature().equals(_observableSig));
		}
		@Override
		public int hashCode() {
			return _observableSig.hashCode();
		}	
	}
	
	class DependencyEdge extends DefaultEdge {
		private static final long serialVersionUID = 2366743581134478147L;
		boolean isMediation = false;
	}
	
	DefaultDirectedGraph<ModelRef, DependencyEdge> _modelstruc = null;
	ModelRef _root = null;
	private HashMap<String, IModel> _modHash;
	
	public void buildModelGraph(IModel rootModel) {
		
		_root = new ModelRef(rootModel);
		
		/*
		 * TODO
		 * recurse dependencies checking the graph and the coverage as we go
		 */
	}
	
	/**
	 * Get the best model for the semantic object passed. If the passed object
	 * is a model, just file its observables and return it. Otherwise we assume the
	 * argument is an observable and look for models that
	 * observe it. If coverage of the best model is incomplete, we may build 
	 * a model that merges more than one, in order of score, to cover the context
	 * as fully as possible.
	 * 
	 * @param toResolve
	 * @return
	 */
	public IModel resolve(ISemanticObject<?> toResolve, IContext context) {
		
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
			ret =  (Model)toResolve;
		} else {
			
			/*
			 * observable
			 */
			SemanticObject<?> observable = (SemanticObject<?>) toResolve;
			String sig = observable.getSignature();
			
			/*
			 * if we already have a strategy, use that unless it creates circular dependencies
			 * TODO log strategy
			 */
			if (_modHash.containsKey(sig) && isAcyclic(observable, _modHash.get(sig))) {
				return _modHash.get(sig);
			}
			
			IContext coverage = new Context();
			ArrayList<IModel> models = new ArrayList<IModel>();
			/*
			 * scan models in order of decreasing quality.
			 * loop until we get the best coverage; stop if/when we get to 100%
			 * TODO log strategy
			 */
			
			for (IModel m : getSuitableModels(observable, context)) {
				
				/*
				 * don't use it at all if it would create circular dependencies.
				 * TODO log strategy
				 */
				if (!isAcyclic(observable, m))
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
			 * finalize choice
			 */
			if (models.size() > 1) {
				
				/*
				 * make multiple model with all suitable alternatives
				 */
				
			} else {
				ret = models.size() == 0 ? null : models.get(0);
			}	
			
		}

		/*
		 * set model as resolvers for ALL the observables it can resolve.
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

	private List<IModel> getSuitableModels(SemanticObject<?> observable,
			IContext context) {
		ArrayList<IModel> ret = new ArrayList<IModel>();
		
		/*
		 * query all models and build score based on metadata
		 */
		
		/*
		 * must be covered at least some or not specify coverage at all (which 
		 * gives them a lower score)
		 */
		
		return ret;
	}

	/**
	 * Try out the scenario of using the given model for the passed observable. Analyze the
	 * resulting graph and return whether it creates circular dependencies.
	 * 
	 * @param iModel
	 * @param observable 
	 * 
	 * @return true if NO circular dependencies are created by using this model for the observable.
	 */
	public boolean isAcyclic(SemanticObject<?> observable, IModel iModel) {
		return true;
	}
	
}
