/**
 * Ontology.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Mar 3, 2008
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
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Mar 3, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.owlapi;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.lang.SemanticType;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IKnowledge;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Path;
import org.semanticweb.owl.io.OWLXMLOntologyFormat;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.util.OWLEntityRemover;

/**
 * @author Ioannis N. Athanasiadis, Dalle Molle Institute for Artificial Intelligence, USI/SUPSI
 * @author Ferdinando Villa
 * 
 * @since 13 sth 2006
 */
public class Ontology implements IOntology {
	
	private String conceptSpace; // i.e. owl or xml or fs
	protected FileKnowledgeRepository kr;

	private boolean acceptDuplicateIDs = false;
	protected OWLOntology ont;
	private Map<SemanticType,IConcept> concepts;
	private Map<SemanticType,IProperty> properties;
	private Map<SemanticType,IInstance> instances;
	private String cs;
	
	// set to false if the ontology is a temporary knowledge RAM for the system - e.g. 
	// sessions.
	boolean isSystem = true;

	class ReferenceRecord {
		public IInstance target;
		public String reference;
		public IProperty property;
		
		public ReferenceRecord(IInstance t, IProperty prop, String ref) {
			target = t;
			reference = ref;
			property = prop;
		}
	}
	
	private void resolveReferences (Collection<ReferenceRecord> refs) throws ThinklabException {

		for (ReferenceRecord r : refs) {
			
			IInstance rr = this.getInstance(r.reference);
			if (rr == null) {
				throw new ThinklabResourceNotFoundException("internal error: can't resolve reference to object " + r.reference);
			}
			
			r.target.addObjectRelationship(r.property, rr);
		}
	}
	
	public Ontology(OWLOntology onto, FileKnowledgeRepository kr){
		
		this.ont = onto;
		this.kr = kr;
		this.concepts = new Hashtable<SemanticType, IConcept>();
		this.properties = new Hashtable<SemanticType, IProperty>();
		this.instances = new Hashtable<SemanticType, IInstance>();
	}
	
	protected void initialize(String cs){
		
		this.cs = cs;
		
		Set<OWLClass> allCl  = ont.getReferencedClasses();
		for(OWLClass cl:allCl){
			
			if (cl.getURI().toString().startsWith(ont.getURI().toString()))
				concepts.put(kr.registry.getSemanticType(cl), new Concept(cl));
		}
		Set<OWLDataProperty> allPr = ont.getReferencedDataProperties();
		for(OWLDataProperty p:allPr){
			if (p.getURI().toString().startsWith(ont.getURI().toString()))
				properties.put(kr.registry.getSemanticType(p), new Property(p));
		}
		
		Set<OWLObjectProperty> objPr = ont.getReferencedObjectProperties();
		for(OWLObjectProperty p:objPr){
			if (p.getURI().toString().startsWith(ont.getURI().toString()))
				properties.put(kr.registry.getSemanticType(p), new Property(p));
		}
		
		Set<OWLIndividual> allIn = ont.getReferencedIndividuals();
		for(OWLIndividual i:allIn){
			if (i.getURI().toString().startsWith(ont.getURI().toString()))
				instances.put(kr.registry.getSemanticType(i), new Instance(i));
		}
	}

	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#getConceptSpace()
	 */
	public String getConceptSpace() {
		return cs;
	}
	
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#getConcept(java.lang.String)
	 */
	public IConcept getConcept(String ID) {	
		return concepts.get(new SemanticType(cs,ID));
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#getConcepts()
	 */
	public Collection<IConcept> getConcepts() {
		return concepts.values();
	}
	

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#getProperty(java.lang.String)
	 */
	public IProperty getProperty(String ID) {
		return properties.get(new SemanticType(cs, ID));
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#getProperties()
	 */
	public Collection<IProperty> getProperties() {
		return properties.values();
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#getInstance(java.lang.String)
	 */
	public IInstance getInstance(String ID) {
		return instances.get(new SemanticType(cs, ID));
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#getInstances()
	 */
	public Collection<IInstance> getInstances() throws ThinklabException {
		return instances.values();
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#allowDuplicateInstanceIDs()
	 */
	public void allowDuplicateInstanceIDs() {
		acceptDuplicateIDs = true;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#createEquivalence(org.integratedmodelling.thinklab.interfaces.IInstance, org.integratedmodelling.thinklab.interfaces.IInstance)
	 */
	public void createEquivalence(IInstance o1, IInstance o2) {

		OWLIndividual ind1 = ((Instance)o1).entity.asOWLIndividual();
		OWLIndividual ind2 = ((Instance)o2).entity.asOWLIndividual();
		
		Set<OWLIndividual> oi = new HashSet<OWLIndividual>();
		
		oi.add(ind1);
		oi.add(ind2);
		
		OWLAxiom ax = FileKnowledgeRepository.df.getOWLSameIndividualsAxiom(oi);
		AddAxiom add = new AddAxiom(ont,ax);
		
		try {
			FileKnowledgeRepository.get().manager.applyChange(add);
		} catch (OWLOntologyChangeException e) {
			throw new ThinklabRuntimeException(e);
		}
	}


	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#createInstance(java.lang.String, org.integratedmodelling.thinklab.interfaces.IConcept)
	 */
	public IInstance createInstance(String ID, IConcept c) throws ThinklabException {
		
		String label = null;
		
		if(c.isAbstract()) {
			throw new ThinklabIOException(" cannot create an instance of abstract concept " + c);
		}

		if (ID == null) {
			ID = getUniqueObjectName("ins");
		}
		
		if (getInstance(ID) != null) {
			
			if (!acceptDuplicateIDs) {
				
				throw new ThinklabValidationException("instance with name " + ID
						+ " already exists in concept space " + conceptSpace);
			}
			
			/* if we find a duplicated ID, we add progressive numbers to it, and
			 * we set the desired instance as a label, which can later be overwritten
			 * by any label the user has specified.
			 */
			int cnt = 1;
			label = ID;
			
			do {
				ID = Path.getLeading(ID, '_') + "_" + cnt++;
			} while (getInstance(ID) != null);
		}

		OWLIndividual ind = 
			FileKnowledgeRepository.df.getOWLIndividual(URI.create(ont.getURI() + "#" + ID));
		OWLAxiom ax = 
			FileKnowledgeRepository.df.getOWLClassAssertionAxiom(ind, ((Concept)c).entity.asOWLClass());
		AddAxiom add = new AddAxiom(ont,ax);
		
		try {
			FileKnowledgeRepository.get().manager.applyChange(add);
		} catch (OWLOntologyChangeException e) {
			throw new ThinklabRuntimeException(e);
		}
		
		Instance inst = null;
		
		inst = new Instance(ind);
		instances.put(new SemanticType(cs, ID), inst); 
		
		return inst;
	}


	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#createInstance(org.integratedmodelling.thinklab.interfaces.IInstance)
	 */
	public IInstance createInstance(IInstance i) throws ThinklabException {
		
		IInstance inst = getInstance(i.getLocalName());
		if (inst != null)
			return inst;
		return createInstance(i.asList(null));
	}


	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#createInstance(java.lang.String, org.integratedmodelling.utils.IList)
	 */
	public IInstance createInstance(String ID, IList list)
			throws ThinklabException {
		
		ArrayList<ReferenceRecord> reftable = new ArrayList<ReferenceRecord>();	
		IInstance ret  = createInstanceInternal(ID, list, reftable);
		resolveReferences(reftable);

		return ret;
	}


	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#createInstance(org.integratedmodelling.utils.IList)
	 */
	public IInstance createInstance(IList list) throws ThinklabException {
		
		ArrayList<ReferenceRecord> reftable = new ArrayList<ReferenceRecord>();	
		IInstance ret  = createInstanceInternal(list, reftable);
		resolveReferences(reftable);

		return ret;
	}
	
	IInstance createInstanceInternal(String ID, IList list,
			Collection<ReferenceRecord> reftable) throws ThinklabException {

		IInstance ret = null;
		
		for (Object o : list.array()) {

			if (ret == null) {

				Pair<IConcept, String> cc = ThinklabOWLManager
						.getConceptFromListObject(o, this);
				ret = createInstance(ID, cc.getFirst());

			} else if (o instanceof IList) {
				ThinklabOWLManager.get().interpretPropertyList((IList) o,
						this, ret, reftable);
			} 
		}

		return ret;
	}


	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#getLastModificationDate()
	 */
	public long getLastModificationDate() {
		// TODO Auto-generated method stub
		return 0;
	}


	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#getURI()
	 */
	public String getURI() {
		return ont.getURI().toString();
	}


	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#getUniqueObjectName(java.lang.String)
	 */
	public String getUniqueObjectName(String prefix) {
		/*
		 * FIXME
		 * not the most efficient way, but OWLAPI doesn't seem to have a catalog of names or 
		 * a name generation function like protege.
		 */
		String ret = NameGenerator.newName(prefix);
		while (containsName(ret)) {
			ret = NameGenerator.newName(prefix);
		}
		return ret;
	}


	private boolean containsName(String ret) {

		SemanticType st = new SemanticType(cs, ret);
		return 
			instances.get(st) != null ||
			concepts.get(st) != null ||
			properties.get(st) != null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#read(java.net.URL)
	 */
	public void read(URL url) {

		// TODO
		
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IOntology#removeInstance(java.lang.String)
	 */
	public void removeInstance(String id) throws ThinklabException {

        OWLEntityRemover remover = 
        	new OWLEntityRemover(FileKnowledgeRepository.get().manager, Collections.singleton(ont));

        Instance inst = (Instance) instances.get(new SemanticType(cs, id));

        if (inst != null) {
        	
        	inst.entity.accept(remover);

        	try {
        		FileKnowledgeRepository.get().manager.applyChanges(remover.getChanges());
        	} catch (OWLOntologyChangeException e) {
        		throw new ThinklabRuntimeException(e);
        	}
        }

	}

	@Override
	public boolean write(URI physicalURI) throws ThinklabException {

		boolean ret = true;
		if (physicalURI == null) {
			try {
				String fn = kr.ontologyfn.get(getConceptSpace());
				if (fn != null) {
					File ff = new File(fn);
					FileKnowledgeRepository.get().manager.saveOntology(ont, ff.toURI());
				}
				} catch (Exception e) {
				ret = false;
			}
			return ret;
		}
		
		try {
			FileKnowledgeRepository.get().manager.
				saveOntology(ont, new OWLXMLOntologyFormat(), physicalURI);
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
		
		return ret;
	}


	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#addDescription(java.lang.String)
	 */
	public void addDescription(String desc) {
		// TODO Auto-generated method stub
	
	}


	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#addDescription(java.lang.String, java.lang.String)
	 */
	public void addDescription(String desc, String language) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#addLabel(java.lang.String)
	 */
	public void addLabel(String desc) {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#addLabel(java.lang.String, java.lang.String)
	 */
	public void addLabel(String desc, String language) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#getDescription()
	 */
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#getDescription(java.lang.String)
	 */
	public String getDescription(String languageCode) {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#getLabel()
	 */
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IResource#getLabel(java.lang.String)
	 */
	public String getLabel(String languageCode) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString(){
		return cs;
	}
	    
	protected IKnowledge resolveURI(URI uri){
		SemanticType st = Registry.get().getSemanticType(uri);
		if (concepts.containsKey(st)) return concepts.get(st);
		else if (properties.containsKey(st)) return properties.get(st);
		else if(instances.containsKey(st)) return instances.get(st);
		else return null;
	}

	public IInstance createInstanceInternal(IList list,
			Collection<ReferenceRecord> reftable) throws ThinklabException {
		
		IInstance ret = null;
		
		for (Object o : list.array()) {
			
			/**
			 * We may simply add an instance as the only list element, meaning we just want to 
			 * use it without creating anything.
			 */
			if (o instanceof IInstance) {
				return (IInstance)o;
			}
			
			if (ret == null) {

				Pair<IConcept, String> cc = ThinklabOWLManager.getConceptFromListObject(o, this);
				ret = createInstance(
						cc.getSecond() == null ? getUniqueObjectName("ins") : cc.getSecond(),  
						cc.getFirst());		
				
			} else if (o instanceof IList) {
				ThinklabOWLManager.get().interpretPropertyList((IList)o, this, ret, reftable);
			}
			
		}
		
		return ret;
	}

	@Override
	public boolean isAnonymous() {
		return ont.getURI().toString().startsWith(FileKnowledgeRepository.DEFAULT_TEMP_URI);
	}

	@Override
	public boolean equals(Object obj) {
		return 
			(obj != null) &&
			(obj instanceof Ontology) &&
			(((Ontology)obj).getURI().equals(getURI()));
	}

	@Override
	public int hashCode() {
		return getURI().hashCode();
	}

	/**
	 * Used internally to find concepts: can see the internal concepts in the session as well
	 * as the KM public ones.
	 * 
	 * @return
	 * @throws ThinklabResourceNotFoundException
	 */
	IConcept findConcept(String id) throws ThinklabException {
		
		IConcept ret = null;
		if (id.startsWith(getConceptSpace()))
			ret = this.getConcept(id.substring(id.indexOf(":") + 1));
		
		if (ret == null)
			ret = KnowledgeManager.get().requireConcept(id);
		
		if (ret == null)
			throw new ThinklabResourceNotFoundException("concept " + id + " unknown to session");
		
		return ret;
	}
	
	@Override
	public IConcept createConcept(IList list) throws ThinklabException {
		
		Concept ret = null;
		String id = null;
		
		OWLOntologyManager manager = FileKnowledgeRepository.get().manager;
		OWLDataFactory factory = FileKnowledgeRepository.get().manager.getOWLDataFactory();
		
		for (Object o : list.array()) {
			
			if (o instanceof IList) {
			} else {
			
				String s = o.toString();
				if (!SemanticType.validate(s)) {
					s = this.getConceptSpace() + ":" + s;
				}

				if (findConcept(s) == null) {
					
				}			
			}
			
			
//
//			if (ret == null) {
//
//				Triple<Set<IConcept>, String, LogicalConnector> cc = ThinklabOWLManager.getConceptsFromListObject(o, this);
//				
//				id = cc.getSecond();
//				if (id == null)
//					id = NameGenerator.newName("tcl");
//				
//				OWLClass newcl = factory.getOWLClass(URI.create(getURI() + "#" + id));
//
//				if (cc.getFirst().size() == 1) {
//					
//					OWLClass parent = (OWLClass) ((Concept)(cc.getFirst().iterator().next())).entity;
//					OWLAxiom axiom = factory.getOWLSubClassAxiom(newcl, parent);
//					AddAxiom add = new AddAxiom(ont, axiom);
//					try {
//						manager.applyChange(add);
//					} catch (OWLOntologyChangeException e) {
//						throw new ThinklabValidationException(e);
//					}
//					
//				} else {
//				
//					HashSet<OWLDescription> alld = new HashSet<OWLDescription>();
//					
//					for (IConcept c : cc.getFirst()) {
//						alld.add(((Concept)c).entity.asOWLClass());
//					}
//					
//					OWLDescription parents = 
//						cc.getThird().equals(LogicalConnector.UNION) ?
//								factory.getOWLObjectUnionOf(alld) :
//								factory.getOWLObjectIntersectionOf(alld);
//								
//					OWLAxiom axiom = factory.getOWLSubClassAxiom(newcl, parents);
//					AddAxiom add = new AddAxiom(ont, axiom);
//					try {
//						manager.applyChange(add);
//					} catch (OWLOntologyChangeException e) {
//						throw new ThinklabValidationException(e);
//					}
//				}				
//				ret = new Concept(newcl);
//
//			} else if (o instanceof IList) {
//				
//				IList pl = (IList)o;
//				
//				/*
//				 * must be a property and a value, instance list, or list of values to restrict it with.
//				 */
//				Property property = ThinklabOWLManager.getPropertyFromListObject(pl.first());
//				
//				/*
//				 * we only allow multiple values if the property is a classification property.
//				 * In that case, they must all be concepts. Otherwise we restrict to concepts
//				 * or literal values.
//				 */
//				
//				if (property.isClassification()) {
//					
//					Object[] rs = pl.array();
//					
//					/*
//					 * all values should specify concepts. We should build an instance that is
//					 * child of a union of all of them and use it as a restriction.
//					 */
//					Set<OWLClass> concepts = new HashSet<OWLClass>();
//					
//					for (int i = 1; i < rs.length; i++) {
//					
//						/*
//						 * collect concept
//						 */
//						if (rs[i] instanceof IConcept)
//							concepts.add(((Concept)rs[i]).entity.asOWLClass());
//						else {
//							IConcept cc = KnowledgeManager.get().requireConcept(rs[i].toString());
//							concepts.add(((Concept)cc).entity.asOWLClass());
//						}
//					}
//					
//					/* 
//					 * create instance of concept (if one) or the union of all if > 1
//					 */
//					OWLIndividual ind = 
//						 FileKnowledgeRepository.df.getOWLIndividual(URI.create(ont.getURI() + "#" + NameGenerator.newName("dum")));						
//					OWLDescription type = null;
//					if (concepts.size() == 1) {
//						type = concepts.iterator().next();
// 					} else {
//						type = factory.getOWLObjectUnionOf(concepts);
//					}
//					
//					OWLAxiom ax = 
//						FileKnowledgeRepository.df.getOWLClassAssertionAxiom(ind, type);
//					AddAxiom add = new AddAxiom(ont,ax);
//					
//					try {
//						manager.applyChange(add);
//					} catch (OWLOntologyChangeException e) {
//						throw new ThinklabValidationException(e);
//					}
//					
//					
//					/*
//					 * restrict property
//					 */
//					OWLDescription hasVal = 
//						factory.getOWLObjectValueRestriction(
//								(OWLObjectProperty)(property.entity), 
//								ind);
//					
//					OWLAxiom axiom = factory.getOWLSubClassAxiom((OWLClass)(ret.entity), hasVal);
//					add = new AddAxiom(ont, axiom);
//					try {
//						manager.applyChange(add);
//					} catch (OWLOntologyChangeException e) {
//						throw new ThinklabValidationException(e);
//					}
//					
//				} else if (property.isObjectProperty()) {
//
//					/*
//					 * pl.second should be an instance or a list that specifies an instance.
//					 */
//					Instance inst = null;
//					if (pl.second() instanceof IList) {
//						inst = (Instance) this.createInstance((IList) pl.second());
//					} else if (pl.second() instanceof Instance) {
//						inst = (Instance)pl.second();
//					} else {
//						throw new ThinklabValidationException("concept restriction: can't turn " + pl.second() + " into an instance");
//					}
//					
//					/*
//					 * add hasValue restriction
//					 */
//					OWLDescription hasVal = 
//						factory.getOWLObjectValueRestriction(
//								(OWLObjectProperty)(property.entity), 
//								(OWLIndividual)(inst.entity));
//					
//					OWLAxiom axiom = factory.getOWLSubClassAxiom((OWLClass)(ret.entity), hasVal);
//					AddAxiom add = new AddAxiom(ont, axiom);
//					try {
//						manager.applyChange(add);
//					} catch (OWLOntologyChangeException e) {
//						throw new ThinklabValidationException(e);
//					}
//					
//				} else {
//					
//					/*
//					 * TODO must be a literal or a IValue
//					 */
//					throw new ThinklabUnimplementedFeatureException("value restriction on literals unimplemented");
//				}
//			}
		}

		concepts.put(new SemanticType(cs, id), ret);
		
		return ret;
		
	}

	@Override
	public IOntology getOntology() {
		return this;
	}

	@Override
	public IConcept createConcept(String localName, IConcept[] parents) throws ThinklabException {

		OWLOntologyManager manager = FileKnowledgeRepository.get().manager;
		OWLDataFactory factory = FileKnowledgeRepository.get().manager.getOWLDataFactory();		
		IConcept ret = getConcept(localName);
		
		if (ret == null) {
			
			URI uri = URI.create(getURI() + "#" + localName);
			OWLClass newcl = factory.getOWLClass(uri);
			OWLAxiom axiom = factory.getOWLDeclarationAxiom(newcl);
			
			try {
				manager.addAxiom(ont, axiom);
			} catch (OWLOntologyChangeException e) {
				throw new ThinklabValidationException(e);
			}
			
			ret = new Concept(newcl);
			concepts.put(kr.registry.getSemanticType(uri), new Concept(newcl));
		}
		
		ArrayList<IConcept> pars = new ArrayList<IConcept>();
		if (parents != null) {
			for (IConcept c : parents)
				if (c != null)
					pars.add(c);
		}
		
		for (IConcept p : pars) {
						
			if (ret.is(p))
				continue;
			
			OWLClass parent = (OWLClass) ((Concept)p).entity;
			OWLAxiom axiom = factory.getOWLSubClassAxiom((OWLClass)((Concept)ret).entity, parent);
		
			try {
				manager.addAxiom(ont,axiom);
			} catch (OWLOntologyChangeException e) {
				throw new ThinklabValidationException(e);
			}
		}
		
		return ret;
	}

	@Override
	public IList asList() {
		// TODO Auto-generated method stub
		return null;
	}

}