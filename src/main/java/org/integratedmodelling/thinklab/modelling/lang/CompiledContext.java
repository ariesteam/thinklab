package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
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
	DefaultDirectedGraph<IAccessor, DefaultEdge> _graph = null;
	
	public CompiledContext(IContext context) {
		super((Context) context);
	}

	public IObservation run() {
		// TODO Auto-generated method stub
		
		/*
		 * create new context with just the states and extents
		 */
		return null;
	}

	public void compile(Model model) {
		// TODO Auto-generated method stub
		
	}

}
