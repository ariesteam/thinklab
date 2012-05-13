package org.integratedmodelling.thinklab.modelling.lang;

import java.util.ArrayList;
import java.util.List;

import org.integratedmodelling.collections.Triple;
import org.integratedmodelling.common.HashableObject;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IState;
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
	
	/*
	 * the "mother" dependency graph, containing reference to resolved or resolvable 
	 * models. Created at compile() time and used to create actual
	 * model graphs for each possible solution.
	 * 
	 * Circular dependencies are possible but should be resolved on the actual
	 * model graphs, as each solution may bring in different dependencies. 
	 * Creating this queries the kbox for unresolved observables.
	 */
	DefaultDirectedGraph<ModelRef, DefaultEdge> _modelstruc = null;
	ModelRef _root = null;
	

	/*
	 * the accessor graph that is created based on the model graph, used
	 * to compute the context at run(). When this is created, actual
	 * operations get done - e.g. contextualization of datasources that
	 * may imply significant work.
	 */
	DefaultDirectedGraph<IAccessor, DefaultEdge> _graph = null;

	/*
	 * table of resolved DB results for all references looked up. Indexed
	 * by _index in each node in _modelstruc.
	 */
	List<List<IModel>> _resolved = new ArrayList<List<IModel>>();
	
	public CompiledContext(IContext context) {
		super((Context) context);
	}

	public IObservation run() {

		/*
		 * create new context with just the states and extents
		 */
		return null;
	}

	public void compile(Model model) {

		/*
		 * create the dependency graph, looking up
		 * dependencies as necessary.
		 */
		_root = compileModel(model, false);
		
		/*
		 * create cursor for alternative model structures
		 */
		
	}

	private ModelRef compileModel(Model model, boolean isOptional) {

		ModelRef ret = new ModelRef();
		
		ret._model = model;
		
		if (/* model unresolved */ false) {
			/*
			 * lookup in catalog first, kbox next;
			 * if fail and !isOptional, 
			 * 	fuck
			 */
		}
		
		if (/* mediated model */ false) {
			/*
			 * compile mediated and chain to us
			 * flag mediation in ret (TBI)
			 */
		}
		
		for (Triple<IModel, String, Boolean> d : model.getDependencies()) {
			
			boolean opt = d.getThird();
			
			ModelRef mr = compileModel((Model) d.getFirst(), opt || isOptional);
			
			mr._fname = d.getSecond();
			
			/*
			 * ADD mr to graph and create dependency
			 */
		}
		
		return ret;
	}

}
