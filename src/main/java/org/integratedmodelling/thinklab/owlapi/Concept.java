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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IKnowledge;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAnnotationAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLRestriction;

/**
 * @author Ioannis N. Athanasiadis
 * @author Ferdinando Villa
 * 
 */
public class Concept extends Knowledge implements IConcept {

	
	/**
	 * @param cl
	 * @throws ThinklabResourceNotFoundException
	 */
	public Concept(OWLClass cl) {
		super(cl, OWLType.CLASS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#isAbstract()
	 */
	public boolean isAbstract() {
		try{
			return (getAnnotationProperties().contains(Thinklab.ABSTRACT_PROPERTY));
		} catch (Exception e) {
			return false;
		}
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * FIXME for now the only time this returns anything different from getInstances()
//	 *  is when a reasoner is connected - check what we want with these two, and
//	 *  possibly remove one method.
//	 *  
//	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getAllInstances()
//	 */
//	public Collection<IInstance> getAllInstances() {
//
//		Set<IInstance> ret = new HashSet<IInstance>();
//		
//		try {
//			if (FileKnowledgeRepository.get().instanceReasoner != null) {
//				for (OWLIndividual ind : 
//						FileKnowledgeRepository.get().instanceReasoner.
//							getIndividuals(this.entity.asOWLClass(), false)) {
//					ret.add(new Instance(ind));
//				}
//			}
//		} catch (OWLReasonerException e) {
//			// just proceed with the dumb method
//		}
// 		
//		
//		Set<OWLOntology> ontologies = FileKnowledgeRepository.KR.manager.getOntologies();
//		for (OWLIndividual ind : this.entity.asOWLClass().getIndividuals(ontologies)) {
//			ret.add(new Instance(ind));
//		}
//		return ret;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getParent()
	 */
	public IConcept getParent() throws ThinklabException {
		Collection<IConcept> pp = getParents();
		if (pp.size() > 1) {
			throw new ThinklabException("Concept " + getSemanticType()
					+ " has more than one parent");
		}
		return pp.iterator().next();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getParents()
	 */
	public Collection<IConcept> getParents() {
		
		Set<IConcept> concepts = new HashSet<IConcept>();
		synchronized (this.entity) {
			Set<OWLDescription> set = ((OWLClass) this.entity)
				.getSuperClasses(KR().manager.getOntologies());
		
			for (OWLDescription s : set) {
				if (!(s.isAnonymous() || s.isOWLNothing()))
					concepts.add(new Concept(s.asOWLClass()));
			}
		}

		// OWLAPI doesn't do this - only add owl:Thing if this is its direct subclass, i.e. has no 
		// parents in OWLAPI.
		if (concepts.isEmpty() && !KR().getRootConcept().equals(this))
			concepts.add(KR().getRootConcept());
		
		return concepts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getAllParents()
	 */
	public Collection<IConcept> getAllParents() {

		Set<IConcept> concepts = new HashSet<IConcept>();
		
		if (KR().getClassReasoner() != null) {
			
			try {
				Set<Set<OWLClass>> parents = 
					KR().getClassReasoner()
						.getAncestorClasses((OWLClass) entity);
				Set<OWLClass> subClses = OWLReasonerAdapter
						.flattenSetOfSets(parents);
				for (OWLClass cls : subClses) {
					concepts.add(new Concept(cls));
				}
				
				return concepts;
				
			} catch (OWLException e) {
				// just continue to dumb method
			}

		} else {
			
			for (IConcept c : getParents()) {				
				concepts.add(c);
				concepts.addAll(c.getAllParents());
 				
			}
		}

		// OWLAPI doesn't do this
		concepts.add(KR().getRootConcept());

		return concepts;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getChildren()
	 */
	public Collection<IConcept> getChildren() {
		Set<IConcept> concepts = new HashSet<IConcept>();
		synchronized (this.entity) {
			Set<OWLDescription> set = ((OWLClass) this.entity)
				.getSubClasses(KR().manager.getOntologies());
			for (OWLDescription s : set) {
				if (!(s.isAnonymous() || s.isOWLNothing() || s.isOWLThing()))
					concepts.add(new Concept(s.asOWLClass()));
			}
			if (set.isEmpty() && ((OWLClass) entity).isOWLThing()) {
				for (IOntology onto : KR().ontologies
					.values()) {
					concepts.addAll(onto.getConcepts());
				}
			}
		}
		return concepts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getAllProperties()
	 */
	public Collection<IProperty> getAllProperties() {
		Set<IProperty> props = (Set<IProperty>) getProperties();
		for(IConcept c: getAllParents()){
			props.addAll(c.getProperties());
		}
		return props;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getAnnotationProperties()
	 */
	public Collection<IProperty> getAnnotationProperties() {
		// where are we searching? In all ontologies...
		Set<OWLOntology> ontologies = KR().manager.getOntologies();
		Set<IProperty> properties = new HashSet<IProperty>();
		for (OWLOntology ontology : ontologies) {
			synchronized (ontology) {
				for ( OWLAnnotationAxiom<?> op : ontology.getAnnotationAxioms()) {
					if(op.getSubject().equals(this.entity)){
						OWLAnnotation<?> ann = op.getAnnotation();
						if(ann.isAnnotationByConstant()){
							//					TODO	
						} else {
							//					TODO
						}
					}
				}
			}
		}
		return properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getProperties()
	 */
	public Collection<IProperty> getProperties() {
		
		Collection<IProperty> props = getDirectProperties();
		ArrayList<Collection<IProperty>> psets = new ArrayList<Collection<IProperty>>();
		
		for (IProperty prop: props) 
			synchronized (prop) {
				psets.add(prop.getChildren());
			}
		
		for (Collection<IProperty> pp : psets) 
			props.addAll(pp);
		
		return props;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getDirectProperties()
	 */
	public Collection<IProperty> getDirectProperties() {
		
		// where are we searching? In all ontologies...
		Set<OWLOntology> ontologies = KR().manager.getOntologies();
		
		Set<IProperty> properties = new HashSet<IProperty>();
		for (OWLOntology ontology : ontologies) {
			synchronized (ontology) {
				for (OWLObjectProperty op : ontology
						.getReferencedObjectProperties()) {
					Set<OWLDescription> rang = op.getDomains(ontologies);
					if (rang.contains(entity))
						properties.add(new Property(op));
				}
				for (OWLDataProperty op : ontology.getReferencedDataProperties()) {
					Set<OWLDescription> rang = (op.getDomains(ontologies));
					if (rang.contains(entity))
						properties.add(new Property(op));
				}
			}
		}
		return properties;
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getInstances()
//	 */
//	public Collection<IInstance> getInstances() {
//		
//		Set<IInstance> ret = new HashSet<IInstance>();
//		
//		try {
//			if (FileKnowledgeRepository.get().instanceReasoner != null) {
//				for (OWLIndividual ind : 
//						FileKnowledgeRepository.get().instanceReasoner.
//							getIndividuals(this.entity.asOWLClass(), true)) {
//					ret.add(new Instance(ind));
//				}
//			}
//		} catch (OWLReasonerException e) {
//			// just proceed with the dumb method
//		}
// 		
//		
//		Set<OWLOntology> ontologies = FileKnowledgeRepository.KR.manager.getOntologies();
//		synchronized (this.entity) {
//			for (OWLIndividual ind : this.entity.asOWLClass().getIndividuals(ontologies)) {
//				ret.add(new Instance(ind));
//			}
//		}
//		
//		return ret;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getLeastGeneralCommonConcept(org.integratedmodelling.thinklab.interfaces.IConcept)
	 */
	public IConcept getLeastGeneralCommonConcept(IConcept otherConcept) {
		// should we use the class tree?
		IConcept ret = null;
		if (otherConcept == null)
			return this;
		if (is(otherConcept))
			ret = otherConcept;
		else if (otherConcept.is(this))
			ret = this;
		else {
			for (IConcept pp : getParents()) {
				IConcept c1 = pp.getLeastGeneralCommonConcept(otherConcept);
				if (c1 != null) {
					ret = c1;
					break;
				}
			}
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getMaxCardinality(org.integratedmodelling.thinklab.interfaces.IProperty)
	 */
	public int getMaxCardinality(IProperty property) {

		int ret = 1;
		
		if (!((Property)property).isFunctional())

			for (OWLRestriction<?> r : OWLAPI.getRestrictions(this, true)) {
			
				if (r instanceof OWLDataMaxCardinalityRestriction &&
					r.getProperty().equals(((Property)property).entity)) {
			
					ret = ((OWLDataMaxCardinalityRestriction)r).getCardinality();
				
				} else 	if (r instanceof OWLObjectMaxCardinalityRestriction &&
					r.getProperty().equals(((Property)property).entity)) {
			
					ret = ((OWLObjectMaxCardinalityRestriction)r).getCardinality();
				}
		}
		return ret;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getMinCardinality(org.integratedmodelling.thinklab.interfaces.IProperty)
	 */
	public int getMinCardinality(IProperty property) {
		
		int ret = ((Property)property).isFunctional() ? 1 : 0;
		if (ret != 1)
			for (OWLRestriction<?> r : OWLAPI.getRestrictions(this, true)) {
			
				if (r instanceof OWLDataMinCardinalityRestriction &&
					r.getProperty().equals(((Property)property).entity)) {
			
					ret = ((OWLDataMinCardinalityRestriction)r).getCardinality();
				} else 	if (r instanceof OWLObjectMinCardinalityRestriction &&
					r.getProperty().equals(((Property)property).entity)) {
			
					ret = ((OWLObjectMinCardinalityRestriction)r).getCardinality();
				}
		}
		return ret;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getNumberOfProperties(java.lang.String)
	 */
	public int getPropertiesCount(String property) {
		return getProperties().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getPropertyRange(org.integratedmodelling.thinklab.interfaces.IProperty)
	 */
	public Collection<IConcept> getPropertyRange(IProperty property) {
		
		ArrayList<IConcept> ret = new ArrayList<IConcept>();
		IConcept conc = this;
		
		while (conc != null && !conc.toString().equals("owl:Thing")) {
			
			for (OWLRestriction r : OWLAPI.getRestrictions((Concept)conc, true)) {
			
				if (r instanceof OWLObjectAllRestriction &&
						r.getProperty().equals(((Property)property).entity)) {
					
					if (!((OWLObjectAllRestriction)r).getFiller().isAnonymous())
						ret.add(new Concept(((OWLObjectAllRestriction)r).getFiller().asOWLClass()));
	
				} else if (r instanceof OWLObjectSomeRestriction &&
						r.getProperty().equals(((Property)property).entity)) {
					
					if (!((OWLObjectSomeRestriction)r).getFiller().isAnonymous())
						ret.add(new Concept(((OWLObjectSomeRestriction)r).getFiller().asOWLClass()));
				}
			}
			
			if (ret.size() > 0)
				break;

			try {
				conc = conc.getParent();
			} catch (ThinklabException e) {
				// FIXME this should follow all parents  recursively, not loop and ignore the other parents.
				conc = null;
			}
		}
		
		if (ret.size() == 0)
			for (IConcept cc : property.getRange())
				ret.add(cc);
		
		return ret;
	}
	
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getRestrictions()
//	 */
//	public IQuery getRestrictions() throws ThinklabException {
//
//		/*
//		 * This accumulates all restrictions from parents as well.
//		 */		
//		Collection<OWLRestriction<?>> rs = OWLAPI.getRestrictions(this,true);
//		Ontology ont = getThinklabOntology();
//		Constraint ret = new Constraint(this);
//		
//			for (OWLRestriction<?> r : rs) {
//
//				IProperty p = new Property(r.getProperty());
//				Quantifier q = null;
//				
//				if (r instanceof OWLDataAllRestriction ||
//					r instanceof OWLObjectAllRestriction) {
//
//					q = new Quantifier("all");
//
//					IConcept c = 
//						ThinklabOWLManager.get().getRestrictionFiller(r, ont);
//					
//					if (c != null)
//						ret.restrict(new Restriction(q, p, new Constraint(c)));
//
//				} else if (r instanceof OWLDataSomeRestriction || 
//						   r instanceof OWLObjectSomeRestriction) {
//
//					q = new Quantifier("any");
//					
//					IConcept c = 
//						ThinklabOWLManager.get().getRestrictionFiller(r, ont);
//					
//					if (c != null)
//						ret.restrict(new Restriction(q, p, new Constraint(c)));
//
//				} else if (r instanceof OWLDataExactCardinalityRestriction) {
//											
//					int card = ((OWLDataExactCardinalityRestriction)r).getCardinality();
//					q = new Quantifier(Integer.toString(card));
//					ret.restrict(new Restriction(q, p));
//
//				}  else if (r instanceof OWLObjectExactCardinalityRestriction) {
//											
//					int card = ((OWLObjectExactCardinalityRestriction)r).getCardinality();
//					q = new Quantifier(Integer.toString(card));
//					ret.restrict(new Restriction(q, p));
//
//				} else if (r instanceof OWLDataMinCardinalityRestriction) {
//
//					int card = ((OWLDataMinCardinalityRestriction)r).getCardinality();			
//					q = new Quantifier(card+":");
//					ret.restrict(new Restriction(q, p));
//
//				} else if (r instanceof OWLObjectMinCardinalityRestriction) {
//					
//					int card = ((OWLObjectMinCardinalityRestriction)r).getCardinality();
//					q = new Quantifier(card+":");
//					ret.restrict(new Restriction(q, p));
//
//				} else if (r instanceof OWLDataMaxCardinalityRestriction) {
//
//					int card = ((OWLDataMaxCardinalityRestriction)r).getCardinality();
//					q = new Quantifier(":" + card);
//					ret.restrict(new Restriction(q, p));
//
//				} else if (r instanceof OWLObjectMaxCardinalityRestriction) {
//					
//					int card = ((OWLObjectMaxCardinalityRestriction)r).getCardinality();
//					q = new Quantifier(":" + card);
//					ret.restrict(new Restriction(q, p));
//					
//				} else if (r instanceof OWLDataValueRestriction) {
//
//					ret.restrict(
//							new Restriction(
//									new Quantifier("all"),
//									p, 
//									"=", 
//									(((OWLDataValueRestriction)r).getValue().toString())));
//					
//				}  else if (r instanceof OWLObjectValueRestriction) {
//
////										ret.restrict(
////												new Restriction(
////														new Quantifier("all"),
////														p, 
////														"=", 
////														((OWLHasValue)r).getHasValue().toString()));
//									}
//				/*
//				 * TODO there are more restrictions in OWL > 1.0; must check which ones
//				 * go into a constraint
//				 */
//			}
//		
//		return ret;
//
//		
//	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @Override
//	 * 
//	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getDefinition()
//	 */
//	public IQuery getDefinition() {
//
//		Query ret = null;
//		ArrayList<OWLRestriction<?>> rs = new ArrayList<OWLRestriction<?>>();
//
//		try {
//
//			ret = getDefinitionInternal(this, rs);
//			Ontology ont = getThinklabOntology();
//
//			if (rs != null)
//				for (OWLRestriction<?> r : rs) {
//
//					IProperty p = new Property(r.getProperty());
//					Quantifier q = null;
//
//					if (r instanceof OWLDataAllRestriction
//							|| r instanceof OWLObjectAllRestriction) {
//
//						q = new Quantifier("all");
//
//						IConcept c = ThinklabOWLManager.get()
//								.getRestrictionFiller(r, ont);
//
//						if (c != null)
//							ret.restrict(new Restriction(q, p,
//									new Query(c)));
//
//					} else if (r instanceof OWLDataSomeRestriction
//							|| r instanceof OWLObjectSomeRestriction) {
//
//						q = new Quantifier("any");
//
//						IConcept c = ThinklabOWLManager.get()
//								.getRestrictionFiller(r, ont);
//
//						if (c != null)
//							ret.restrict(new Restriction(q, p,
//									new Query(c)));
//
//					} else if (r instanceof OWLDataExactCardinalityRestriction) {
//
//						int card = ((OWLDataExactCardinalityRestriction) r)
//								.getCardinality();
//						q = new Quantifier(Integer.toString(card));
//						ret.restrict(new Restriction(q, p));
//
//					} else if (r instanceof OWLObjectExactCardinalityRestriction) {
//
//						int card = ((OWLObjectExactCardinalityRestriction) r)
//								.getCardinality();
//						q = new Quantifier(Integer.toString(card));
//						ret.restrict(new Restriction(q, p));
//
//					} else if (r instanceof OWLDataMinCardinalityRestriction) {
//
//						int card = ((OWLDataMinCardinalityRestriction) r)
//								.getCardinality();
//						q = new Quantifier(card + ":");
//						ret.restrict(new Restriction(q, p));
//
//					} else if (r instanceof OWLObjectMinCardinalityRestriction) {
//
//						int card = ((OWLObjectMinCardinalityRestriction) r)
//								.getCardinality();
//						q = new Quantifier(card + ":");
//						ret.restrict(new Restriction(q, p));
//
//					} else if (r instanceof OWLDataMaxCardinalityRestriction) {
//
//						int card = ((OWLDataMaxCardinalityRestriction) r)
//								.getCardinality();
//						q = new Quantifier(":" + card);
//						ret.restrict(new Restriction(q, p));
//
//					} else if (r instanceof OWLObjectMaxCardinalityRestriction) {
//
//						int card = ((OWLObjectMaxCardinalityRestriction) r)
//								.getCardinality();
//						q = new Quantifier(":" + card);
//						ret.restrict(new Restriction(q, p));
//
//					} else if (r instanceof OWLDataValueRestriction) {
//
//						ret.restrict(new Restriction(new Quantifier("all"), p,
//								"=", (((OWLDataValueRestriction) r).getValue()
//										.toString())));
//
//					} else if (r instanceof OWLObjectValueRestriction) {
//
//						// ret.restrict(
//						// new Restriction(
//						// new Quantifier("all"),
//						// p,
//						// "=",
//						// ((OWLHasValue)r).getHasValue().toString()));
//					}
//					/*
//					 * TODO there are more restrictions in OWL > 1.0; must check
//					 * which ones go into a constraint
//					 */
//				}
//
//			// merge in any further constraints from Thinklab-specific
//			// annotations
//			Query tlc = null; // ThinklabOWLManager.get().getAdditionalConstraints(this);
//
//			if (tlc != null) {
//				ret.merge(tlc, LogicalConnector.INTERSECTION);
//			}
//
//		} catch (Exception e) {
//			throw new ThinklabRuntimeException(e);
//		}
//
//		return ret;
//
//	}
//
//	/* 
//	 * accumulate suitable restrictions recursively until no more restrictions on
//	 * inherited properties are found; return constraint initialized with stop concept, or null if we must
//	 * continue.
//	 * 
//	 * If there are multiple parents, this will stop at the first that matches the
//	 * stop condition. Which is probably not the right thing to do.
//	 */ 
//	private static Query getDefinitionInternal(IConcept c, Collection<OWLRestriction<?>> restrictions) 
//		throws ThinklabException {
//		
//		Collection<OWLRestriction<?>> rs = OWLAPI.getRestrictions((Concept)c, false);
//				
//		boolean found = false;
//		if (rs != null) {
//			for (OWLRestriction<?> r : rs) {
//
//				IProperty p = new Property(r.getProperty());
//			
//				if (p.getDomain().equals(c))
//					continue;
//				
//				restrictions.add(r);
//				found = true;
//			}
//		}
//		
//		if (!found)
//			return new Query(c);
//		
//		for (IConcept cc : c.getParents()) {
//			Query ret = getDefinitionInternal(cc, restrictions);
//			if (ret != null)
//				return ret;	
//		}
//		
//		return null;
//	}
//

	@Override
	public boolean is(IKnowledge c) {
		
		if (! (c instanceof Concept))
			return false;
		
		Concept cc = (Concept)c;
		
		if (cc.equals(this))
			return true;
		
		try {
			if (KR().getClassReasoner() != null) {
				return 
					KR().getClassReasoner().
						isSubClassOf(this.entity.asOWLClass(), cc.entity.asOWLClass());
			}
		} catch (OWLReasonerException e) {
			// just proceed with the dumb method
		}
 		
		Collection<IConcept> collection = getAllParents();
		collection.add(this);
		return collection.contains(c);
	}

	@Override
	public IQuery getDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IConcept> getSemanticClosure() {

		if (KR().getClassReasoner() != null) {
			
			HashSet<IConcept> ret = new HashSet<IConcept>();
			Set<Set<OWLClass>> cset = null;
			try {
				cset = KR().getClassReasoner().getDescendantClasses(entity.asOWLClass());
			} catch (OWLReasonerException e) {
				throw new ThinklabRuntimeException(e);
			}
			for (Set<OWLClass> set : cset) {
				for (OWLClass cl : set) {
					ret.add(new Concept(cl.asOWLClass()));
				}
			}
			return ret;
		}
		
		return collectChildren(new HashSet<IConcept>());
	}
	

	private Set<IConcept> collectChildren(Set<IConcept> hashSet) {

		for (IConcept c : getChildren()) {
			if (!hashSet.contains(c));
				((Concept)c).collectChildren(hashSet);
			hashSet.add(c);
		}			
		return hashSet;
	}
}
