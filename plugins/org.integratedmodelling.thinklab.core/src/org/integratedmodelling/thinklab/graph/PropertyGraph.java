package org.integratedmodelling.thinklab.graph;

import java.util.HashSet;
import java.util.Set;

import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.integratedmodelling.thinklab.interfaces.IRelationship;

public class PropertyGraph extends KnowledgeGraph {

	Set<String> properties = new HashSet<String>();
	
	public PropertyGraph(IKnowledgeSubject root, String property) {
		
	}
	
	public PropertyGraph(IKnowledgeSubject root, String[] property) {
		
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
	protected boolean followRelationship(IKnowledgeSubject source, IRelationship relationship, IConcept target) {
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
