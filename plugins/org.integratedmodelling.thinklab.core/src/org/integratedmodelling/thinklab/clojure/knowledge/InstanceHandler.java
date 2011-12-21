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
package org.integratedmodelling.thinklab.clojure.knowledge;

import java.net.URI;
import java.net.URISyntaxException;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.lang.SemanticType;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.utils.NameGenerator;

/**
 * Interacts with a new instance on behalf of an object clojure form. Handles forward referencing in
 * kbox definition.
 * 
 * @author Ferdinando Villa
 *
 */
public class InstanceHandler {

	ISession _session = null;
	IInstance _instance = null;
	IConcept  _type = null;
	KBoxHandler _handler = null;
	String _forward = null;
	String _external = null;
	String _id = null;
	String _label = null;
	String _comment = null;
	
	public InstanceHandler(ISession session, String concept, KBoxHandler handler) throws ThinklabException {
		
		_session = session;
		_handler = handler;
		_id = NameGenerator.newName("obj_");
		
		if (concept.contains("://")) {
			/* URL of external object, will be stored as is */
			_external = concept;
			
		} else {
		
			if (SemanticType.validate(concept)) {
				_type = KnowledgeManager.get().requireConcept(concept);
					_instance = _session.createObject(_type.toString());
			} else {
				if (_handler == null)
					throw new ThinklabValidationException(
						"object: cannot define a forward reference outside of a with-kbox form");
				_forward = concept;
			}
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
	
	public void addProperty(IProperty property, Object value) throws ThinklabException {
		
		if (_forward  != null) {
			throw new ThinklabValidationException("object: cannot add properties to a forward reference");
		}
		
		if (value instanceof InstanceHandler) {
			
			InstanceHandler ih = (InstanceHandler)value;

			if (ih._external != null) {

				/*
				 * behavior differs if we're building an object or storing into a kbox
				 */
				if (_handler != null) {
					
					/*
					 * store proxy for external record in kbox
					 */
					try {
						_instance.addObjectRelationship(property, new URI(ih._external));
					} catch (URISyntaxException e) {
						throw new ThinklabValidationException(e);
					}
					
				} else {
					/*
					 * build object and link it up
					 */
					String uri = ih._external.toString();
					String[] up = uri.split("#");
					
					if (up.length != 2) {
						throw new ThinklabValidationException("parsing reference " + uri + ": invalid external object URI");
					}
					
					IKBox kbox = KBoxManager.get().requireGlobalKBox(up[0]);
					IList list = kbox.getObjectAsListFromID(up[1], null);
					IInstance linked = _session.createObject(list);  
					_instance.addObjectRelationship(property, linked);
				}
				
			} else {
				if (_handler == null)
					throw new ThinklabValidationException(
						"object: cannot handle a forward reference outside of a with-kbox form");
				/*
				 * record a forward ref to resolve later by the kbox handler
				 */
				_handler.declareForwardReference(_instance, property, ih._forward);
			}
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
		
		/*
		 * add annotations
		 */
		if (_instance != null) {
			if (_label != null)
				_instance.addLabel(_label);
			if (_comment != null)
				_instance.addDescription(_comment);
		}
		
		if (_handler != null && _instance != null) {
			_handler.registerObject(_id, _instance);
		}
		return _forward == null ? _instance : this;
	}
}
