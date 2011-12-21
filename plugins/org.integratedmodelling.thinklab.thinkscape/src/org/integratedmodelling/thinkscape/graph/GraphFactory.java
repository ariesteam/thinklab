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
package org.integratedmodelling.thinkscape.graph;

import org.integratedmodelling.ograph.OGraph;
import org.integratedmodelling.ograph.ONode;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinkscape.Options;

public class GraphFactory {
	

	private String lang=null;
	private NodeFactory nodeFactory= null;
	
	public GraphFactory(OGraph g, String lang){
		this.lang=lang;
		nodeFactory = new NodeFactory(g,lang);
	}
	
	public GraphFactory(OGraph g){
		this.lang=Options.lang;
		nodeFactory = new NodeFactory(g,lang);
	}
	
//	 build instance of the object including pts property value pairs
	public ONode  buildInstance(IInstance instance){

		return nodeFactory.getInstanceNode(instance );		 
	}
	
	public void deleteInstance(IInstance inst){
		;
	}

}
