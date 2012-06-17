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
package org.integratedmodelling.thinklab;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.lang.SemanticType;
import org.integratedmodelling.list.ReferenceList;
import org.integratedmodelling.thinklab.annotation.AnnotationFactory;
import org.integratedmodelling.thinklab.api.factories.IKnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.kbox.neo4j.NeoKBox;

/**
 * Main knowledge manager functionalities. Thinklab holds one of these and proxies all knowledge
 * operations to it.
 * 
 * @author Ferd
 * 
 */
public class KnowledgeManager implements IKnowledgeManager {

	protected CommandManager _commandManager = new CommandManager();
	
	protected AnnotationFactory _annotationFactory = new AnnotationFactory();
	
	private HashMap<String, IKbox> _kboxes = new HashMap<String, IKbox>();

	protected KnowledgeManager()  {
	}

	@Override
	public ISemanticObject<?> parse(String literal, IConcept concept)
			throws ThinklabException {
		return _annotationFactory.parse(literal, concept);
	}

	@Override
	public ISemanticObject<?> annotate(Object object) throws ThinklabException {
		return _annotationFactory.annotate(object);
	}

	@Override
	public Object instantiate(IList semantics) throws ThinklabException {
		
		if ( !(semantics instanceof IReferenceList)) {
			semantics = ReferenceList.list(semantics.toArray());
		}
		return _annotationFactory.instantiate((IReferenceList) semantics);
	}

	public IReferenceList conceptualize(Object object) throws ThinklabException {
		return _annotationFactory.conceptualize(object);
	}

	@Override
	public IConcept getLeastGeneralCommonConcept(IConcept... cc) {
		return getLeastGeneralCommonConcept(Arrays.asList(cc));
	}

	@Override
	public IKbox createKbox(String uri) throws ThinklabException {

		IKbox ret = null;
		if (!uri.contains("://")) {
			File kf = new File(Thinklab.get().getScratchArea("kbox") + File.separator + uri);
			kf.mkdirs();
			try {
				ret = new NeoKBox(kf.toURI().toURL().toString());
				ret.open();
				_kboxes.put(uri, ret);
			} catch (MalformedURLException e) {
				throw new ThinklabInternalErrorException(e);
			}
		}
		
		return ret;
	}

	@Override
	public void dropKbox(String uri) throws ThinklabException {
		// TODO Auto-generated method stub
	}

	@Override
	public IKbox requireKbox(String uri) throws ThinklabException {
		
		IKbox ret = null;
		if (_kboxes.containsKey(uri))
			ret = _kboxes.get(uri);
		else
			ret = createKbox(uri);
		return ret;
	}

	@Override
	public IProperty getProperty(String prop) {
		return retrieveProperty(new SemanticType(prop));
	}

	@Override
	public IConcept getConcept(String conc) {
		return retrieveConcept(new SemanticType(conc));
	}
	
	public void shutdown() {
		
		for (IKbox kbox : _kboxes.values()) {
			
			/*
			 * TODO if kbox properties contains a temporary tag, clear it before closing.
			 */
			
			kbox.close();
		}
		
		/* TODO any other cleanup actions */
		
		_annotationFactory = null;
		_commandManager = null;
		_kboxes.clear();
	}

	public IConcept requireConcept(String id) throws ThinklabException {
    	
    	SemanticType st = new SemanticType(id);
    	return requireConcept(st);
    }
 
	public IProperty requireProperty(String id) throws ThinklabException {
 
    	SemanticType st = new SemanticType(id);
        return requireProperty(st);
    }

    /**
     * Return concept from semantic type. Concept must exist.
     * @param id the semantic type
     * @return the concept.
     * @throws ThinklabResourceNotFoundException if concept not found.
     */
    public IConcept requireConcept(SemanticType id) throws ThinklabResourceNotFoundException {
    	
        IConcept ret = retrieveConcept(id);
        
        if (ret == null) {
        	throw new ThinklabResourceNotFoundException("concept " + id + " is unknown");
        }
        return ret;
    }

    /**
     * Return property from semantic type. Concept must exist.
     * @param id the semantic type
     * @return the property.
     * @throws ThinklabResourceNotFoundException if property not found.
     */
    public IProperty requireProperty(SemanticType id) throws ThinklabResourceNotFoundException {	
    	
        IProperty ret =  retrieveProperty(id);
        
        if (ret == null) {
        	throw new ThinklabResourceNotFoundException("property " + id + " is unknown");
        }
        return ret;
    }
    
	public IConcept retrieveConcept(SemanticType t) {

		IConcept ret = null;
		INamespace ns = Thinklab.get().getNamespace(t.getConceptSpace());
	    if (ns != null)
	    	ret = ns.getConcept(t.getLocalName());
	    if (ret == null && t.toString().equals(Thinklab.get().getKnowledgeRepository().getRootConcept().toString()))	{
	    	ret = Thinklab.get().getKnowledgeRepository().getRootConcept();
	    }
	    return ret;
	}

	public IProperty retrieveProperty(SemanticType t) {
		
		IProperty ret = null;
		INamespace ns = Thinklab.get().getNamespace(t.getConceptSpace());
	    if (ns != null)
	    	ret = ns.getProperty(t.getLocalName());
	    return ret;
	}

	public IConcept getLeastGeneralCommonConcept(String semanticType, String otherConcept) throws ThinklabException {
		return getLeastGeneralCommonConcept(requireConcept(semanticType), requireConcept(otherConcept));
	}

	public IConcept getLeastGeneralCommonConcept(IConcept concept1, IConcept c) {
		return concept1.getLeastGeneralCommonConcept(c);
	}
	
	public IConcept getLeastGeneralCommonConcept(Collection<IConcept> cc) {

		IConcept ret = null;
		Iterator<IConcept> ii = cc.iterator();

		if (ii.hasNext()) {		
			
		  ret = ii.next();
		  
		  if (ret != null)
			while (ii.hasNext()) {
		      ret = ret.getLeastGeneralCommonConcept(ii.next());
		      if (ret == null)
		    	  break;
		    }
		}
		
		return ret;
	}
	
	public CommandManager getCommandManager() {
		return _commandManager;
	}

	public void registerAnnotation(Class<?> clls, String value) throws ThinklabException {
		_annotationFactory.registerAnnotationConcept(requireConcept(value), clls);
	}


	public void registerLiteralAnnotation(Class<?> clls, String concept,
			String datatype, Class<?> javaClass) throws ThinklabException {
		_annotationFactory.registerLiteralAnnotation(clls, requireConcept(concept), datatype, javaClass);
	}

	@Override
	public void registerAnnotatedClass(Class<?> cls, IConcept concept) {
		_annotationFactory.registerAnnotationConcept(concept, cls);
	}

	public boolean isJavaLiteralClass(Class<?> cls) {
		return _annotationFactory.isJavaLiteralClass(cls);
	}

	public boolean isLiteralConcept(IConcept concept) {
		return _annotationFactory.isLiteralConcept(concept);
	}

	public ISemanticObject<?> getSemanticObject(IReferenceList list, Object object) {
		return _annotationFactory.getSemanticObject(list, object);
	}

	public IConcept getLiteralConceptForJavaClass(Class<? extends Object> class1) {
		return _annotationFactory.getLiteralConceptForJavaClass(class1);
	}

	public ISemanticObject<?> getSemanticLiteral(IReferenceList semantics) {
		return _annotationFactory.getSemanticLiteral(semantics);
	}

	@Override
	public ISemanticObject<?> entify(IList semantics) throws ThinklabException {
		if ( !(semantics instanceof IReferenceList)) {
			semantics = ReferenceList.list(semantics.toArray());
		}
		return _annotationFactory.entify((IReferenceList) semantics);
	}

	@Override
	public IConcept getXSDMapping(String string) {
		return _annotationFactory.getXSDMapping(string);
	}

}
