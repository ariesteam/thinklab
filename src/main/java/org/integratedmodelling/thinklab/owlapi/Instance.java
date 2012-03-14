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
package org.integratedmodelling.thinklab.owlapi;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.utils.NameGenerator;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;

/**
 * @author Ioannis N. Athanasiadis
 * @author Ferdinando Villa
 */
public class Instance extends Knowledge  {

	boolean _initialized = false;
	
	/*
	 * this enables us to compare instances efficiently
	 */
	String  _signature = null;
	
	/**
	 * @param i
	 * @param ontology 
	 * @throws ThinklabResourceNotFoundException 
	 */
	public Instance(OWLIndividual i) {
		super(i,OWLType.INDIVIDUAL);
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.Instance#addClassificationRelationship(org.integratedmodelling.thinklab.interfaces.IProperty, org.integratedmodelling.thinklab.interfaces.IConcept)
	 */
	public void addClassificationRelationship(IProperty p, IConcept cls)
			throws ThinklabException {

		if (!p.isClassification()) {
			throw new ThinklabValidationException("property " + p.toString()
					+ " does not admit class value " + cls.toString());
		}

		OWLIndividual toadd = ThinklabOWLManager.get().getClassLiteralInstance(cls);		
		addObjectRelationship(p,toadd);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.Instance#addLiteralRelationship(org.integratedmodelling.thinklab.interfaces.IProperty, java.lang.Object)
	 */
	public void addLiteralRelationship(IProperty p, Object literal)
		throws ThinklabException {

		ISemanticObject o = Thinklab.get().annotate(literal);
		if (o != null && o.isLiteral()) {

			if (((Property)p).entity.isOWLDataProperty()) {
				OWLAPI.setOWLDataPropertyValue(
						getOWLOntology(),
						entity.asOWLIndividual(),
						((Property)p).entity.asOWLDataProperty(), 
						ThinklabOWLManager.get().getDatatype(o));
				
			} else {
				
				/*
				 * FIXME should never happen or it's an error.
				 */
				throw new ThinklabInternalErrorException("can't find datatype for literal: " + o);
			}
			
		} else {
			
			OWLAPI.setOWLDataPropertyValue(
					getOWLOntology(),
					entity.asOWLIndividual(),
					((Property)p).entity.asOWLDataProperty(), 
					OWLAPI.getOWLConstant(literal));
		}		


	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.Instance#addObjectRelationship(org.integratedmodelling.thinklab.interfaces.IProperty, org.integratedmodelling.thinklab.interfaces.Instance)
	 */
	public void addObjectRelationship(IProperty p, OWLIndividual object)
			throws ThinklabException {

		OWLAPI.setOWLObjectPropertyValue(
				getOWLOntology(),
				entity.asOWLIndividual(),
				((Property)p).entity.asOWLObjectProperty(), 
				((Instance)object).entity.asOWLIndividual());
		
	}

	public void addClassificationRelationship(String p, IConcept cls) throws ThinklabException {
		addClassificationRelationship(Thinklab.p(p), cls);
	}

	public void addLiteralRelationship(String p, Object literal) throws ThinklabException {
		addLiteralRelationship(Thinklab.p(p), literal);		
	}

	public void addObjectRelationship(String p, OWLIndividual instance) throws ThinklabException {
		addObjectRelationship(Thinklab.p(p), instance);		
	}

//	/* (non-Javadoc)
//	 * @see org.integratedmodelling.thinklab.interfaces.Instance#clone(org.integratedmodelling.thinklab.interfaces.IOntology)
//	 */
//	public Instance clone(IOntology ontology) throws ThinklabException {
//		
//		IList list = this.asList(getLocalName());
//		return ontology.createInstance(list);
//	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.Instance#getDirectType()
	 */
	public IConcept getDirectType() {
		// cross fingers
		return new Concept(
			this.entity.asOWLIndividual().getTypes(this.getOWLOntology()).iterator().next().asOWLClass());
	}
	
	public ArrayList<IConcept> getParents() {

		ArrayList<IConcept> ret = new ArrayList<IConcept>();
		for (Iterator<OWLDescription> it = 
				this.entity.asOWLIndividual().getTypes(getOWLOntology()).iterator(); 
			    it.hasNext(); )	{
			ret.add(new Concept(it.next().asOWLClass()));
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.Instance#getEquivalentInstances()
	 */
	public Collection<Instance> getEquivalentInstances() {
		
		Set<Instance> ret = new HashSet<Instance>();
		
		for (OWLOntology o : KR().manager.getOntologies()) {
			for (OWLIndividual ind : this.entity.asOWLIndividual().getSameIndividuals(o)) {
				ret.add(new Instance(ind));
			}
		}
		
		return ret;
	}

//	/* (non-Javadoc)
//	 * @see org.integratedmodelling.thinklab.interfaces.Instance#isConformant(org.integratedmodelling.thinklab.interfaces.Instance, org.integratedmodelling.thinklab.interfaces.IConformance)
//	 */
//	public boolean isConformant(Instance otherInstance,
//			IConformance conformance) throws ThinklabException {
//		
//		if (conformance == null)
//			conformance = new DefaultConformance();
//		
//		return ((Constraint)(conformance.getQuery(this.conceptualize()))).match(otherInstance);
//	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.Instance#isValidated()
	 */
	public boolean isValidated() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.Instance#toList(java.lang.String, java.util.HashMap)
	 */
	public IList toList(String oref, HashMap<String, String> refTable)
			throws ThinklabException {
		return convertToList(refTable, oref);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.Instance#validate()
	 */
	public void validate() throws ThinklabException {
		
//		ArrayList< Pair<Instance, InstanceImplementation>> implementations = 
//			new ArrayList<Pair<Instance,InstanceImplementation>>();
//
//		validateInternal(implementations, null);
//
//		/* 
//		 * validate all implementations in order of collection. This way it goes depth-first - check... 
//		 */
//		for (Pair<Instance,InstanceImplementation> impl : implementations) {
//			impl.getSecond().validate(impl.getFirst());
//		}
	}
	
	/*
	 * This one creates all implementation and collects them
	 */
//	protected void validateInternal(
//			ArrayList<Pair<Instance, InstanceImplementation>> implementations,
//			HashSet<String> refs) 
//		throws ThinklabException {
//
//		if (refs == null)
//			refs = new HashSet<String>();
//		
//		for (IRelationship p : getRelationships()) {
//			
//			if (p.isObject()) {
//				
//				Instance inst = (Instance) p.getObject();
//				if (!refs.contains(inst.getURI())) {
//					refs.add(inst.getURI());
//					inst.validateInternal(implementations, refs);
//				}
//			}
//		}
//
//		InstanceImplementation impl = getImplementation();
//		if (impl != null) {
//			implementations.add(new Pair<Instance, InstanceImplementation>(this, impl));
//		}
//	}

//	/* (non-Javadoc)
//	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#get(java.lang.String)
//	 */
//	public ISemanticLiteral get(String property) throws ThinklabException {
//		
//		Collection<IRelationship> cr = getRelationshipsTransitive(property);
//
//		if (cr.size() == 1)
//			return cr.iterator().next().getValue();
//
//		/* TODO return a ListValue if more than one result */
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getNumberOfRelationships(java.lang.String)
	 */
	public int getRelationshipsCount(String property)
			throws ThinklabException {

		IProperty p = Thinklab.p(property);

		return
			ThinklabOWLManager.get().getNOfRelationships(
					getOWLOntology(),
					entity.asOWLIndividual(),
					((Property)p).entity);
		
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationships()
	 */
	public Collection<Relationship> getRelationships()
			throws ThinklabException {
		Set<Relationship> ret = new HashSet<Relationship>();

		for (IProperty p : ThinklabOWLManager.get().getValuedProperties(getOWLOntology(), entity.asOWLIndividual())) {
			Collection<ISemanticObject> rrel = 
				ThinklabOWLManager.get().translateRelationship(
						getOWLOntology(),
						entity.asOWLIndividual(), 
						((Property)p).entity, null);

			for (ISemanticObject v : rrel) {
				ret.add(new Relationship(p,v));
			}
			
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationships(java.lang.String)
	 */
	public Collection<Relationship> getRelationships(String property)
			throws ThinklabException {
		Set<Relationship> ret = new HashSet<Relationship>();

		IProperty p = Thinklab.p(property);

		Collection<ISemanticObject> rrel = 
			ThinklabOWLManager.get().translateRelationship(
					getOWLOntology(),
					entity.asOWLIndividual(), 
					((Property)p).entity, null);

		for (ISemanticObject v : rrel) {
			ret.add(new Relationship(p,v));
		}

		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationshipsTransitive(java.lang.String)
	 */
	public Collection<Relationship> getRelationshipsTransitive(String property)
			throws ThinklabException {
		Set<Relationship> ret = new HashSet<Relationship>();
		IProperty p = Thinklab.p(property);

		Collection<IProperty> pp = p.getAllChildren();
		pp.add(p);

		for (IProperty prop : pp) {
			Collection<ISemanticObject> rrel = 
				ThinklabOWLManager.get().translateRelationship(
						getOWLOntology(),
						entity.asOWLIndividual(), 
						((Property)prop).entity, null);

			for (ISemanticObject v : rrel) {
				ret.add(new Relationship(prop,v));
			}
		}
		return ret;	

	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getType()
	 */
	public IConcept getType() {
		return getDirectType();
	}


	protected boolean is(IConcept c){
		return getDirectType().is(c);
	}
	

	private IList convertToList(HashMap<String, String> references, String name) throws ThinklabException {

		String iname = name == null ? getLocalName() : name;
		
		if (references.containsKey(iname)) {
			return PolyList.list("#" + iname);
		}
		references.put(iname, getURI());
		
		ArrayList<Object> alist = new ArrayList<Object>();
		alist.add(getDirectType().toString() + 
				(NameGenerator.isGenerated(iname) ? "" : "#" + iname));

		String comment = getDescription();
		String label = getLabel();

		if (comment != null && !comment.equals(""))
			alist.add(PolyList.list("rdfs:comment", comment));

		if (label != null && !label.equals(""))
			alist.add(PolyList.list("rdfs:label", label));

		for (Relationship r : getRelationships()) {
			alist.add(((Relationship)r).asList(references));
		}		
		
		return PolyList.fromArray(alist.toArray());

	}

	public IList asList(String name) throws ThinklabException {
		return toListInternal(name, null);
	}

	public IList toListInternal(String name, HashMap<String, String> refs) throws ThinklabException {

		if (refs == null)
			refs = new HashMap<String, String>();

		return convertToList(refs, name);
	}

	public void addObjectRelationship(IProperty p, URI externalObject)
			throws ThinklabException {
		// TODO Auto-generated method stub
		throw new ThinklabUnimplementedFeatureException("UNIMPLEMENTED: storing objects as URIs");
	}

	@Override
	public String toString() {
		
		try {
			return _signature == null ? 
					"(uninitialized: " + asList(null).toString() + ")" :
					_signature;
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		} 
	}
	
	@Override
	public boolean equals(Object o) {
		
		boolean ret = false;
		if (o instanceof Instance) {
			String s = ((Instance)o)._signature;
			if (_signature != null && s != null)
				ret = _signature.equals(s);
			else {
				throw new ThinklabRuntimeException("internal: comparing two instances before initialization");
			}
		}
		return ret;
	}
	
	@Override
	public int hashCode() {
		if (_signature == null)
			throw new ThinklabRuntimeException("internal: requesting hashcode from uninitialized instance");
		return _signature.hashCode();
	}

	public String computeSignature() throws ThinklabException {

		ArrayList<String> p = new ArrayList<String>();
		
		for (IConcept c : getParents()) {
			p.add(c.toString());
		}
		
		for (Relationship rel : getRelationships()) {
			p.add(((Relationship)rel).getSignature());
		}
		
		Collections.sort(p);

		String ret = "";
		for (String s : p)
			ret += s;
				
		return ret;
	}

	public String getSignature() {
		if (_signature == null)
			throw new ThinklabRuntimeException("internal: requesting hashcode from uninitialized instance");
		return _signature;
	}
	
	
	
}
