package org.integratedmodelling.thinkscape.graph;

import org.integratedmodelling.ograph.OGraph;
import org.integratedmodelling.ograph.ONode;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
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
