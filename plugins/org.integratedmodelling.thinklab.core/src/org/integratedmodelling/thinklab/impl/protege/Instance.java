/**
 * Instance.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.impl.protege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedSemanticTypeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.IAlgorithmInterpreter;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IConformance;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.IKnowledge;
import org.integratedmodelling.thinklab.interfaces.IOntology;
import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.integratedmodelling.thinklab.interfaces.IRelationship;
import org.integratedmodelling.thinklab.interfaces.IResource;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.interpreter.AlgorithmInterpreterFactory;
import org.integratedmodelling.thinklab.value.AlgorithmValue;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;

import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;

public class Instance implements IInstance {

	RDFIndividual instance;

	/**
	 * Default constructor
	 * @param instance
	 */
	public Instance(OWLIndividual instance) {
		if (instance instanceof DefaultOWLIndividual) {
			this.instance = (DefaultOWLIndividual) instance;
		} else
			throw new IllegalArgumentException("Not a proper individual");
	}

	/**
	 * Default constructor
	 * @param instance
	 */
	public Instance(RDFIndividual instance) {
		this.instance = instance;
	}

	public IConcept getType() {
		return getDirectType();
	}
	
	public IConcept getDirectType() {
		return new Concept(instance.getRDFType());
	}

	
	public boolean is(String conceptType) {
		IConcept c = null;
		try {
			c = KnowledgeManager.get().retrieveConcept(conceptType);
		} catch (ThinklabException e) {
			return false;
		}
		return this.is(c);	
	}

	public boolean is(IKnowledge concept) {
		// The IKnowledge should be a Concept as well (as a Concept cannot be an Instance)
		// and an instance cannot be another one.
		if ((concept instanceof IConcept)) {
			// this is the correct way, but it may become way too expensive as it's needed often. Plus, it
			// does not seem to work if instances are in other ontologies.
			//return(((Concept)concept).getAllInstances().contains(this.instance));
			return getDirectType().is(concept);
		} 
		return false;
	}
	
	/* We need to override the @equals()@ method for using properly  
	 * @java.util.Collection@, @java.util.Set@, etc
	 * (non-Javadoc)
	 * @see java.lang.Object#equals()
	 */
	public boolean equals(Object obj){
		if (obj instanceof Instance) {
			Instance that = (Instance) obj;
			return this.instance.equals(that.instance);
		}
		return false;
	}
	
	/* We need to override the @hashCode()@ method for using properly  
	 * @java.util.Collection@, @java.util.Set@, etc
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		int result; 
		result = instance.hashCode(); 
		return result; 
	}
	
	public boolean equals(String s) {
		return getURI().equals(s);
	}

	public boolean equals(SemanticType s) {
		return getSemanticType().equals(s);
	}

	public boolean equals(IResource r) {
		return r.getURI().equals(getURI());
	}
	
	public String toString() {
		return getSemanticType().toString();
	}
	
	public SemanticType getSemanticType() {
		try {
			return new SemanticType(getConceptSpace() + ":" + getLocalName());
		} catch (ThinklabMalformedSemanticTypeException e) {
			// no way
		}
		return null;
	}
	
	public void addDescription(String desc) {
		instance.addComment(desc);
	}

	public void addDescription(String desc, String language) {
		// TODO Protege does not support languages in comments
		instance.addComment(desc);
	}

	public void addLabel(String desc) {
		instance.addLabel(desc, null);
	}

	public void addLabel(String desc, String language) {
		instance.addLabel(desc, language);
	}



	public String getConceptSpace() {
		return this.instance.getNamespacePrefix();
	}

	public String getLabel() {

		return ThinklabOWLManager.getLabel(instance, null);	
	}

	public String getDescription() {

		return ThinklabOWLManager.getComment(instance, null);	
	}

	public String getLabel(String languageCode) {

		return ThinklabOWLManager.getLabel(instance, languageCode);
	}

	public String getDescription(String languageCode) {

		return ThinklabOWLManager.getComment(instance, null);
	}


	public String getURI() {
		return instance.getURI();
	}

	public String getLocalName() {
		return instance.getLocalName();
	}

	public void addClassificationRelationship(IProperty p, IConcept cls)
	throws ThinklabException {

		if (!p.isClassification()) {
			throw new ThinklabValidationException("property " + p.toString()
					+ " does not admit class value " + cls.toString());
		}

		Instance toadd = ThinklabOWLManager.get().getClassLiteralInstance(cls);		
		instance.addPropertyValue(((Property) p).property, toadd.instance);
	}

	public void addLiteralRelationship(IProperty p, Object literal)
	throws ThinklabException {

		if (literal instanceof IValue) {

			Instance toadd = 
				ThinklabOWLManager.get().getExtendedLiteralInstance(
						null, 
						(IValue)literal,
						KnowledgeManager.get().getKnowledgeRepository().requireOntology(getConceptSpace()));

			if (((Property)p).property instanceof OWLDatatypeProperty) {
				instance.addPropertyValue(((Property)p).property, 
						ThinklabOWLManager.get().translateIValueToDatatype((IValue)literal));
				
			} else {
				instance.addPropertyValue(((Property)p).property, toadd.instance);
			}
		} else {
			instance.addPropertyValue(((Property)p).property, literal);
		}		
	}

	public void addObjectRelationship(IProperty p, IInstance object)
	throws ThinklabException {

		if (!p.isObjectProperty()) {
			throw new ThinklabValidationException("property " + p.toString()
					+ " does not admit object value " + object.toString());
		}
		if (!object.isValidated()) {
			throw new ThinklabValidationException("can't set property "
					+ p.toString() + " to  non-validated object "
					+ object.toString());
		}

		instance.addPropertyValue(((Property) p).property,
				((Instance) object).instance);
	}



	public IInstanceImplementation getImplementation() throws ThinklabException {
		return ThinklabOWLManager.get().getInstanceImplementation(this);
	}

	public boolean isValidated() {
		// TODO Auto-generated method stub
		return true;
	}

	private Polylist convertToList(HashMap<String, String> references, String name) throws ThinklabException {

		String iname = name == null ? getLocalName() : name;
		
		if (references.containsKey(iname)) {
			return Polylist.list("#" + iname);
		}
		references.put(iname, getURI());
		
		ArrayList<Object> alist = new ArrayList<Object>();
		alist.add(getDirectType().toString() + "#" + iname);

		String comment = getDescription();
		String label = getLabel();

		if (comment != null && !comment.equals(""))
			alist.add(Polylist.list("rdfs:comment", comment));

		if (label != null && !label.equals(""))
			alist.add(Polylist.list("rdfs:label", label));

		for (IRelationship r : getRelationships()) {
			if (!KnowledgeManager.get().isPropertyBlacklisted(r.getProperty().toString()))
				alist.add(((Relationship)r).asList(references));
		}		
		
		return Polylist.PolylistFromArray(alist.toArray());

	}

	public Polylist toList(String name) throws ThinklabException {
		return toListInternal(name, null);
	}

	public Polylist toListInternal(String name, HashMap<String, String> refs) throws ThinklabException {

		if (refs == null)
			refs = new HashMap<String, String>();

		return convertToList(refs, name);
	}


	/*
	 * This one creates all implementation and collects them
	 */
	protected void validateInternal(
			ArrayList<Pair<IInstance, IInstanceImplementation>> implementations,
			HashSet<String> refs) 
	throws ThinklabException {

		if (refs == null)
			refs = new HashSet<String>();
		
		for (IRelationship p : getRelationships()) {
			
			if (p.isObject()) {
				
				Instance inst = (Instance)p.getValue().asObjectReference().getObject();
				if (!refs.contains(inst.getURI())) {
					refs.add(inst.getURI());
					inst.validateInternal(implementations, refs);
				}
			}
		}

		IInstanceImplementation impl = getImplementation();
		if (impl != null) {
			implementations.add(new Pair(this, impl));
		}
	}

	/**
	 * Validation must be called from the application that creates the instance. It triggers creation and 
	 * validation of all implementation, following object relationships in connected objects.
	 */
	public void validate() throws ThinklabException {

		ArrayList< Pair<IInstance, IInstanceImplementation>> implementations = 
			new ArrayList<Pair<IInstance,IInstanceImplementation>>();

		validateInternal(implementations, null);

		/* 
		 * validate all implementations in order of collection. This way it goes depth-first - check... 
		 */
		for (Pair<IInstance,IInstanceImplementation> impl : implementations) {
			impl.getSecond().validate(impl.getFirst());
		}

	}

	public void validate(boolean validateOWL) throws ThinklabException {

		if (validateOWL) {
			/* FIXME use some motherfucking nondeprecated function. */
			if (!((OWLIndividual)instance).isValid())
				/* FIXME better error message */
				throw new ThinklabValidationException("instance " + getLocalName() + " does not validate");
		}
		validate();
	}

	@SuppressWarnings("unchecked")
	public Collection<IRelationship> getRelationships() throws ThinklabException {

		ArrayList<IRelationship> ret = new ArrayList<IRelationship>();

		Collection<RDFProperty> props = instance.getRDFProperties();

		for (RDFProperty p : props) {
			
			String cspace = new Property(p).getConceptSpace();
			
			// FIXME won't hurt, but shouldn't be necessary - this also gets stuff like rdfs:type
			// FIXME for now just filter out stuff we don't want to store - 
			if (cspace != null && !cspace.equals("rdfs") && !cspace.equals("owl")) {
				Collection<IValue> rrel = ThinklabOWLManager.get().translateRelationship(instance, p);
 			
				for (IValue v : rrel) {
					ret.add(new Relationship(new Property(p),v));
				}
			}
		}

		return ret;
	}

	public Collection<IRelationship> getRelationships(String property)
	throws ThinklabException {

		ArrayList<IRelationship> ret = new ArrayList<IRelationship>();

		IProperty p = KnowledgeManager.get().requireProperty(property);

		Collection<IValue> rrel = 
			ThinklabOWLManager.get().translateRelationship(instance, ((Property)p).property);

		for (IValue v : rrel) {
			ret.add(new Relationship(p,v));
		}

		return ret;
	}

	public IValue get(String property) throws ThinklabException {

		Collection<IRelationship> cr = getRelationshipsTransitive(property);

		if (cr.size() == 1)
			return cr.iterator().next().getValue();

		/* TODO return a ListValue if more than one result */
		return null;
	}

	public int getNumberOfRelationships(String property) throws ThinklabException {
		IProperty p = KnowledgeManager.get().requireProperty(property);
		return ThinklabOWLManager.getNRelationships(instance, ((Property)p).property);
	}

	public Collection<IRelationship> getRelationshipsTransitive(String property)
	throws ThinklabException {

		ArrayList<IRelationship> ret = new ArrayList<IRelationship>();
		IProperty p = KnowledgeManager.get().requireProperty(property);

		Collection<IProperty> pp = p.getAllChildren();
		pp.add(p);

		for (IProperty prop : pp) {
			Collection<IValue> rrel = 
				ThinklabOWLManager.get().translateRelationship(instance, ((Property)prop).property);

			for (IValue v : rrel) {
				ret.add(new Relationship(prop,v));
			}
		}
		return ret;	
	}

	
	public IValue execute(AlgorithmValue algorithm, ISession session) throws ThinklabException {

		/* retrieve an interpreter */
		IAlgorithmInterpreter interpreter =
			AlgorithmInterpreterFactory.get().getInterpreter(algorithm, session);

		/* obtain a context; the session should be bound to it, but that depends on
		 * the language. */
		IAlgorithmInterpreter.IContext context = 
			AlgorithmInterpreterFactory.get().getContext(algorithm, session);

		/* bind self as "self" */
		context.bind(this, "self");

		return interpreter.execute(algorithm, context);
	}

	public IValue execute(AlgorithmValue algorithm, ISession session, Map<String, IValue> arguments) throws ThinklabException {

		/* retrieve an interpreter */
		IAlgorithmInterpreter interpreter =
			AlgorithmInterpreterFactory.get().getInterpreter(algorithm, session);

		/* obtain a context; the session should be bound to it, but that depends on
		 * the language. */
		IAlgorithmInterpreter.IContext context = 
			AlgorithmInterpreterFactory.get().getContext(algorithm, session);

		/* bind self as "self" */
		context.bind(this, "self");

		/* bind arguments */
		for (String key : arguments.keySet()) {
			context.bind(arguments.get(key), key);
		}

		return interpreter.execute(algorithm, context);
	}

	public IInstance clone(IOntology ontology) throws ThinklabException {
		Polylist list = this.toList(getLocalName());
		return ontology.createInstance(list);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.interfaces.IInstance#isConformant(org.integratedmodelling.ima.core.interfaces.IInstance, org.integratedmodelling.ima.core.interfaces.IConformance)
	 */
	public boolean isConformant(IInstance otherInstance, IConformance conformance) throws ThinklabException {
		conformance.setTo(this);
		return conformance.getConstraint().match(otherInstance);
	}

	public Collection<IInstance> getEquivalentInstances() {

		ArrayList<IInstance> ret = new ArrayList<IInstance>();
			
		for (Object rr : instance.getSameAs()) {
			if (rr instanceof OWLIndividual)
				ret.add(new Instance((OWLIndividual)rr));
		}
		
		return ret;
	
	}

	public void addClassificationRelationship(String p, IConcept cls) throws ThinklabException {
		addClassificationRelationship(KnowledgeManager.get().requireProperty(p), cls);
	}

	public void addLiteralRelationship(String p, Object literal) throws ThinklabException {
		addLiteralRelationship(KnowledgeManager.get().requireProperty(p), literal);		
	}

	public void addObjectRelationship(String p, IInstance instance) throws ThinklabException {
		addObjectRelationship(KnowledgeManager.get().requireProperty(p), instance);		
	}

	public Polylist toList(String oref, HashMap<String, String> refTable)
			throws ThinklabException {
		return convertToList(refTable, oref);
	}

	public void setImplementation(IInstanceImplementation impl) throws ThinklabException {
		ThinklabOWLManager.get().setInstanceImplementation(this, impl);
	}






}
