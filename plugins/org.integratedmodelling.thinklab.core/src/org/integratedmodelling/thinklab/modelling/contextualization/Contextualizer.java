package org.integratedmodelling.thinklab.modelling.contextualization;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.listeners.IListener;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IContextMapper;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 * Handles the contextualization of an model tree using a graph of connections between observations and
 * accessors.
 * 
 * @author Ferd
 *
 */
public class Contextualizer  {

	int _resultCount = 0;
	
	class Node {
		IAccessor accessor;
		IState state;
	}
	
	class Edge {
		String formalId;
		IContextMapper contextMapper;
	}
	
	/*
	 * create the contextualization graph for an observer.
	 */
	DefaultDirectedGraph<Node, Edge> createGraph(IObserver observer) {
		return null;
	}
	
	/*
	 * initializer. Will lookup all unresolved observations and unless the context contains
	 * usable ones, will query the knowledge base for each of them. After construction, the
	 * user can inquire how many possible results are available. If > 0, contextualize(n) can
	 * be called to produce a harmonized context with all states.
	 */
	public Contextualizer(IObserver observer, IContext context) {
		
	}
	
	/**
	 * Add any listener. What listeners do is dependent on their subclass.
	 * TODO list supported listeners. 
	 * 
	 * @param listener
	 */
	public void addListener(IListener listener) {
		
	}
	
	/**
	 * Number of possible ways to contextualize the observer in the passed context.
	 * 
	 * @return
	 */
	public int getResultCount() {
		return _resultCount;
	}
	
	/*
	 * Contextualization driver for the n-th possible solution. Steps are:
	 * 
	 * 1. collect all observations
	 */
	public IContext contextualize(int resultIndex) throws ThinklabException {
		
		return null;
	}
	
	
}
