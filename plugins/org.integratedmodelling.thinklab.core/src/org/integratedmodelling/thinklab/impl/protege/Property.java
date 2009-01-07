/**
 * Property.java
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedSemanticTypeException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IKnowledge;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.interfaces.knowledge.IResource;

import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSDatatype;

/**
 * @author Ioannis N. Athanasiadis, Dalle Molle Institute for Artificial Intelligence, USI/SUPSI
 *
 * @since 13 Jun 2006
 */
public class Property extends Object implements IProperty {
	
	protected RDFProperty property;
	
	/**
	 * This is the default constructor 
	 */
	public Property(RDFProperty property) {
		this.property = property;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.IProperty#is(org.integratedmodelling.ima.core.IProperty)
	 */
	public boolean is(IKnowledge prop) {
		
		return (prop instanceof IProperty) && 
			   (equals(prop) || 
					   property.getSuperproperties(true).contains(((Property)prop).property));
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.IProperty#isClassification()
	 */
	public boolean isClassification() {
		try {
			return is(KnowledgeManager.get().getClassificationProperty());
		} catch (ThinklabException e) {
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.IProperty#isLiteralProperty()
	 */
	public boolean isLiteralProperty() {
		try {
			return property instanceof OWLDatatypeProperty ||
				   is(KnowledgeManager.get().getReifiedLiteralProperty());
		} catch (ThinklabException e) {
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.IProperty#isObjectProperty()
	 */
	public boolean isObjectProperty() {
		return (property instanceof OWLObjectProperty);
	}
	
	public IProperty getInverseProperty(){
		if(property.getInverseProperty()==null)
			return null;
		return new Property(property.getInverseProperty());
	}
	
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.IProperty#getRange()
	 */
	// a property can be either an ObjectProperty or a DatatypeProperty
	// 
	// The ObjectProperty can be either of type NamedClass or UnionClass
	// and it could be a mixture of such.
	//
	// The Datatype property can be only one kind of datatype 
	// (i.e boolean XOR float, cant be boolean OR float). 
	public Collection<IConcept> getRange() {
		
		Collection<IConcept> result = new ArrayList<IConcept>();

		if(isObjectProperty()){
			Iterator inRanges = property.getRanges(true).iterator();
			while(inRanges.hasNext()){
				RDFResource res = (RDFResource) inRanges.next();
				if (res instanceof OWLNamedClass) {
					try {
						result.add(KnowledgeManager.get().getConceptFromURI(res.getURI().toString()));
					} catch (ThinklabNoKMException e) {
						e.printStackTrace();
					}
				} else if (res instanceof DefaultOWLUnionClass){
					Collection<OWLNamedClass> col = new HashSet<OWLNamedClass>(); 
					col = getAllSingleNamedOperands((DefaultOWLUnionClass) res, col);
					Iterator i = col.iterator();
					while(i.hasNext()){
						try {
							result.add(KnowledgeManager.get().getConceptFromURI(((OWLNamedClass) i.next()).getURI().toString()));
						} catch (ThinklabNoKMException e) {
							e.printStackTrace();
						}
					}
				}// else it is imposible 
			}
		} else{
			Iterator inRanges = property.getRanges(false).iterator();
			// I follow the getRanges property but there SHOULD be only one
			while(inRanges.hasNext()){
				RDFResource res = (RDFResource) inRanges.next();
				if (res instanceof DefaultRDFSDatatype) {
					DefaultRDFSDatatype datatype = (DefaultRDFSDatatype) res;
					String ret = datatype.getURI();
					try {
						if (ret.endsWith("#string"))
							result.add(KnowledgeManager.get().getTextType());
						else if (ret.endsWith("#float"))
							result.add(KnowledgeManager.get().getFloatType());
						else if (ret.endsWith("#double"))
							result.add(KnowledgeManager.get().getDoubleType());
						else if (ret.endsWith("#int"))
							result.add(KnowledgeManager.get().getIntegerType());
						else if (ret.endsWith("#long"))
							result.add(KnowledgeManager.get().getLongType());
						else if(ret.endsWith("#boolean"))
							result.add(KnowledgeManager.Boolean());
					} catch (ThinklabException e) {	
						e.printStackTrace();
					}
				} else if (res instanceof OWLDataRange){
					// TODO: this is a list...
				}
			}
		}
		return result;
	}
	
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.IProperty#getDomain()
	 */
	public Collection<IConcept> getDomain() {
		
		Set<IConcept> ret = new HashSet<IConcept>();
		
		RDFResource res = property.getDomain(false);
		if (res instanceof OWLNamedClass){
			ret.add(new Concept((OWLNamedClass)res));
		} else if (res instanceof RDFSClass){
			// this is a hack: the KM is bypassed for the anonymous classes! 
			// Not sure if it is working!
			// FV should be ok - we don't need the KM 
			ret.add(new Concept((RDFSClass)res));
		}  //there shouldn't be other option
		
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.IResource#getID()
	 */
	public String getLocalName() {
		return property.getLocalName();
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.IResource#getConceptSpace()
	 */
	public String getConceptSpace() {
		return this.property.getNamespacePrefix();
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.IResource#getURI()
	 */
	public String getURI() {
		return property.getURI();
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.IResource#getSemanticType()
	 */
	public SemanticType getSemanticType() {
		try {
			return new SemanticType(getConceptSpace()+":"+getLocalName());
		} catch (ThinklabMalformedSemanticTypeException e) {
			// won't happen
		}
		return null;
	}
	
	public String getLabel() {
		
		return ThinklabOWLManager.getLabel(property, null);	
	}
	
	public String getDescription() {
		
		return ThinklabOWLManager.getComment(property, null);	
	}
	
	public String getLabel(String languageCode) {
		
		return ThinklabOWLManager.getLabel(property, languageCode);
	}
	
	public String getDescription(String languageCode) {

		return ThinklabOWLManager.getComment(property, null);
	}
	
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.IResource#equals(java.lang.String)
	 */
	public boolean equals(String s) {
		return getSemanticType().toString().equals(s);
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.IResource#equals(org.integratedmodelling.ima.core.SemanticType)
	 */
	public boolean equals(SemanticType s) {
		return getSemanticType().equals(s);
	}
	
	
	/* We need to override the @equals()@ method for using properly  
	 * @java.util.Collection@, @java.util.Set@, etc
	 * (non-Javadoc)
	 * @see java.lang.Object#equals()
	 */
	public boolean equals(Object obj){
		if (obj instanceof Property) {
			Property that = (Property) obj;
			return this.property.equals(that.property);
		}
		return false;
	}
	
	/* We need to override the @hashCode()@ method for using properly  
	 * @java.util.Collection@, @java.util.Set@, etc
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		int result; 
		result = property.hashCode(); 
		return result; 
	}
	
	/**
	 * This method transforms a OWLUnionClass to a Collection of OWLNamedClasses.
	 * Iteratively adds the members of sub-Unions. 
	 * However it misses possible OWLIntersectionClasses.
	 * Migtht there is a better implmementation
	 * @param class1 the union class
	 * @param col the collection to add the named classes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Collection<OWLNamedClass> getAllSingleNamedOperands(OWLUnionClass class1, Collection<OWLNamedClass> col) {
		Collection operands = class1.getOperands();
		Collection<OWLNamedClass> namedOperands = class1.getNamedOperands();
		col.addAll(namedOperands);
		operands.remove(namedOperands);
		Iterator i = operands.iterator();
		while (i.hasNext()){
			OWLClass current = (OWLClass) i.next();
			if( current instanceof OWLUnionClass){
				col.addAll(getAllSingleNamedOperands((OWLUnionClass) current,col));
			}
		}
		return col;
	}
	public String toString(){
		return getSemanticType().toString();
	}

	@SuppressWarnings("unchecked")
	public Collection<IProperty> getAllParents() {
		
		ArrayList<IProperty> ret = new ArrayList<IProperty>();
		
		Collection<RDFProperty> pp = property.getSuperproperties(true);
		
		for (RDFProperty p : pp)
			ret.add(new Property(p));
		
		return ret;
	}

	@SuppressWarnings("unchecked")
	public Collection<IProperty> getAllChildren() {
		ArrayList<IProperty> ret = new ArrayList<IProperty>();
		
		Collection<RDFProperty> pp = property.getSubproperties(true);
		
		for (RDFProperty p : pp)
			ret.add(new Property(p));
		
		return ret;

	}

	public IProperty getParent() throws ThinklabException {
		Collection<IProperty> pp = getParents();
		if (pp.size() > 1) {
			throw new ThinklabException("property " + getSemanticType() + " has more than one parent");
		}
		return pp.iterator().next();
	}

	@SuppressWarnings("unchecked")
	public Collection<IProperty> getParents() {
		ArrayList<IProperty> ret = new ArrayList<IProperty>();
		
		Collection<RDFProperty> pp = property.getSuperproperties(false);
		
		for (RDFProperty p : pp)
			ret.add(new Property(p));
		
		return ret;
	}

	@SuppressWarnings("unchecked")
	public Collection<IProperty> getChildren() {
		ArrayList<IProperty> ret = new ArrayList<IProperty>();
		
		Collection<RDFProperty> pp = property.getSubproperties(false);
		
		for (RDFProperty p : pp)
			ret.add(new Property(p));
		
		return ret;
	}

	public boolean isAbstract() {
		return ThinklabOWLManager.
		getAnnotationAsBoolean(property, 
							   ThinklabOWLManager.abstractAnnotationProperty,
							   false);
	}

	public void addDescription(String desc) {
		property.addComment(desc);
	}

	public void addDescription(String desc, String language) {
		// TODO Protege does not support languages in comments
		property.addComment(desc);
	}

	public void addLabel(String desc) {
		property.addLabel(desc, null);
	}

	public void addLabel(String desc, String language) {
		property.addLabel(desc, language);
	}
	
	public boolean equals(IResource r) {
		return r.getURI().equals(getURI());
	}
	
	public boolean is(String conceptType) {

		boolean ret = false;
		
		IProperty c = null;
		try {
			c = KnowledgeManager.get().retrieveProperty(conceptType);
		} catch (ThinklabException e) {
			// just return false
		}
		
		if (c != null)
			ret = this.is(c);
		
		return ret;
		
	}

	/**
	 * This is used to check if we need to instantiate an extended literal when exporting and
	 * importing from lists.
	 * @return
	 */
	public boolean isDataProperty() {
		return property instanceof OWLDatatypeProperty;
	}

	public boolean isFunctional() {
		return property.isFunctional();
	}

	public boolean isAnnotation() {
		// TODO Auto-generated method stub
		return false;
	}
	
}