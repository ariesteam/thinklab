package org.integratedmodelling.thinklab.modelling;

import org.integratedmodelling.thinklab.annotation.SemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.IState;
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

	
	/**
	 * Get the best model for the semantic object passed. If the passed object
	 * is a model, just return it, so we can use it also to "resolve" when
	 * the user has specified a precise model. Otherwise we look for what
	 * observes it.
	 * 
	 * @param toResolve
	 * @return
	 */
	public IModel resolve(ISemanticObject<?> toResolve, IContext context) {
		
		if (toResolve instanceof Model) {
			return (Model)toResolve;
		}
		
		return null;
	}

	/**
	 * Collect models and dependencies from graph, coalescing dependencies for multiple
	 * observables into a graph with a unique model per node; analyze the resulting graph for cycles
	 * 
	 * @return
	 */
	public boolean isAcyclic() {
		return true;
	}
	
}
