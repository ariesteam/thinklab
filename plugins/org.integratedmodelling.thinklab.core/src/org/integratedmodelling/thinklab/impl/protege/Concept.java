/**
 * Concept.java
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
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedSemanticTypeException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IKnowledge;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.integratedmodelling.thinklab.interfaces.IRelationship;
import org.integratedmodelling.thinklab.interfaces.IResource;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.utils.LogicalConnector;
import org.integratedmodelling.utils.Quantifier;

import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLCardinality;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSNamedClass;

public class Concept implements IConcept {

	private static final  Logger log = Logger.getLogger(Concept.class);

	protected RDFSClass concept;

	public Concept(OWLClass owlClass) {
		this.concept = owlClass;
		// log.debug("Concept " +getURI() + " generated");
	}

	public Concept(RDFSClass rdfsClass) {
		this.concept = rdfsClass;
		log.debug("Concept " +getSemanticType() + " generated");
	}


	public IConcept getType() {
		return this;
	}
	
	public IConcept getParent() throws ThinklabException {
		Collection<IConcept> pp = getParents();
		if (pp.size() > 1) {
			throw new ThinklabException("concept " + getSemanticType() + " has more than one parent");
		}
		return pp.iterator().next();
	}

	/* This gets the direct parents only
	 * (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.IConcept#getParents()
	 */
	@SuppressWarnings("unchecked")
	public Collection<IConcept> getParents() {
		Collection<IConcept> result = new ArrayList();
		Iterator<RDFSClass> iter = concept.getNamedSuperclasses(false).iterator();
		while (iter.hasNext()) {
			result.add(new Concept(iter.next()));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Collection<IConcept> getAllParents() {
		Collection<IConcept> result = new ArrayList();
		Collection assertedsuperclasses = this.concept.getNamedSuperclasses(true);
		Iterator<RDFSClass> iter = assertedsuperclasses.iterator();
		while (iter.hasNext()) {
			result.add(new Concept(iter.next()));
		}
		ProtegeOWLReasoner reasoner = ReasonerManager.getInstance().getReasoner(concept.getOWLModel());
		if(reasoner.isConnected()){
			Collection inferredsuperclasses = new ArrayList();
//			This is the reasoner. I think that we should not check if it is connected, as this is a global option
			try {
				inferredsuperclasses = reasoner.getSuperclasses((OWLClass) concept, null);
			} catch (DIGReasonerException e) {
				log.warn(e);
			}
			Iterator<RDFSClass> iter2 = inferredsuperclasses.iterator();
			while (iter2.hasNext()) {
				result.add(new Concept(iter2.next()));
			}
		}
		return result;
	}



	@SuppressWarnings("unchecked")
	public Collection<IConcept> getChildren() {
		Collection<IConcept> result = new ArrayList<IConcept>();
		Iterator<RDFSClass> i =  concept.getNamedSubclasses(false).iterator();
		while (i.hasNext()) {
			Concept cc = new Concept(i.next());
			try {
				if (KnowledgeManager.get().getKnowledgeRepository().retrieveOntology(cc.getConceptSpace()) != null)
					result.add(cc);
			} catch (ThinklabNoKMException e) {
			}
		}
		return result;
	}

	
	@SuppressWarnings("unchecked")
	public Collection<IProperty> getProperties() {
		ArrayList<IProperty> ret = new ArrayList<IProperty>();		
		
		if (concept instanceof DefaultOWLNamedClass) {	
			Collection<RDFProperty> cp = ((DefaultOWLNamedClass)concept).getAssociatedProperties();
			for (RDFProperty p : cp)
				ret.add(new Property(p));	
		}
		return ret;
	}
	
	
	@SuppressWarnings("unchecked")
	public Collection<IProperty> getAllProperties() {
		ArrayList<IProperty> ret = new ArrayList<IProperty>();		
		if (concept instanceof DefaultOWLNamedClass) {	
			Collection<RDFProperty> cp = concept.getUnionDomainProperties(true);		
			for (RDFProperty p : cp)
				ret.add(new Property(p));	
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public Collection<IProperty> getDirectProperties() {
		ArrayList<IProperty> ret = new ArrayList<IProperty>();		
		if (concept instanceof DefaultOWLNamedClass) {	
			Collection<RDFProperty> cp = concept.getUnionDomainProperties(false);		
			for (RDFProperty p : cp)
				ret.add(new Property(p));	
		}
		return ret;
	}


	public int getNumberOfProperties(String property) {
		return getProperties().size();
	}

	@SuppressWarnings("unchecked")
	public Collection<IInstance> getAllInstances() {
		ArrayList<IInstance> result = new ArrayList<IInstance>();

		Collection<RDFIndividual> assertedindividuals = concept.getInstances(true);
		Iterator<RDFIndividual> iter2 = assertedindividuals.iterator();
		while (iter2.hasNext()) {
			result.add(new Instance(iter2.next()));
		}

		ProtegeOWLReasoner reasoner = ReasonerManager.getInstance().getReasoner(concept.getOWLModel());
		if(reasoner.isConnected()){
			Collection<RDFIndividual> inferredindividuals = new ArrayList();
			try {
				inferredindividuals = reasoner.getIndividualsBelongingToClass((OWLClass) concept, null);
			} catch (DIGReasonerException e) {
				log.warn(e);
			}
			Iterator<RDFIndividual> iter = inferredindividuals.iterator();
			while (iter.hasNext()) {
				result.add(new Instance(iter.next()));
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Collection<IInstance> getInstances() {
		ArrayList<IInstance> ret = new ArrayList<IInstance>();
		Collection<RDFIndividual> ii = concept.getInstances(false);
		for (RDFIndividual i : ii)
			ret.add(new Instance(i));
		return ret;
	}

	/* We need to override the @equals()@ method for using properly  
	 * @java.util.Collection@, @java.util.Set@, etc
	 * (non-Javadoc)
	 * @see java.lang.Object#equals()
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Concept)
			return this.concept.equals(((Concept)obj).concept);
		return false;
	}
	
	/* We need to override the @hashCode@ method for using properly  
	 * @java.util.Collection@, @java.util.Set@, etc
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
			int result; 
			result = concept.hashCode(); 
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
	
	public boolean is(IKnowledge c) {
		
		// The IConcept should be an OWL and 
		// the IKnowledge should be a Concept as well (as a Concept cannot be an Instance)
		boolean ret = false;
		
		if ((c instanceof Concept)) {
			ret = (c.equals(this) || getAllParents().contains(c));
		} 
		
		/* 
		 * see if we have additional constraints in any of the parents; if so, 
		 * test match if we have a match already. We should probably cache the additional 
		 * constraints related to a concept, for speed. 
		 */
		if (ret && c instanceof IConcept) {
			
			Constraint additional = null;
			try {
				additional = 
					ThinklabOWLManager.get().getAdditionalConstraints((IConcept)c);
			} catch (ThinklabException e1) {
				// just drop it
				return ret;
			}
		
			if (additional != null) {
				try {
					if (!additional.match((IKnowledgeSubject)c)) {
						ret = false;
					}
				} catch (ThinklabException e) {
					// just drop it
				}
			}
		}
		return ret;
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
	
	
	public boolean isAbstract() {
		return ThinklabOWLManager.
		getAnnotationAsBoolean(concept, 
				ThinklabOWLManager.abstractAnnotationProperty,
				false);
	}

	public IConcept getLeastGeneralCommonConcept(IConcept otherConcept) {
		
		// FIXME should we use the class tree?
		IConcept ret = null;
		if (is(otherConcept))
			ret = otherConcept;
		else if (otherConcept.is(this))
			ret = this;
		else  {
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



	public String getLocalName() {
		return concept.getLocalName();
	}

	public SemanticType getSemanticType() {
		return new SemanticType(getConceptSpace(),getLocalName());
	}


	public String toString() {
		return getSemanticType().toString();
	}


	public void addDescription(String desc) {
		concept.addComment(desc);
	}

	public void addDescription(String desc, String language) {
		// TODO Protege does not support languages in comments
		concept.addComment(desc);
	}

	public void addLabel(String desc) {
		concept.addLabel(desc, null);
	}

	public void addLabel(String desc, String language) {
		concept.addLabel(desc, language);
	}

	public String getConceptSpace() {
		return this.concept.getNamespacePrefix();	
	}

	public String getLabel() {
		return ThinklabOWLManager.getLabel(concept, null) ;	
	}

	public String getDescription() {
		return ThinklabOWLManager.getComment(concept, null);	
	}

	public String getLabel(String languageCode) {
		return ThinklabOWLManager.getLabel(concept, languageCode);
	}

	public String getDescription(String languageCode) {
		return ThinklabOWLManager.getComment(concept, null);
	}

	public String getURI() {
		return concept.getURI();
	}

	@SuppressWarnings("unchecked")
	public Constraint getRestrictions() throws ThinklabException {
		
		/*
		 * This accumulates all restrictions from parents as well.
		 */		
		Collection<OWLRestriction> rs = 
			((DefaultOWLNamedClass)concept).getRestrictions(true);
		
		Constraint ret = new Constraint(this);
		
		if (rs != null)
			for (OWLRestriction r : rs) {

				IProperty p = new Property(r.getOnProperty());
				Quantifier q = null;
				
				if (r instanceof OWLAllValuesFrom) {

					q = new Quantifier("all");
					IConcept c = new Concept((OWLClass) ((OWLAllValuesFrom)r).getAllValuesFrom());
					ret.restrict(new Restriction(q, p, new Constraint(c)));

				} else if (r instanceof OWLSomeValuesFrom) {

					q = new Quantifier("any");
					IConcept c = new Concept((OWLClass) ((OWLSomeValuesFrom)r).getSomeValuesFrom());
					ret.restrict(new Restriction(q, p, new Constraint(c)));

				} else if (r instanceof OWLCardinality) {

					int card = ((OWLCardinality)r).getCardinality();
					q = new Quantifier(Integer.toString(card));
					ret.restrict(new Restriction(q, p));

				} else if (r instanceof OWLMinCardinality) {

					int card = ((OWLMinCardinality)r).getCardinality();				
					q = new Quantifier(card+":");
					ret.restrict(new Restriction(q, p));

				} else if (r instanceof OWLMaxCardinality) {

					int card = ((OWLMaxCardinality)r).getCardinality();
					q = new Quantifier(":" + card);
					ret.restrict(new Restriction(q, p));

				} else if (r instanceof OWLHasValue) {

					ret.restrict(
							new Restriction(
									new Quantifier("all"),
									p, 
									"=", 
									((OWLHasValue)r).getHasValue().toString()));
				}
			}
		
		return ret;
	}

	
	public Constraint getDefinition() throws ThinklabException {
		
		ArrayList<OWLRestriction> rs = new ArrayList<OWLRestriction>();
		
		Constraint ret = getDefinitionInternal(this, rs);
		
		if (rs != null)
			for (OWLRestriction r : rs) {

				IProperty p = new Property(r.getOnProperty());
				Quantifier q = null;

				if (r instanceof OWLAllValuesFrom) {

					q = new Quantifier("all");
					IConcept c = new Concept((OWLClass) ((OWLAllValuesFrom)r).getAllValuesFrom());
					ret.restrict(new Restriction(q, p, new Constraint(c)));

				} else if (r instanceof OWLSomeValuesFrom) {

					q = new Quantifier("any");
					IConcept c = new Concept((OWLClass) ((OWLSomeValuesFrom)r).getSomeValuesFrom());
					ret.restrict(new Restriction(q, p, new Constraint(c)));

				} else if (r instanceof OWLCardinality) {

					int card = ((OWLCardinality)r).getCardinality();
					q = new Quantifier(Integer.toString(card));
					ret.restrict(new Restriction(q, p));

				} else if (r instanceof OWLMinCardinality) {

					int card = ((OWLMinCardinality)r).getCardinality();				
					q = new Quantifier(card+":");
					ret.restrict(new Restriction(q, p));

				} else if (r instanceof OWLMaxCardinality) {

					int card = ((OWLMaxCardinality)r).getCardinality();
					q = new Quantifier(":" + card);
					ret.restrict(new Restriction(q, p));

				} else if (r instanceof OWLHasValue) {

					ret.restrict(
							new Restriction(
									new Quantifier("all"),
									p, 
									"=", 
									((OWLHasValue)r).getHasValue().toString()));
				}
			}
		
		// merge in any further constraints from Thinklab-specific annotations
		Constraint tlc = 
			ThinklabOWLManager.get().getAdditionalConstraints(this);
		
		if (tlc != null) {
			ret.merge(tlc, LogicalConnector.INTERSECTION);
		}
		
		return ret;
	}
	
	/* 
	 * accumulate suitable restrictions recursively until no more restrictions on
	 * inherited properties are found; return constraint initialized with stop concept, or null if we must
	 * continue.
	 * 
	 * If there are multiple parents, this will stop at the first that matches the
	 * stop condition. Which is probably not the right thing to do.
	 */ 
	private static Constraint getDefinitionInternal(IConcept c, Collection<OWLRestriction> restrictions) 
		throws ThinklabException {
		
		// DefaultRDFSNamedClass gets here too.
		if (!(((Concept)c).concept instanceof DefaultOWLNamedClass)) {
			return new Constraint(c);
		}
		
		Collection<OWLRestriction> rs = 
			((DefaultOWLNamedClass)(((Concept)c).concept)).getRestrictions(false);
				
		boolean found = false;
		if (rs != null) {
			for (OWLRestriction r : rs) {

				IProperty p = new Property(r.getOnProperty());
				
				if (p.getDomain().equals(c))
					continue;
				
				restrictions.add(r);
				found = true;
			}
		}
		
		if (!found)
			return new Constraint(c);
		
		for (IConcept cc : c.getParents()) {
			Constraint ret = getDefinitionInternal(cc, restrictions);
			if (ret != null)
				return ret;	
		}
		
		return null;
	}


	@SuppressWarnings("unchecked")
	public Collection<IRelationship> getRelationships() throws ThinklabException {
		ArrayList<IRelationship> ret = new ArrayList<IRelationship>();
		Collection<RDFProperty> props = concept.getRDFProperties();
		for (RDFProperty p : props) {
			Collection<IValue> rrel = ThinklabOWLManager.get().translateRelationship(concept, p);
			for (IValue v : rrel) {
				ret.add(new Relationship(new Property(p),v));
			}
		}
		return ret;		
	}

	public Collection<IRelationship> getRelationships(String property) throws ThinklabException {
		ArrayList<IRelationship> ret = new ArrayList<IRelationship>();
		IProperty p = KnowledgeManager.get().requireProperty(property);
		Collection<IValue> rrel = 
			ThinklabOWLManager.get().translateRelationship(concept, ((Property)p).property);
		for (IValue v : rrel) {
			ret.add(new Relationship(p,v));
		}
		return ret;
	}

	public int getNumberOfRelationships(String property) throws ThinklabException {
		IProperty p = KnowledgeManager.get().requireProperty(property);
		return ThinklabOWLManager.getNRelationships(concept, ((Property)p).property);
	}

	public IValue get(String property) throws ThinklabException {
		Collection<IRelationship> cr = getRelationshipsTransitive(property);
		if (cr.size() == 1)
			return cr.iterator().next().getValue();
		/* TODO return a ListValue if more than one result */
		return null;
	}

	public Collection<IRelationship> getRelationshipsTransitive(String property) throws ThinklabException {

		ArrayList<IRelationship> ret = new ArrayList<IRelationship>();
		IProperty p = KnowledgeManager.get().requireProperty(property);

		Collection<IProperty> pp = p.getParents();
		pp.add(p);

		for (IProperty prop : pp) {
			Collection<IValue> rrel = 
				ThinklabOWLManager.get().translateRelationship(concept, ((Property)prop).property);

			for (IValue v : rrel) {
				ret.add(new Relationship(prop,v));
			}
		}
		return ret;	
	}

	public Collection<IConcept> getPropertyRange(IProperty property) throws ThinklabException {
		
		ArrayList<IConcept> ret = new ArrayList<IConcept>();
		ArrayList<OWLRestriction> rs = new ArrayList<OWLRestriction>();
		getDefinitionInternal(this, rs);
		boolean more = true;
		
		for (OWLRestriction r : rs) {

			IProperty p = new Property(r.getOnProperty());
			
			// a typical case of "scare of equals"
			if (!p.toString().equals(property.toString()))
				continue;
			
			if (r instanceof OWLAllValuesFrom) {
				ret.add(new Concept((OWLClass) ((OWLAllValuesFrom)r).getAllValuesFrom()));
				more = false;
			} else if (r instanceof OWLSomeValuesFrom) {
				ret.add(new Concept((OWLClass) ((OWLSomeValuesFrom)r).getSomeValuesFrom()));
			} 
		}
		
		// add unrestricted types unless we had an all values from restriction
		if (ret.size() == 0 || more) {
			for (IConcept c : property.getRange()) {
				ret.add(c);
			}
		}
		
		return ret;
	}

	public int getMaxCardinality(IProperty property) {
		return ((DefaultOWLNamedClass)concept).
			getMaxCardinality(((Property)property).property);
	}

	public int getMinCardinality(IProperty property) {
		return ((DefaultOWLNamedClass)concept).
			getMinCardinality(((Property)property).property);
	}

	public Collection<IProperty> getAnnotationProperties() {
		// TODO Auto-generated method stub
		return null;
	}

}




