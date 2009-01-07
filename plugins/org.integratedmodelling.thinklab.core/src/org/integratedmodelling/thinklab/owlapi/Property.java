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
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.semanticweb.owl.inference.OWLReasonerAdapter;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLPropertyExpression;

/**
 * @author Ioannis N. Athanasiadis
 *
 */
public class Property extends Knowledge implements IProperty {


	/**
	 * @param p
	 */
	public Property(OWLObjectProperty p) {
		super(p,OWLType.OBJECTPROPERTY);
	}
	
	public Property(OWLDataProperty p) {
		super(p,OWLType.DATAPROPERTY);
	}

	Property(OWLPropertyExpression p) {
		super(
			( p instanceof OWLObjectProperty ? (OWLObjectProperty)p : (OWLDataProperty)p),
			( p instanceof OWLObjectProperty ? OWLType.OBJECTPROPERTY : OWLType.DATAPROPERTY));
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getAllChildren()
	 */
	public Collection<IProperty> getAllChildren() {
		
		Set<IProperty> ret = new HashSet<IProperty>();

		for (IProperty c : getChildren()) {
			
			ret.add(c);
			for (IProperty p : c.getChildren()) {
				ret.addAll(p.getAllChildren());
			}
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getAllParents()
	 */
	public Collection<IProperty> getAllParents() {
		
		Set<IProperty> ret = new HashSet<IProperty>();
		
		if (FileKnowledgeRepository.KR.propertyReasoner != null) {
			
			try {
				if (entity.isOWLObjectProperty()) {
					Set<Set<OWLObjectProperty>> parents = FileKnowledgeRepository.KR.propertyReasoner
						.getAncestorProperties(entity.asOWLObjectProperty());
					Set<OWLObjectProperty> subClses = OWLReasonerAdapter
						.flattenSetOfSets(parents);
					for (OWLObjectProperty cls : subClses) {
						ret.add(new Property(cls));
					}
				} else if (entity.isOWLDataProperty()) {
					Set<Set<OWLDataProperty>> parents = FileKnowledgeRepository.KR.propertyReasoner
						.getAncestorProperties(entity.asOWLDataProperty());
					Set<OWLDataProperty> subClses = OWLReasonerAdapter
						.flattenSetOfSets(parents);
					for (OWLDataProperty cls : subClses) {
						ret.add(new Property(cls));
					}
				}
				return ret;
				
			} catch (OWLException e) {
				// just continue to dumb method
			}

		} else {
			
			for (IProperty c : getParents()) {
				ret.add(c);
				ret.addAll(c.getAllParents());
			}
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getChildren()
	 */
	public Collection<IProperty> getChildren() {
		Set<IProperty> ret = new HashSet<IProperty>();
		
		Set<OWLOntology> onts = 
			FileKnowledgeRepository.get().manager.getOntologies();
		
		if (entity.isOWLDataProperty()) {
			for (OWLOntology o : onts)  {
				for (OWLDataPropertyExpression p : 
						entity.asOWLDataProperty().getSubProperties(o)) {
					ret.add(new Property(p));
				}
			}
		} else if (entity.isOWLObjectProperty()) {
			for (OWLOntology o : onts)  {
				for (OWLObjectPropertyExpression p : 
						entity.asOWLObjectProperty().getSubProperties(o)) {
					ret.add(new Property(p));
				}
			}
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getDomain()
	 */
	public Collection<IConcept> getDomain() {

		Set<IConcept> ret = new HashSet<IConcept>();
		if (entity.isOWLDataProperty()) {
			for (OWLDescription c : entity.asOWLDataProperty().getDomains(
					FileKnowledgeRepository.get().manager.getOntologies())) {
				ret.add(new Concept(c.asOWLClass()));
			}
		} else if (entity.isOWLObjectProperty()) {
			for (OWLDescription c : entity.asOWLObjectProperty().getDomains(
					FileKnowledgeRepository.get().manager.getOntologies())) {
				ret.add(new Concept(c.asOWLClass()));
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getInverseProperty()
	 */
	public IProperty getInverseProperty() {

		Property ret = null;
		
		if (entity.isOWLObjectProperty()) {
			
			Set<OWLObjectPropertyExpression> dio = 
				entity.asOWLObjectProperty().getInverses(getOntology());
			
			if (dio.size() > 1) 
				Thinklab.get().logger().error(
						"taking the inverse of property " + 
						this + 
						", which has multiple inverses");
			
			if (dio.size() > 0) {
				ret = new Property(dio.iterator().next());
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getParent()
	 */
	public IProperty getParent()
			throws ThinklabException {

		Collection<IProperty> pars = getParents();
	
		if (pars.size() > 1)
			Thinklab.get().logger().error("asking for single parent of multiple-inherited property " + this);

		return pars.size() == 0 ? null : pars.iterator().next();
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getParents()
	 */
	public Collection<IProperty> getParents() {
		
		Set<IProperty> ret = new HashSet<IProperty>();
		Set<OWLOntology> onts = 
			FileKnowledgeRepository.get().manager.getOntologies();

		/*
		 * TODO use reasoner as appropriate
		 */
		
		if (entity.isOWLDataProperty()) {
			for (OWLOntology o : onts)  {
				for (OWLDataPropertyExpression p : 
						entity.asOWLDataProperty().getSuperProperties(o)) {
					ret.add(new Property(p));
				}
			}
		} else if (entity.isOWLObjectProperty()) {
			for (OWLOntology o : onts)  {
				for (OWLObjectPropertyExpression p : 
						entity.asOWLObjectProperty().getSuperProperties(o)) {
					ret.add(new Property(p));
				}
			}
		}

		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getRange()
	 */
	public Collection<IConcept> getRange() {
		
		Set<IConcept> ret = new HashSet<IConcept>();
		if (entity.isOWLDataProperty()) {
			
			for (OWLDataRange c : entity.asOWLDataProperty().getRanges(
					FileKnowledgeRepository.get().manager.getOntologies())) {

				if (c.isDataType()) {
					OWLDataType dtype = (OWLDataType) c;
					String tltype = KnowledgeManager.get().getXSDMapping(dtype.getURI().toString());
					if (tltype != null) {
						try {
							ret.add(KnowledgeManager.get().requireConcept(tltype));
						} catch (Exception e) {
							// just don't add it
						}
					}
				}
			}
		} else if (entity.isOWLObjectProperty()) {
			for (OWLDescription c : entity.asOWLObjectProperty().getRanges(
					FileKnowledgeRepository.get().manager.getOntologies())) {
				if (!c.isAnonymous())
					ret.add(new Concept(c.asOWLClass()));
			}
		}
		return ret;
		
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#isAbstract()
	 */
	public boolean isAbstract() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#isAnnotation()
	 */
	public boolean isAnnotation() {
		// TODO don't know how to do this, or if it even makes sense in OWLAPI
		return false;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#isClassification()
	 */
	public boolean isClassification() {
		try {
			return is(KnowledgeManager.get().getClassificationProperty());
		} catch (ThinklabException e) {
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#isFunctional()
	 */
	public boolean isFunctional() {

		return entity.isOWLDataProperty() ?
				entity.asOWLDataProperty().isFunctional(getOntology()) :
				entity.asOWLObjectProperty().isFunctional(getOntology());
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#isLiteralProperty()
	 */
	public boolean isLiteralProperty() {
		try {
			return entity.isOWLDataProperty() ||
				   is(KnowledgeManager.get().getReifiedLiteralProperty());
		} catch (ThinklabException e) {
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#isObjectProperty()
	 */
	public boolean isObjectProperty() {
		return entity.isOWLObjectProperty();
	}
	
	public boolean is(Property p){
		
		/*
		 * TODO use reasoner as appropriate
		 */
		return p.equals(this) || getAllParents().contains(p);
	}
}
