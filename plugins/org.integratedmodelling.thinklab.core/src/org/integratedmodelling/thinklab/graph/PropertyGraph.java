package org.integratedmodelling.thinklab.graph;

import java.util.HashSet;
import java.util.Set;

import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IKnowledge;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.IRelationship;

public class PropertyGraph extends KnowledgeGraph {

	Set<String> properties = new HashSet<String>();
	
	public PropertyGraph(IKnowledge root, String property) {
		
	}
	
	public PropertyGraph(IKnowledge root, String[] property) {
		
	}
	
	/**
	 * Property graph for all classes (except the root of all classes) and the given property
	 */
	public PropertyGraph(String property) {
		
	}

	/**
	 * Property graph for all classes (except the root of all classes) and the given properties
	 */
	public PropertyGraph(String[] property) {
		
	}
	
	@Override
	protected boolean followRelationship(IKnowledge source, IRelationship relationship, IConcept target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean followProperty(IConcept source, IConcept target,
			IProperty property) {
		// TODO Auto-generated method stub
		return false;
	}

}
