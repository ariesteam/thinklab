/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
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
