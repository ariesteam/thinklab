package org.integratedmodelling.clojure;

import java.net.URI;
import java.net.URISyntaxException;

import org.integratedmodelling.modelling.data.InstanceHandler;
import org.integratedmodelling.modelling.data.KBoxHandler;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Polylist;

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
