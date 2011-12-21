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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.constraint.DefaultConformance;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IConformance;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;

/**
 * @author Ioannis N. Athanasiadis
 * @author Ferdinando Villa
 */
public class Instance extends Knowledge implements IInstance {

	boolean _initialized = false;
	
	/**
	 * @param i
	 * @param ontology 
	 * @throws ThinklabResourceNotFoundException 
	 */
	public Instance(OWLIndividual i) {
		super(i,OWLType.INDIVIDUAL);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#addClassificationRelationship(org.integratedmodelling.thinklab.interfaces.IProperty, org.integratedmodelling.thinklab.interfaces.IConcept)
	 */
	public void addClassificationRelationship(IProperty p, IConcept cls)
			throws ThinklabException {

		if (!p.isClassification()) {
			throw new ThinklabValidationException("property " + p.toString()
					+ " does not admit class value " + cls.toString());
		}

		Instance toadd = ThinklabOWLManager.get().getClassLiteralInstance(cls);		
		addObjectRelationship(p,toadd);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#addLiteralRelationship(org.integratedmodelling.thinklab.interfaces.IProperty, java.lang.Object)
	 */
	public void addLiteralRelationship(IProperty p, Object literal)
			throws ThinklabException {

		if (literal instanceof IValue) {

			if (((Property)p).entity.isOWLDataProperty()) {
				OWLAPI.setOWLDataPropertyValue(
						getOWLOntology(),
						entity.asOWLIndividual(),
						((Property)p).entity.asOWLDataProperty(), 
						ThinklabOWLManager.get().translateIValueToDatatype((IValue)literal));
				
			} else {
				
				Instance toadd = 
					ThinklabOWLManager.get().getExtendedLiteralInstance(
							null, 
							(IValue)literal,
							KnowledgeManager.get().getKnowledgeRepository().requireOntology(getConceptSpace()));

				OWLAPI.setOWLObjectPropertyValue(
						getOWLOntology(),
						entity.asOWLIndividual(),
						((Property)p).entity.asOWLObjectProperty(), 
						toadd.entity.asOWLIndividual());
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
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#addObjectRelationship(org.integratedmodelling.thinklab.interfaces.IProperty, org.integratedmodelling.thinklab.interfaces.IInstance)
	 */
	public void addObjectRelationship(IProperty p, IInstance object)
			throws ThinklabException {

		OWLAPI.setOWLObjectPropertyValue(
				getOWLOntology(),
				entity.asOWLIndividual(),
				((Property)p).entity.asOWLObjectProperty(), 
				((Instance)object).entity.asOWLIndividual());
		
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

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#clone(org.integratedmodelling.thinklab.interfaces.IOntology)
	 */
	public IInstance clone(IOntology ontology) throws ThinklabException {
		
		Polylist list = this.toList(getLocalName());
		return ontology.createInstance(list);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#getDirectType()
	 */
	public IConcept getDirectType() {
		// cross fingers
		return new Concept(
			this.entity.asOWLIndividual().getTypes(this.getOWLOntology()).iterator().next().asOWLClass());
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#getEquivalentInstances()
	 */
	public Collection<IInstance> getEquivalentInstances() {
		
		Set<IInstance> ret = new HashSet<IInstance>();
		
		for (OWLOntology o : FileKnowledgeRepository.get().manager.getOntologies()) {
			for (OWLIndividual ind : this.entity.asOWLIndividual().getSameIndividuals(o)) {
				ret.add(new Instance(ind));
			}
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#getImplementation()
	 */
	public IInstanceImplementation getImplementation() throws ThinklabException {
		return ThinklabOWLManager.get().getInstanceImplementation(this);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#isConformant(org.integratedmodelling.thinklab.interfaces.IInstance, org.integratedmodelling.thinklab.interfaces.IConformance)
	 */
	public boolean isConformant(IInstance otherInstance,
			IConformance conformance) throws ThinklabException {
		
		if (conformance == null)
			conformance = new DefaultConformance();
		
		return conformance.getConstraint(this).match(otherInstance);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#isValidated()
	 */
	public boolean isValidated() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#toList(java.lang.String, java.util.HashMap)
	 */
	public Polylist toList(String oref, HashMap<String, String> refTable)
			throws ThinklabException {
		return convertToList(refTable, oref);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IInstance#validate()
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
			implementations.add(new Pair<IInstance, IInstanceImplementation>(this, impl));
		}
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#get(java.lang.String)
	 */
	public IValue get(String property) throws ThinklabException {
		
		Collection<IRelationship> cr = getRelationshipsTransitive(property);

		if (cr.size() == 1)
			return cr.iterator().next().getValue();

		/* TODO return a ListValue if more than one result */
		return null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getNumberOfRelationships(java.lang.String)
	 */
	public int getNumberOfRelationships(String property)
			throws ThinklabException {

		IProperty p = KnowledgeManager.get().requireProperty(property);

		return
			ThinklabOWLManager.get().getNOfRelationships(
					getOWLOntology(),
					entity.asOWLIndividual(),
					((Property)p).entity);
		
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationships()
	 */
	public Collection<IRelationship> getRelationships()
			throws ThinklabException {
		Set<IRelationship> ret = new HashSet<IRelationship>();

		for (IProperty p : ThinklabOWLManager.get().getValuedProperties(getOWLOntology(), entity.asOWLIndividual())) {
			Collection<IValue> rrel = 
				ThinklabOWLManager.get().translateRelationship(
						getOWLOntology(),
						entity.asOWLIndividual(), 
						((Property)p).entity, null);

			for (IValue v : rrel) {
				ret.add(new Relationship(p,v));
			}
			
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationships(java.lang.String)
	 */
	public Collection<IRelationship> getRelationships(String property)
			throws ThinklabException {
		Set<IRelationship> ret = new HashSet<IRelationship>();

		IProperty p = KnowledgeManager.get().requireProperty(property);

		Collection<IValue> rrel = 
			ThinklabOWLManager.get().translateRelationship(
					getOWLOntology(),
					entity.asOWLIndividual(), 
					((Property)p).entity, null);

		for (IValue v : rrel) {
			ret.add(new Relationship(p,v));
		}

		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationshipsTransitive(java.lang.String)
	 */
	public Collection<IRelationship> getRelationshipsTransitive(String property)
			throws ThinklabException {
		Set<IRelationship> ret = new HashSet<IRelationship>();
		IProperty p = KnowledgeManager.get().requireProperty(property);

		Collection<IProperty> pp = p.getAllChildren();
		pp.add(p);

		for (IProperty prop : pp) {
			Collection<IValue> rrel = 
				ThinklabOWLManager.get().translateRelationship(
						getOWLOntology(),
						entity.asOWLIndividual(), 
						((Property)prop).entity, null);

			for (IValue v : rrel) {
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
	
	@Override
	public void setImplementation(IInstanceImplementation impl)
			throws ThinklabException {
		ThinklabOWLManager.get().setInstanceImplementation(this, impl);	
	}


	private Polylist convertToList(HashMap<String, String> references, String name) throws ThinklabException {

		String iname = name == null ? getLocalName() : name;
		
		if (references.containsKey(iname)) {
			return Polylist.list("#" + iname);
		}
		references.put(iname, getURI());
		
		ArrayList<Object> alist = new ArrayList<Object>();
		alist.add(getDirectType().toString() + 
				(NameGenerator.isGenerated(iname) ? "" : "#" + iname));

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

	@Override
	public void addObjectRelationship(IProperty p, URI externalObject)
			throws ThinklabException {
		// TODO Auto-generated method stub
		throw new ThinklabUnimplementedFeatureException("UNIMPLEMENTED: storing objects as URIs");
	}

	@Override
	public String toString() {
		try {
			return getLocalName() + ": " + toList(null);
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}
	
}
