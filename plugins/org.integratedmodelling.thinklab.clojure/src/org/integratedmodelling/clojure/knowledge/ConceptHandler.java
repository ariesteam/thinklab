package org.integratedmodelling.clojure.knowledge;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.runtime.ISession;

public class ConceptHandler {
	
	ISession _session = null;
	String _id = null;
	String _label = null;
	String _comment = null;
	IConcept _concept = null;
	
	public ConceptHandler(ISession session, String concept) throws ThinklabException {
		
		_session = session;
		_id = concept;
		
		if (SemanticType.validate(concept)) {
			_concept = KnowledgeManager.get().retrieveConcept(concept);
		}
	}
	
	/**
	 * Annotations are added in order of appearance; if there's only one it's a comment, if two they're
	 * label and comment. Any further ones are ignored.
	 * 
	 * @param annotation
	 */
	public void addAnnotation(String annotation) {
		
		if (_label == null && _comment == null)
			_comment = annotation;
		else if (_comment != null && _label == null) {
			_label = _comment;
			_comment = annotation;
		}
	}
	
	public void define(Object value) throws ThinklabException {
		
	}
	
	/*
	 * will be self if a forward reference, the finished object otherwise
	 */
	public IConcept getConcept() {
		
		if (_concept == null) {
			
		} 
		
		return _concept;
	}
}
