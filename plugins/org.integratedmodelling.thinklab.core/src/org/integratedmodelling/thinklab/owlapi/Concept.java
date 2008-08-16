/**
 * Created on Mar 3, 2008 
 * By Ioannis N. Athanasiadis
 *
 * Copyright 2007 Dalle Molle Institute for Artificial Intelligence
 * 
 * Licensed under the GNU General Public License.
 *
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.integratedmodelling.thinklab.owlapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IOntology;
import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.integratedmodelling.thinklab.interfaces.IRelationship;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.utils.LogicalConnector;
import org.integratedmodelling.utils.Quantifier;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAnnotationAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
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
			return (getAnnotationProperties().contains(KnowledgeManager.get().getAbstractProperty()));
		} catch (Exception e) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getType()
	 */
	public IConcept getType() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * FIXME for now the only time this returns anything different from getInstances()
	 *  is when a reasoner is connected - check what we want with these two, and
	 *  possibly remove one method.
	 *  
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getAllInstances()
	 */
	public Collection<IInstance> getAllInstances() {

		Set<IInstance> ret = new HashSet<IInstance>();
		
		try {
			if (FileKnowledgeRepository.get().instanceReasoner != null) {
				for (OWLIndividual ind : 
						FileKnowledgeRepository.get().instanceReasoner.
							getIndividuals(this.entity.asOWLClass(), false)) {
					ret.add(new Instance(ind));
				}
			}
		} catch (OWLReasonerException e) {
			// just proceed with the dumb method
		}
 		
		
		Set<OWLOntology> ontologies = FileKnowledgeRepository.KR.manager.getOntologies();
		for (OWLIndividual ind : this.entity.asOWLClass().getIndividuals(ontologies)) {
			ret.add(new Instance(ind));
		}
		
		return ret;
	}

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
		Set<OWLDescription> set = ((OWLClass) this.entity)
				.getSuperClasses(FileKnowledgeRepository.KR.manager
						.getOntologies());
		
		for (OWLDescription s : set) {
			if (!(s.isAnonymous() || s.isOWLNothing()))
				concepts.add(new Concept(s.asOWLClass()));
		}
		
		// OWLAPI doesn't do this - only add owl:Thing if this is its direct subclass, i.e. has no 
		// parents in OWLAPI.
		if (concepts.isEmpty() && !FileKnowledgeRepository.KR.getRootConceptType().equals(this))
			concepts.add(FileKnowledgeRepository.KR.getRootConceptType());
		
		return concepts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getAllParents()
	 */
	public Collection<IConcept> getAllParents() {

		Set<IConcept> concepts = new HashSet<IConcept>();
		
		if (FileKnowledgeRepository.KR.classReasoner != null) {
			
			try {
				Set<Set<OWLClass>> parents = FileKnowledgeRepository.KR.classReasoner
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
				for (IConcept p : c.getParents()) {
					concepts.addAll(p.getAllParents());
				}
 				
			}
		}

		// OWLAPI doesn't do this
		concepts.add(FileKnowledgeRepository.KR.getRootConceptType());

		return concepts;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getChildren()
	 */
	public Collection<IConcept> getChildren() {
		Set<IConcept> concepts = new HashSet<IConcept>();
		Set<OWLDescription> set = ((OWLClass) this.entity)
				.getSubClasses(FileKnowledgeRepository.KR.manager
						.getOntologies());
		for (OWLDescription s : set) {
			if (!(s.isAnonymous() || s.isOWLNothing() || s.isOWLThing()))
				concepts.add(new Concept(s.asOWLClass()));
		}
		if (set.isEmpty() && ((OWLClass) entity).isOWLThing()) {
			for (IOntology onto : FileKnowledgeRepository.KR.ontologies
					.values()) {
				concepts.addAll(onto.getConcepts());
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
		Set<IProperty> props = new HashSet<IProperty>() ;
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
		Set<OWLOntology> ontologies = FileKnowledgeRepository.KR.manager
				.getOntologies();
		Set<IProperty> properties = new HashSet<IProperty>();
		for (OWLOntology ontology : ontologies) {
			for ( OWLAnnotationAxiom op : ontology.getAnnotationAxioms()) {
				if(op.getSubject().equals(this.entity)){
					OWLAnnotation ann = op.getAnnotation();
					if(ann.isAnnotationByConstant()){
//					TODO	
					} else {
//					TODO
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
		for(IProperty prop: props) 
			psets.add(prop.getChildren());
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
		Set<OWLOntology> ontologies = FileKnowledgeRepository.KR.manager.getOntologies();
		
		Set<IProperty> properties = new HashSet<IProperty>();
		for (OWLOntology ontology : ontologies) {
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
		return properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getInstances()
	 */
	public Collection<IInstance> getInstances() {
		
		Set<IInstance> ret = new HashSet<IInstance>();
		
		try {
			if (FileKnowledgeRepository.get().instanceReasoner != null) {
				for (OWLIndividual ind : 
						FileKnowledgeRepository.get().instanceReasoner.
							getIndividuals(this.entity.asOWLClass(), true)) {
					ret.add(new Instance(ind));
				}
			}
		} catch (OWLReasonerException e) {
			// just proceed with the dumb method
		}
 		
		
		Set<OWLOntology> ontologies = FileKnowledgeRepository.KR.manager.getOntologies();
		for (OWLIndividual ind : this.entity.asOWLClass().getIndividuals(ontologies)) {
			ret.add(new Instance(ind));
		}
		
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getLeastGeneralCommonConcept(org.integratedmodelling.thinklab.interfaces.IConcept)
	 */
	public IConcept getLeastGeneralCommonConcept(IConcept otherConcept) {
		// should we use the class tree?
		IConcept ret = null;
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
		
		for (OWLRestriction r : OWLAPI.getRestrictions(this, true)) {
			
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
		
		int ret = 0;
		
		for (OWLRestriction r : OWLAPI.getRestrictions(this, true)) {
			
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
	public int getNumberOfProperties(String property) {
		return getProperties().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getPropertyRange(org.integratedmodelling.thinklab.interfaces.IProperty)
	 */
	public Collection<IConcept> getPropertyRange(IProperty property) {
		
		ArrayList<IConcept> ret = new ArrayList<IConcept>();
	
		for (OWLRestriction r : OWLAPI.getRestrictions(this, true)) {
			
			if (r instanceof OWLObjectAllRestriction &&
				r.getProperty().equals(((Property)property).entity)) {
			
				ret.add(new Concept(((OWLObjectAllRestriction)r).getFiller().asOWLClass()));
			} else if (r instanceof OWLObjectSomeRestriction &&
				r.getProperty().equals(((Property)property).entity)) {
			
				ret.add(new Concept(((OWLObjectSomeRestriction)r).getFiller().asOWLClass()));
			}
		}
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<IRelationship> getRelationships() throws ThinklabException {
		ArrayList<IRelationship> ret = new ArrayList<IRelationship>();
//		Collection<RDFProperty> props = entity.getRDFProperties();
//		for (RDFProperty p : props) {
//			Collection<IValue> rrel = ThinklabOWLManager.get().translateRelationship(concept, p);
//			for (IValue v : rrel) {
//				ret.add(new Relationship(new Property(p),v));
//			}
//		}
		Thinklab.get().logger().warn("deprecated getRelationships() called on Concept; returning empty result");

		return ret;		
	}

	public Collection<IRelationship> getRelationships(String property) throws ThinklabException {
		ArrayList<IRelationship> ret = new ArrayList<IRelationship>();
		IProperty p = KnowledgeManager.get().requireProperty(property);
//		Collection<IValue> rrel = 
//			ThinklabOWLManager.get().translateRelationship(concept, ((Property)p).property);
//		for (IValue v : rrel) {
//			ret.add(new Relationship(p,v));
//		}
		Thinklab.get().logger().warn("deprecated getRelationships() called on Concept; returning empty result");

		return ret;
	}

	public int getNumberOfRelationships(String property) throws ThinklabException {
		return getProperties().size();
	}

	public IValue get(String property) throws ThinklabException {
		
		Thinklab.get().logger().warn("deprecated get() called on Concept; returning empty result");

		Collection<IRelationship> cr = getRelationshipsTransitive(property);
		if (cr.size() == 1)
			return cr.iterator().next().getValue();
		/* TODO return a ListValue if more than one result */
		return null;
	}

	public Collection<IRelationship> getRelationshipsTransitive(String property) throws ThinklabException {

		ArrayList<IRelationship> ret = new ArrayList<IRelationship>();
		
		Thinklab.get().logger().warn("deprecated getRelationshipsTransitive called on Concept; returning empty result");
//		IProperty p = KnowledgeManager.get().requireProperty(property);
//
//		
//		
//		Collection<IProperty> pp = p.getParents();
//		pp.add(p);
//
//		for (IProperty prop : pp) {
//			Collection<IValue> rrel = 
//				ThinklabOWLManager.get().translateRelationship(entity, ((Property)prop).entity);
//
//			for (IValue v : rrel) {
//				ret.add(new Relationship(prop,v));
//			}
//		}
		return ret;	
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getRestrictions()
	 */
	public Constraint getRestrictions() throws ThinklabException {

		/*
		 * This accumulates all restrictions from parents as well.
		 */		
		Collection<OWLRestriction> rs = OWLAPI.getRestrictions(this,true);

		Constraint ret = new Constraint(this);
		
			for (OWLRestriction r : rs) {

				IProperty p = new Property(r.getProperty());
				Quantifier q = null;
				
				if (r instanceof OWLDataAllRestriction ||
					r instanceof OWLObjectAllRestriction) {

					q = new Quantifier("all");

					IConcept c = 
						ThinklabOWLManager.get().getRestrictionFiller(r);
					
					if (c != null)
						ret.restrict(new Restriction(q, p, new Constraint(c)));

				} else if (r instanceof OWLDataSomeRestriction || 
						   r instanceof OWLObjectSomeRestriction) {

					q = new Quantifier("any");
					
					IConcept c = 
						ThinklabOWLManager.get().getRestrictionFiller(r);
					
					if (c != null)
						ret.restrict(new Restriction(q, p, new Constraint(c)));

				} else if (r instanceof OWLDataExactCardinalityRestriction) {
											
					int card = ((OWLDataExactCardinalityRestriction)r).getCardinality();
					q = new Quantifier(Integer.toString(card));
					ret.restrict(new Restriction(q, p));

				}  else if (r instanceof OWLObjectExactCardinalityRestriction) {
											
					int card = ((OWLObjectExactCardinalityRestriction)r).getCardinality();
					q = new Quantifier(Integer.toString(card));
					ret.restrict(new Restriction(q, p));

				} else if (r instanceof OWLDataMinCardinalityRestriction) {

					int card = ((OWLDataMinCardinalityRestriction)r).getCardinality();			
					q = new Quantifier(card+":");
					ret.restrict(new Restriction(q, p));

				} else if (r instanceof OWLObjectMinCardinalityRestriction) {
					
					int card = ((OWLObjectMinCardinalityRestriction)r).getCardinality();
					q = new Quantifier(card+":");
					ret.restrict(new Restriction(q, p));

				} else if (r instanceof OWLDataMaxCardinalityRestriction) {

					int card = ((OWLDataMaxCardinalityRestriction)r).getCardinality();
					q = new Quantifier(":" + card);
					ret.restrict(new Restriction(q, p));

				} else if (r instanceof OWLObjectMaxCardinalityRestriction) {
					
					int card = ((OWLObjectMaxCardinalityRestriction)r).getCardinality();
					q = new Quantifier(":" + card);
					ret.restrict(new Restriction(q, p));
					
				} else if (r instanceof OWLDataValueRestriction) {

					ret.restrict(
							new Restriction(
									new Quantifier("all"),
									p, 
									"=", 
									(((OWLDataValueRestriction)r).getValue().toString())));
					
				}  else if (r instanceof OWLObjectValueRestriction) {

//										ret.restrict(
//												new Restriction(
//														new Quantifier("all"),
//														p, 
//														"=", 
//														((OWLHasValue)r).getHasValue().toString()));
									}
				/*
				 * TODO there are more restrictions in OWL > 1.0; must check which ones
				 * go into a constraint
				 */
			}
		
		return ret;

		
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#get(java.lang.String)
//	 */
//	public IValue get(String property) throws ThinklabException {
//		// TODO this must catch valueOf restrictions on the property on
//		// this concept or the ancestors.
//		return null;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getNumberOfRelationships(java.lang.String)
//	 */
//	public int getNumberOfRelationships(String property)
//			throws ThinklabException {
//		return getRelationships().size();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationships()
//	 */
//	public Collection<IRelationship> getRelationships()
//			throws ThinklabException {
//		ArrayList<IRelationship> ret = new ArrayList<IRelationship>();
//		return ret;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationships(java.lang.String)
//	 */
//	public Collection<IRelationship> getRelationships(String property)
//			throws ThinklabException {
//		ArrayList<IRelationship> ret = new ArrayList<IRelationship>();
//		return ret;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationshipsTransitive(java.lang.String)
//	 */
//	public Collection<IRelationship> getRelationshipsTransitive(String property)
//			throws ThinklabException {
//		ArrayList<IRelationship> ret = new ArrayList<IRelationship>();
//		return ret;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getDefinition()
	 */
	public Constraint getDefinition() throws ThinklabException {
		
		ArrayList<OWLRestriction> rs = new ArrayList<OWLRestriction>();
		
		Constraint ret = getDefinitionInternal(this, rs);

		if (rs != null)
			for (OWLRestriction r : rs) {

				IProperty p = new Property(r.getProperty());
				Quantifier q = null;
				
				if (r instanceof OWLDataAllRestriction ||
					r instanceof OWLObjectAllRestriction) {

					q = new Quantifier("all");

					IConcept c = 
						ThinklabOWLManager.get().getRestrictionFiller(r);
					
					if (c != null)
						ret.restrict(new Restriction(q, p, new Constraint(c)));

				} else if (r instanceof OWLDataSomeRestriction || 
						   r instanceof OWLObjectSomeRestriction) {

					q = new Quantifier("any");
					
					IConcept c = 
						ThinklabOWLManager.get().getRestrictionFiller(r);
					
					if (c != null)
						ret.restrict(new Restriction(q, p, new Constraint(c)));

				} else if (r instanceof OWLDataExactCardinalityRestriction) {
											
					int card = ((OWLDataExactCardinalityRestriction)r).getCardinality();
					q = new Quantifier(Integer.toString(card));
					ret.restrict(new Restriction(q, p));

				}  else if (r instanceof OWLObjectExactCardinalityRestriction) {
											
					int card = ((OWLObjectExactCardinalityRestriction)r).getCardinality();
					q = new Quantifier(Integer.toString(card));
					ret.restrict(new Restriction(q, p));

				} else if (r instanceof OWLDataMinCardinalityRestriction) {

					int card = ((OWLDataMinCardinalityRestriction)r).getCardinality();			
					q = new Quantifier(card+":");
					ret.restrict(new Restriction(q, p));

				} else if (r instanceof OWLObjectMinCardinalityRestriction) {
					
					int card = ((OWLObjectMinCardinalityRestriction)r).getCardinality();
					q = new Quantifier(card+":");
					ret.restrict(new Restriction(q, p));

				} else if (r instanceof OWLDataMaxCardinalityRestriction) {

					int card = ((OWLDataMaxCardinalityRestriction)r).getCardinality();
					q = new Quantifier(":" + card);
					ret.restrict(new Restriction(q, p));

				} else if (r instanceof OWLObjectMaxCardinalityRestriction) {
					
					int card = ((OWLObjectMaxCardinalityRestriction)r).getCardinality();
					q = new Quantifier(":" + card);
					ret.restrict(new Restriction(q, p));
					
				} else if (r instanceof OWLDataValueRestriction) {

					ret.restrict(
							new Restriction(
									new Quantifier("all"),
									p, 
									"=", 
									(((OWLDataValueRestriction)r).getValue().toString())));
					
				}  else if (r instanceof OWLObjectValueRestriction) {

//										ret.restrict(
//												new Restriction(
//														new Quantifier("all"),
//														p, 
//														"=", 
//														((OWLHasValue)r).getHasValue().toString()));
									}
				/*
				 * TODO there are more restrictions in OWL > 1.0; must check which ones
				 * go into a constraint
				 */
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
		
		Collection<OWLRestriction> rs = OWLAPI.getRestrictions((Concept)c, false);
				
		boolean found = false;
		if (rs != null) {
			for (OWLRestriction r : rs) {

				IProperty p = new Property(r.getProperty());
			
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



	protected boolean is(Concept c) {
		
		if (c.equals(this))
			return true;
		
		try {
			if (FileKnowledgeRepository.get().classReasoner != null) {
				return FileKnowledgeRepository.get().classReasoner.
					isSubClassOf(this.entity.asOWLClass(), c.entity.asOWLClass());
			}
		} catch (OWLReasonerException e) {
			// just proceed with the dumb method
		}
 		
		Collection<IConcept> collection = getAllParents();
		collection.add(this);
		return collection.contains(c);
	}
}
