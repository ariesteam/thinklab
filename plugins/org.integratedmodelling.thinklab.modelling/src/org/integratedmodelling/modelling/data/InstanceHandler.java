package org.integratedmodelling.modelling.data;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.utils.NameGenerator;

public class InstanceHandler {

	ISession _session = null;
	IInstance _instance = null;
	IConcept  _type = null;
	KBoxHandler _handler = null;
	String _forward = null;
	String _id = null;
	
	public InstanceHandler(ISession session, String concept, KBoxHandler handler) throws ThinklabException {
		
		_session = session;
		_handler = handler;
		_id = NameGenerator.newName("obj_");
		
		if (SemanticType.validate(concept))
			_type = KnowledgeManager.get().requireConcept(concept);
		else {
			if (_handler == null)
				throw new ThinklabValidationException(
						"object: cannot define a forward reference outside of a with-kbox form");
			_forward = concept;
		}
	}
	
	public void addProperty(IProperty property, Object value) throws ThinklabException {
		
		if (_forward  != null) {
			throw new ThinklabValidationException("object: cannot add properties to a forward reference");
		}
		
		if (_instance == null) {
			_instance = _session.createObject(_type.getSemanticType());
		}
		
		if (value instanceof InstanceHandler) {
			
			if (_handler == null)
				throw new ThinklabValidationException(
						"object: cannot handle a forward reference outside of a with-kbox form");
			/*
			 * record a forward ref to resolve later
			 */
			_handler.declareForwardReference(((InstanceHandler)value)._forward, property, _id);
			
		} else if (value instanceof IConcept) {
			_instance.addClassificationRelationship(property, (IConcept)value);
		}else if (value instanceof IInstance) {
			_instance.addObjectRelationship(property, (IInstance)value);
		} else {
			_instance.addLiteralRelationship(property, value);
		}
	}
	
	/*
	 * will be self if a forward reference, the finished object otherwise
	 */
	public Object getObject() {
		
		if (_handler != null && _instance != null) {
			_handler.registerObject(_id, _instance);
		}
		return _forward == null ? _instance : this;
	}
}
