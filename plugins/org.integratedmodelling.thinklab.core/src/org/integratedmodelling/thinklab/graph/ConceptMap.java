package org.integratedmodelling.thinklab.graph;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.integratedmodelling.thinklab.interfaces.IRelationship;

public class ConceptMap extends KnowledgeGraph {

	private static final long serialVersionUID = -5150733549820114934L;

	public ConceptMap(IConcept root) throws ThinklabException {
		buildGraph(root);
	}

	@Override
	protected boolean followProperty(IConcept source, IConcept target,
			IProperty property) {
		// everything goes
		return true;
	}

	@Override
	protected boolean followRelationship(IKnowledgeSubject source,
			IRelationship relationship, IConcept type) {
		// TODO Auto-generated method stub
		return false;
	}


}
