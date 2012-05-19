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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
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

		synchronized (entity) {
			if (KR().getPropertyReasoner() != null) {

				try {
					if (entity.isOWLObjectProperty()) {
						Set<Set<OWLObjectProperty>> parents = 
							KR().getPropertyReasoner()
								.getAncestorProperties(entity
										.asOWLObjectProperty());
						Set<OWLObjectProperty> subClses = OWLReasonerAdapter
								.flattenSetOfSets(parents);
						for (OWLObjectProperty cls : subClses) {
							ret.add(new Property(cls));
						}
					} else if (entity.isOWLDataProperty()) {
						Set<Set<OWLDataProperty>> parents = 
							KR().getPropertyReasoner()
								.getAncestorProperties(entity
										.asOWLDataProperty());
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
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getChildren()
	 */
	public Collection<IProperty> getChildren() {
		Set<IProperty> ret = new HashSet<IProperty>();
		
		Set<OWLOntology> onts = 
			KR().manager.getOntologies();
		
		if (entity.isOWLDataProperty()) {
			for (OWLOntology o : onts)  {
				synchronized (this.entity) {
					for (OWLDataPropertyExpression p : 
						entity.asOWLDataProperty().getSubProperties(o)) {
						ret.add(new Property(p));
					}
				}
			}
		} else if (entity.isOWLObjectProperty()) {
			for (OWLOntology o : onts)  {
				synchronized (this.entity) {
					for (OWLObjectPropertyExpression p : 
							entity.asOWLObjectProperty().getSubProperties(o)) {
						ret.add(new Property(p));
					}
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
		synchronized (this.entity) {
			if (entity.isOWLDataProperty()) {
				for (OWLDescription c : entity.asOWLDataProperty().getDomains(
						KR().manager.getOntologies())) {
					ret.add(new Concept(c.asOWLClass()));
				}
			} else if (entity.isOWLObjectProperty()) {
				for (OWLDescription c : entity.asOWLObjectProperty().getDomains(
						KR().manager.getOntologies())) {
					ret.add(new Concept(c.asOWLClass()));
				}
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getInverseProperty()
	 */
	public IProperty getInverseProperty() {

		Property ret = null;
		
		synchronized (this.entity) {
			if (entity.isOWLObjectProperty()) {
			
				Set<OWLObjectPropertyExpression> dio = 
					entity.asOWLObjectProperty().getInverses(getOWLOntology());
			
				if (dio.size() > 1) 
					Thinklab.get().logger().error(
							"taking the inverse of property " + 
							this	 + 
							", which has multiple inverses");
			
				if (dio.size() > 0) {
					ret = new Property(dio.iterator().next());
				}
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
			KR().manager.getOntologies();

		/*
		 * TODO use reasoner as appropriate
		 */
		synchronized (this.entity) {
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
		}

		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#getRange()
	 */
	public Collection<IConcept> getRange() {

		Set<IConcept> ret = new HashSet<IConcept>();
		synchronized (this.entity) {
			if (entity.isOWLDataProperty()) {

				for (OWLDataRange c : entity.asOWLDataProperty().getRanges(
						KR().manager.getOntologies())) {

					if (c.isDataType()) {
						OWLDataType dtype = (OWLDataType) c;
						// FIXME! complete this
						IConcept tltype = Thinklab.get().getXSDMapping(dtype.getURI().toString());
						if (tltype != null) {
							ret.add(tltype);
						}
					}
				}
			} else if (entity.isOWLObjectProperty()) {
				for (OWLDescription c : entity.asOWLObjectProperty().getRanges(
						KR().manager.getOntologies())) {
					if (!c.isAnonymous())
						ret.add(new Concept(c.asOWLClass()));
				}
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
		return is(Thinklab.CLASSIFICATION_PROPERTY);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#isFunctional()
	 */
	public boolean isFunctional() {

		return entity.isOWLDataProperty() ?
				entity.asOWLDataProperty().isFunctional(getOWLOntology()) :
				entity.asOWLObjectProperty().isFunctional(getOWLOntology());
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.interfaces.IProperty#isLiteralProperty()
	 */
	public boolean isLiteralProperty() {
		return entity.isOWLDataProperty();
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
