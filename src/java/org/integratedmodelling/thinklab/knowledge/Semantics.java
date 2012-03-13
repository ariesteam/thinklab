/**
 * InstanceList.java
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
package org.integratedmodelling.thinklab.knowledge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.api.factories.IKnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IKnowledge;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.ISemantics;
import org.integratedmodelling.thinklab.api.lang.IList;

/**
 * The semantics for a ISemanticObject. It contains all the information to
 * create a OWL individual for the object, plus the Thinklab extensions to
 * support class objects and extended literals. A relationship is also
 * Semantics, so all relationships can be obtained consistently.
 * 
 * Semantics can be built for an arbitrary Object by the knowledge manager as
 * long as it's been properly annotated (see @Literal, @Concept, and @Property)
 * and/or it implements IConceptualizable.
 * 
 * Semantics is implemented internally as a compact IList, so it can be printed
 * nicely and converted to XML, JSON or any host language data structures. Like
 * all semantic objects in Thinklab, it is immutable.
 * 
 * @author Ferdinando Villa
 */
public class Semantics implements ISemantics {


	/*
	 * the semantic predicate is either a concept or a property.
	 */
	IKnowledge predicate;

	/*
	 * filled if the predicate is a concept and the annotation is for a literal object
	 */
	Object literal;
	
	/*
	 * only filled if the predicate is a data property
	 */
	ISemanticObject target;
	
	/*
	 * filled if the predicate is an object property
	 */
	ISemantics       targetSemantics;

	/*
	 * only filled if the predicate is a concept
	 */
	ArrayList<ISemantics> relationships;

	/*
	 * the original representation
	 */
	IList list;

	/*
	 * we need this
	 */
	IKnowledgeManager _km = null;
	
	/*
	 * and this, unfortunately.
	 */
	String _signature = null;

	/**
	 * This will try to convert the passed targets according to what the
	 * predicates expect. So for example, concept and properties can be passed
	 * as strings, and string literals can be passed for data properties that
	 * expect complex objects, which will be parsed from the corresponding
	 * literals. Obvious conversions are also supported through
	 * parse(object.toString()). This way the annotation should support lists
	 * that come from textual input.
	 * 
	 * @param instanceList
	 * @param km
	 */
	public Semantics(IList instanceList, IKnowledgeManager km) {
		_km = km;
		list = instanceList;
		predicate = getKnowledge(list.first());
	}

	/*
	 * parse the rest of the list into relationships and objects. 
	 */
	private void parse(IList iList) {
		
		relationships = new ArrayList<ISemantics>();
		Object[] oo = iList.toArray();
		
		for (int i = 1; i < oo.length; i++) {

			Object o = oo[i];
			if (predicate instanceof IConcept) {
				/*
				 * anything but the predicate must be a relationship, i.e. a IList; if
				 * not a list, we're dealing with a literal.
				 */
				if (o instanceof IList) {					
					relationships.add(new Semantics((IList)o, _km));
				} else {
					literal = o;
				}
				
			} else if (predicate instanceof IProperty) {
				
				/*
				 * target can be a regular object, a semantic object or pure semantics
				 */
				if (o instanceof ISemanticObject) {
					target = (ISemanticObject)o;
				} else if (o instanceof Semantics) {
					targetSemantics = (Semantics)target;
				} else if (o instanceof IList) {
					targetSemantics = new Semantics((IList)o, _km);
				} else if (o instanceof String) {
					
					/*
					 * use the property - if data, find the type for the datatype
					 */
					if (((IProperty)predicate).isObjectProperty()) {
						IConcept range = getRange((IProperty)predicate);
						try {
							target = _km.parse(o.toString(), range);
						} catch (ThinklabException e) {
							throw new ThinklabRuntimeException(e);
						}
					} else {
						
						String datatype = getDataType((IProperty)predicate);
						/*
						 * TODO
						 */
					}
					
				} else {
					/*
					 * try to annotate it
					 */
					try {
						target = _km.annotate(o);
					} catch (ThinklabException e) {
						throw new ThinklabRuntimeException(e);
					}
				}
			} 
		}
	}


	private String getDataType(IProperty predicate2) {
		// TODO Auto-generated method stub
		return null;
	}

	private IConcept getRange(IProperty property) {

		IConcept ret = null;
		Collection<IConcept> range = property.getRange();
		if (range != null && range.size() > 0)
			ret = range.iterator().next();
		else 
			throw new ThinklabRuntimeException("cannot establish range of property " + property +
					" to parse literal value");
		
		return ret;
	}

	private IKnowledge getKnowledge(Object o) {

		IKnowledge ret = null;
		if (o instanceof IKnowledge) {
			ret = (IKnowledge)o;
		} else {
			ret = _km.getConcept(o.toString());
			if (ret == null)
				ret = _km.getProperty(o.toString());
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.lang.ISemantics#getRelationshipsCount()
	 */
	@Override
	public int getRelationshipsCount() {
		return getRelationships().size();
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.lang.ISemantics#getRelationshipsCount(org.integratedmodelling.thinklab.api.knowledge.IProperty)
	 */
	@Override
	public int getRelationshipsCount(IProperty property)
			throws ThinklabException {
		return getRelationships(property).size();
	}

	public IList asList() {
		return list;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.lang.ISemantics#toString()
	 */
	@Override
	public String toString() {
		return list.toString();
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.lang.ISemantics#getConcept()
	 */
	@Override
	public IConcept getConcept() {
		return (IConcept)predicate;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.lang.ISemantics#getProperty()
	 */
	@Override
	public IProperty getProperty() {
		return (IProperty)predicate;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.lang.ISemantics#isLiteral()
	 */
	@Override
	public boolean isLiteral() {
		getRelationships();
		return 
			targetSemantics != null && 
			((Semantics)targetSemantics).literal != null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.lang.ISemantics#getRelationships()
	 */
	@Override
	public Collection<ISemantics> getRelationships() {

		if (relationships == null) {
			parse(list);
		}
		return relationships;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.lang.ISemantics#getRelationships(org.integratedmodelling.thinklab.api.knowledge.IProperty)
	 */
	@Override
	public Collection<ISemantics> getRelationships(IProperty property)
			throws ThinklabException {

		ArrayList<ISemantics> ret = new ArrayList<ISemantics>();
		for (ISemantics rel : getRelationships()) {
			if (((Semantics)rel).predicate.is(property))
				ret.add(rel);
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.lang.ISemantics#getValue(org.integratedmodelling.thinklab.api.knowledge.IProperty)
	 */
	@Override
	public ISemantics getValue(IProperty property) throws ThinklabException {
		Collection<ISemantics> r = getRelationships(property);
		return r.size() > 0 ? r.iterator().next().getTargetSemantics() : null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.lang.ISemantics#getTargetSemantics()
	 */
	@Override
	public ISemantics getTargetSemantics() {

		getRelationships();
		if (target != null) 
			return target.getSemantics();
		return targetSemantics;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.lang.ISemantics#getTargetLiteral()
	 */
	@Override
	public Object getLiteral() {
		getRelationships();
		return literal;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.lang.ISemantics#getTarget()
	 */
	@Override
	public ISemanticObject getTarget() {
		getRelationships();
		if (target == null && targetSemantics != null)
			target = new SemanticObject(targetSemantics, null);
		return target;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.lang.ISemantics#getValues(org.integratedmodelling.thinklab.api.knowledge.IProperty)
	 */
	@Override
	public List<ISemantics> getValues(IProperty property)
			throws ThinklabException {
		List<ISemantics> ret = new ArrayList<ISemantics>();
		for (ISemantics r : getRelationships(property)) {
			ret.add(r.getTargetSemantics());
		}
		return ret;
	}
	
	/*
	 * ensure we can index object with their semantics, although it's quite
	 * expensive. Kbox storage depends on this.
	 */
	private String getSignature() {
		
		if (_signature == null) {
		
			_signature = predicate.toString();

			if (getTarget() != null)
				_signature += "|" + ((Semantics)(getTargetSemantics())).getSignature();

			if (literal != null)
				_signature += "|" + literal.hashCode();

			ArrayList<String> ss = new ArrayList<String>(getRelationshipsCount());
		
			for (ISemantics s : getRelationships()) {
				ss.add(((Semantics)s).getSignature());
			}
			
			Collections.sort(ss);
			for (String s : ss) {
				_signature += "|" + s;
			}
		}
		return _signature;
	}

	@Override
	public boolean equals(Object arg0) {
		return 
			arg0 instanceof Semantics && 
			((Semantics)arg0).getSignature().equals(this.getSignature());
	}

	@Override
	public int hashCode() {
		return getSignature().hashCode();
	}
	
}
