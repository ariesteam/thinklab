package org.integratedmodelling.corescience.lineage;

import org.integratedmodelling.corescience.interfaces.lineage.ILineageTraceable;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;

/**
 * A graph describing the lineage (provenance) of an object. All object that implement the ILineageTraceable
 * interface are able to produce one of these and declare that they want to be part of a lineage
 * tree. Eventually the edges of the graph should store the rationale for the inheritance/production.
 * 
 * These are created using LineageFactory.getLineage(Object o). 
 * 
 * @author Ferdinando
 *
 */
public class LineageTree  extends DefaultDirectedGraph<Object, Object> {

	private static final long serialVersionUID = -3535511676629028567L;
	private ILineageTraceable root = null;
	
	/*
	 * for future use, storing time of creation, states changed, exceptions, etc.
	 */
	public static class LineageEdge extends DefaultEdge {

		private static final long serialVersionUID = 454388510426561900L;

		public LineageEdge() {
			// TODO Auto-generated constructor stub
		}
	}
	
	public LineageTree() {
		super(LineageEdge.class);
	}

	public void setRoot(ILineageTraceable root) {
		this.root = root;
	}

	public ILineageTraceable getRoot() {
		return this.root;
	}
	
	/**
	 * Find object implementing the passed class, using a breadth-first search.
	 * 
	 * @param cl
	 * @return
	 */
	public Object find(Class<?> cl) {
		Object ret = null;
		for (BreadthFirstIterator<Object, Object> it = 
			new BreadthFirstIterator<Object, Object>(this);
				it.hasNext(); ) {
			if (cl.isInstance(ret = it.next()))
				return ret;
		}
		return null;
	}

}
