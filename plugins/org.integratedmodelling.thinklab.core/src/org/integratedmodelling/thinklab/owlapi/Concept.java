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
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAnnotationAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;

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
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getAllInstances()
	 */
	public Collection<IInstance> getAllInstances() {

		
		// TODO easy
		
		return null;
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
		if (FileKnowledgeRepository.KR.reasonerConnected()) {
			try {
				Set<Set<OWLClass>> parents = FileKnowledgeRepository.KR.reasoner
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
		for(IProperty prop:props)
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
		Set<OWLOntology> ontologies = FileKnowledgeRepository.KR.manager
				.getOntologies();
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
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getMinCardinality(org.integratedmodelling.thinklab.interfaces.IProperty)
	 */
	public int getMinCardinality(IProperty property) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getNumberOfProperties(java.lang.String)
	 */
	public int getNumberOfProperties(String property) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getPropertyRange(org.integratedmodelling.thinklab.interfaces.IProperty)
	 */
	public Collection<IConcept> getPropertyRange(IProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getRestrictions()
	 */
	public Constraint getRestrictions() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationships()
	 */
	public Collection<IRelationship> getRelationships()
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationships(java.lang.String)
	 */
	public Collection<IRelationship> getRelationships(String property)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject#getRelationshipsTransitive(java.lang.String)
	 */
	public Collection<IRelationship> getRelationshipsTransitive(String property)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.thinklab.interfaces.IConcept#getDefinition()
	 */
	public Constraint getDefinition() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	protected boolean is(Concept c) {
		Collection<IConcept> collection = getAllParents();
		collection.add(this);
		return collection.contains(c);
	}
}
