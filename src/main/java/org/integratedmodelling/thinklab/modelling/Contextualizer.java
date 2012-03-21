package org.integratedmodelling.thinklab.modelling;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.multidimensional.MultidimensionalCursor;
import org.integratedmodelling.thinklab.api.listeners.IListener;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
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

	private IObserver _observer;
	private IContext _context;
	private MultidimensionalCursor _ticker;
	private IState[] _resolved;

	class Node {
		IAccessor accessor;
		IState state;
	}
	
	class Edge {
		String formalId;
		ContextMapper contextMapper;
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
		
		this._observer = observer;
		this._context = context;
		
		/*
		 * query all the unresolved observables that are not already in the
		 * context. 
		 */
		int nResolved = 0;
		
		/*
		 * create ticker based on results and prepare storage for the 
		 * observations we will use.
		 */
		
		_resolved = new IState[nResolved];
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
		return _ticker.getMultiplicity();
	}
	
	/*
	 * Contextualization driver for the n-th possible solution. Steps are:
	 */
	public IObservation contextualize(int resultIndex) throws ThinklabException {
		
		/*
		 * 1. create accessor for needed obs that were already in context.
		 */
		_ticker.getElementIndexes(resultIndex);
		
		/*
		 * 2. query remaining requirements and run transformations on the observations to
		 * harmonize to context.
		 */
		
		/*
		 * 3. Create remaining accessors and state storage for each needed output, plus
		 * any additional states requested by the accessor. If accessor is not serial,
		 * contextualize recursively; if accessor is a context transformer, redefine
		 * context after it's done.
		 */
		
		/*
		 * 4. build contextualization graph - put a context mapper along each dependency
		 * and establish register in memory for each accessor's state.
		 */
		
		/*
		 * 5. create contextualization topological ordering. Let listeners decide what to
		 * do if there are cycles.
		 */
		
		/*
		 * 5. context loop - for each accessor call getValue (unless context mapper says not
		 * to) and put result in accessor's memory.
		 */
		
		return null;
	}
	
	
}
