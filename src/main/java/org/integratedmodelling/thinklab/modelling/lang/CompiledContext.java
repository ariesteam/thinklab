package org.integratedmodelling.thinklab.modelling.lang;

import java.util.HashMap;
import java.util.List;

import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * The class that does the actual work in contextualization.
 * 
 * @author Ferd
 *
 */
public class CompiledContext extends Context {

	IAccessor _root = null;
	
	/*
	 * the model graph, containing only the resolved models and not the
	 * unresolved optional ones. Created at compile() time and used to
	 * resolve any circular dependencies. Creating this one should be
	 * relatively quick although it does query the kbox for unresolved
	 * observables.
	 */
	DefaultDirectedGraph<IModel, DefaultEdge> _model = null;

	/*
	 * the accessor graph that is created based on the model graph, used
	 * to compute the context at run(). When this is created, actual
	 * operations get done - e.g. contextualization of datasources that
	 * may imply significant work.
	 */
	DefaultDirectedGraph<IAccessor, DefaultEdge> _graph = null;

	/*
	 * the map linking each unresolved model with the results of
	 * the query that resolves it. If compile() succeeds, all the
	 * models in the _model graph that are not resolved will have
	 * a corresponding list of matching models. Used to build the 
	 * full closure of possible observations.
	 */
	HashMap<IModelObject, List<IModel>> _resolved = null;
	
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
		// TODO Auto-generated method stub
		
	}

}
