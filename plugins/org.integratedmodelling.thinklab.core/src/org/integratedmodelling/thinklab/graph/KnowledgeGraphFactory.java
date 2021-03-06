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
package org.integratedmodelling.thinklab.graph;

import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.jgrapht.DirectedGraph;

/**
 * Extracts a subgraph from the knowledge base using different criteria.
 * 
 * @author Ferdinando
 *
 */
public class KnowledgeGraphFactory {

	/**
	 * Extract the full knowledge graph starting at owl:Thing and using all relationships.
	 * @return
	 */
	DirectedGraph<IConcept,?> extractGraph(boolean forceTree) {
		return null;
	}
	
	/**
	 * Extract the full knowledge graph starting at the named root concept.
	 * 
	 * @param root
	 * @return
	 */
	DirectedGraph<IConcept,?> extractGraph(String rootConcept, boolean forceTree) {
		return null;
	}
	
	/**
	 * Extract the knowledge graph starting at the root concept and following the given property.
	 * @param root
	 * @param property
	 * @return
	 */
	DirectedGraph<IConcept,?> extractGraph(String root, String property, boolean forceTree) {
		return null;
	}
	
	/**
	 * Extract a graph containing all root concepts that use the passed property and following it.
	 * @param property
	 * @return
	 */
	DirectedGraph<IConcept,?> extractGraph(IProperty property, boolean forceTree) {
		return null;
	}


	
}
