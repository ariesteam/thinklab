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
import org.integratedmodelling.thinklab.constraint.Constraint;
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
import org.semanticweb.owl.model.OWLCardinalityRestriction;
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

import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;

/**
 * @author Ioannis N. Athanasiadis
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
		}catch (Exception e) {
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
		// Need to add owl:Thing
		concepts.add(FileKnowledgeRepository.KR.getRootConceptType());
		Set<OWLDescription> set = ((OWLClass) this.entity)
				.getSuperClasses(FileKnowledgeRepository.KR.manager
						.getOntologies());
		for (OWLDescription s : set) {
			if (!(s.isAnonymous() || s.isOWLNothing()))
				concepts.add(new Concept(s.asOWLClass()));
		}
		return concepts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getAllParents()
	 */
	public Collection<IConcept> getAllParents() {
		if (FileKnowledgeRepository.KR.classReasoner != null) {
			try {
				Set<Set<OWLClass>> parents = FileKnowledgeRepository.KR.classReasoner
						.getAncestorClasses((OWLClass) entity);
				Set<IConcept> concepts = new HashSet<IConcept>();
				Set<OWLClass> subClses = OWLReasonerAdapter
						.flattenSetOfSets(parents);
				for (OWLClass cls : subClses) {
					concepts.add(new Concept(cls));
				}
				return concepts;
			} catch (OWLException e) {
				e.printStackTrace();
				return getAllParents();
			}

		} else
			return getParents();
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
		for(IProperty prop: props)
			props.addAll(prop.getChildren());
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
//					IConcept c = new Concept((OWLClass) ((OWLAllValuesFrom)r).getAllValuesFrom());
//					ret.restrict(new Restriction(q, p, new Constraint(c)));
//
				} else if (r instanceof OWLDataSomeRestriction || 
						   r instanceof OWLObjectSomeRestriction) {

					q = new Quantifier("any");
//					IConcept c = new Concept((OWLClass) ((OWLSomeValuesFrom)r).getSomeValuesFrom());
//					ret.restrict(new Restriction(q, p, new Constraint(c)));
//
				} else if (r instanceof OWLDataExactCardinalityRestriction ||
						   r instanceof OWLObjectExactCardinalityRestriction) {
//
//					int card = ((OWLCardinality)r).getCardinality();
//					q = new Quantifier(Integer.toString(card));
//					ret.restrict(new Restriction(q, p));
//
				} else if (r instanceof OWLDataMinCardinalityRestriction ||
						   r instanceof OWLObjectMinCardinalityRestriction) {
//
//					int card = ((OWLMinCardinality)r).getCardinality();				
//					q = new Quantifier(card+":");
//					ret.restrict(new Restriction(q, p));
//
				} else if (r instanceof OWLDataMaxCardinalityRestriction ||
						   r instanceof OWLObjectMinCardinalityRestriction) {
//
//					int card = ((OWLMaxCardinality)r).getCardinality();
//					q = new Quantifier(":" + card);
//					ret.restrict(new Restriction(q, p));
//
				} else if (r instanceof OWLDataValueRestriction ||
						   r instanceof OWLObjectValueRestriction) {
//
//					ret.restrict(
//							new Restriction(
//									new Quantifier("all"),
//									p, 
//									"=", 
//									((OWLHasValue)r).getHasValue().toString()));
				}
				/*
				 * TODO there are more restrictions in OWL > 1.0; must check which ones
				 * go into a constraint
				 */
			}
		
		return ret;

		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#get(java.lang.String)
	 */
	public IValue get(String property) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getNumberOfRelationships(java.lang.String)
	 */
	public int getNumberOfRelationships(String property)
			throws ThinklabException {
		return getRelationships().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationships()
	 */
	public Collection<IRelationship> getRelationships()
			throws ThinklabException {
		ArrayList<IRelationship> ret = new ArrayList<IRelationship>();
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationships(java.lang.String)
	 */
	public Collection<IRelationship> getRelationships(String property)
			throws ThinklabException {
		ArrayList<IRelationship> ret = new ArrayList<IRelationship>();
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationshipsTransitive(java.lang.String)
	 */
	public Collection<IRelationship> getRelationshipsTransitive(String property)
			throws ThinklabException {
		ArrayList<IRelationship> ret = new ArrayList<IRelationship>();
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getDefinition()
	 */
	public Constraint getDefinition() throws ThinklabException {
		
		ArrayList<OWLRestriction> rs = new ArrayList<OWLRestriction>();
		
		Constraint ret = getDefinitionInternal(this, rs);
		
//		if (rs != null)
//			for (OWLRestriction r : rs) {
//
//				IProperty p = new Property(r.getOnProperty());
//				Quantifier q = null;
//
//				if (r instanceof OWLAllValuesFrom) {
//
//					q = new Quantifier("all");
//					IConcept c = new Concept((OWLClass) ((OWLAllValuesFrom)r).getAllValuesFrom());
//					ret.restrict(new Restriction(q, p, new Constraint(c)));
//
//				} else if (r instanceof OWLSomeValuesFrom) {
//
//					q = new Quantifier("any");
//					IConcept c = new Concept((OWLClass) ((OWLSomeValuesFrom)r).getSomeValuesFrom());
//					ret.restrict(new Restriction(q, p, new Constraint(c)));
//
//				} else if (r instanceof OWLCardinality) {
//
//					int card = ((OWLCardinality)r).getCardinality();
//					q = new Quantifier(Integer.toString(card));
//					ret.restrict(new Restriction(q, p));
//
//				} else if (r instanceof OWLMinCardinality) {
//
//					int card = ((OWLMinCardinality)r).getCardinality();				
//					q = new Quantifier(card+":");
//					ret.restrict(new Restriction(q, p));
//
//				} else if (r instanceof OWLMaxCardinality) {
//
//					int card = ((OWLMaxCardinality)r).getCardinality();
//					q = new Quantifier(":" + card);
//					ret.restrict(new Restriction(q, p));
//
//				} else if (r instanceof OWLHasValue) {
//
//					ret.restrict(
//							new Restriction(
//									new Quantifier("all"),
//									p, 
//									"=", 
//									((OWLHasValue)r).getHasValue().toString()));
//				}
//			}
		
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
		
//		// DefaultRDFSNamedClass gets here too.
//		if (!(((Concept)c).concept instanceof DefaultOWLNamedClass)) {
//			return new Constraint(c);
//		}
		
//		Collection<OWLRestriction> rs = 
//			((DefaultOWLNamedClass)(((Concept)c).concept)).getRestrictions(false);
				
		boolean found = false;
//		if (rs != null) {
//			for (OWLRestriction r : rs) {
//
//				IProperty p = new Property(r.getOnProperty());
//				
//				if (p.getDomain().equals(c))
//					continue;
//				
//				restrictions.add(r);
//				found = true;
//			}
//		}
		
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
