/**
 * QueryFormStructure.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinkcap.
 * 
 * Thinkcap is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinkcap is distributed in the hope that it will be useful,
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
 * @author    Ferdinando
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.webapp.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.integratedmodelling.thinklab.webapp.interfaces.IRestrictionComponent;
import org.integratedmodelling.thinklab.webapp.interfaces.IRestrictionComponentConstructor;
import org.integratedmodelling.thinklab.webapp.interfaces.IVisualizationComponentConstructor;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.w3c.dom.Node;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Rows;

public class QueryFormStructure {
	
	private String conceptID;
	VisualConcept concept;
	boolean initialized = false;
	
	// the three containers below capture the declarations from XML
	HashSet<String> ignoredOntologies = new HashSet<String>();
	HashSet<String> ignoredProperties = new HashSet<String>();
	ArrayList<FieldSpecs> declaredFields = null;
	
	class FieldSpecs {
		String label;
		VisualProperty property;
		IRestrictionComponentConstructor constructor;
		IVisualizationComponentConstructor vconstructor;
		boolean isHidden;
		String group;
		public boolean multiple;
		public boolean hide;
	}

	/* 
	 * to be filled by initialize() with whatever fields we determined we 
	 * want in the form, based on this specs and the ancestors.
	 */
	ArrayList<FieldSpecs> formFields = new ArrayList<FieldSpecs>();
	
	QueryFormStructure ancestor = null;
	
	QueryFormStructure(String id) {
		this.conceptID	 = id;
	}
	
	void read(Node node, boolean isVisualization) throws ThinklabException {
		
		for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling()) {
			
			if (n.getNodeName().equals("ignore")) {
				
				String wattr = XMLDocument.getAttributeValue(n, "ontology");
				
				if (wattr != null && !wattr.equals(""))
					ignoredOntologies.add(wattr);
				
				wattr = XMLDocument.getAttributeValue(n, "property");
				
				if (wattr != null && !wattr.equals(""))
					ignoredProperties.add(wattr);

				
			} else if (n.getNodeName().equals("field")) {

				String label = XMLDocument.getAttributeValue(n, "label");
				String prope = XMLDocument.getAttributeValue(n, "property");
				String claxx = XMLDocument.getAttributeValue(n, "use", "");
				String multi = XMLDocument.getAttributeValue(n, "multiple", "false");
				String fhide  = XMLDocument.getAttributeValue(n, "multiple", "false");
				String group  = XMLDocument.getAttributeValue(n, "group");
				
				FieldSpecs fs = new FieldSpecs();
				
				fs.label = label;
				fs.property = TypeManager.get().getVisualProperty(KnowledgeManager.get().requireProperty(prope));
				fs.multiple = BooleanValue.parseBoolean(multi);
				fs.hide = BooleanValue.parseBoolean(fhide);
				fs.group = group;
				
				if (claxx != null && !claxx.equals("")) {
					
					try {
						Class<?> zio = Class.forName(claxx);
						
						if (isVisualization)
							fs.vconstructor = (IVisualizationComponentConstructor) zio.newInstance();
						else
							fs.constructor = (IRestrictionComponentConstructor) zio.newInstance();
						
					} catch (Exception e) {
						throw new ThinklabValidationException(e);
					}
	
				} else {
					fs.constructor = 
						TypeManager.get().
							getRestrictionComponentConstructor(fs.property.property);
				}
				
				if (declaredFields == null)
					declaredFields = new ArrayList<FieldSpecs>();
				
				declaredFields.add(fs);

			}
		}
	}
	
	boolean isIgnored(String prefix) {

		boolean ret = 
			ignoredOntologies.contains(prefix) || ignoredProperties.contains(prefix);
		
		if (!ret && ancestor != null) {
			ret = ancestor.isIgnored(prefix);
		}
		
		return ret;
	}
	
	/*
	 * we defer this to when it's used the first time, so that the whole hierarchy of
	 * forms is defined. 
	 */
	void initialize() throws ThinklabException {
		
		if (initialized)
			return;
		
		initialized = true;

		concept = TypeManager.get().getVisualConcept(conceptID);
		
		/* 
		 * retrieve all other form structures up the hierarchy from the type manager.
		 */
		ArrayList<QueryFormStructure> qfs = 
			TypeManager.get().getAllQueryStructures(concept.concept);
		
		if (qfs.size() > 1) {			
			ancestor = qfs.get(qfs.size() - 1);
			ancestor.initialize();
		}
		
		/*
		 * 1. determine which properties we want and in which order. First see if we have
		 * a given order somewhere down the line.
		 */
		ArrayList<FieldSpecs> order = getDeclaredOrder();
		
		/*
		 * 2. If we have no ordering, build it from the list of properties, ignoring whatever
		 * we told it to ignore.
		 */
		if (order != null) {

			formFields = order;
			
		} else {
			
			/*
			 * literals first
			 */
			for (VisualProperty p : concept.getLiteralProperties(VisualConcept.ALL)) {
				
				if (isIgnored(p.property.getConceptSpace()) || isIgnored(p.property.toString()))
					continue;
				
				FieldSpecs fs = new FieldSpecs();
				
				fs.group = "Attributes";
				fs.property = p;
				fs.hide = false;
				fs.label = p.getLabel();
				fs.multiple = concept.concept.getMaxCardinality(p.property) > 1;
				
				fs.constructor = TypeManager.get().getRestrictionComponentConstructor(p.property);
				
				if (fs.constructor != null)
					formFields.add(fs);	
				
			}
			
			/*
			 * classifications
			 */
			for (VisualProperty p : concept.getClassificationProperties(VisualConcept.ALL)) {
				
				if (isIgnored(p.property.getConceptSpace()) || isIgnored(p.property.toString()))
					continue;
				
				FieldSpecs fs = new FieldSpecs();
				
				fs.group = "Classifications";
				fs.property = p;
				fs.hide = false;
				fs.label = p.getLabel();
				fs.multiple = concept.concept.getMaxCardinality(p.property) > 1;
				
				fs.constructor = TypeManager.get().getRestrictionComponentConstructor(p.property);
				
				if (fs.constructor != null)
					formFields.add(fs);	
			}
			
			/*
			 * objects
			 */
			for (VisualProperty p : concept.getObjectProperties(VisualConcept.ALL)) {
				
				if (isIgnored(p.property.getConceptSpace()) || isIgnored(p.property.toString()))
					continue;
				
				FieldSpecs fs = new FieldSpecs();

				// links should stand alone, not in a group
				fs.property = p;
				fs.hide = false;
				fs.label = p.getLabel();
				fs.multiple = concept.concept.getMaxCardinality(p.property) > 1;
				
				fs.constructor = TypeManager.get().getRestrictionComponentConstructor(p.property);
				
				if (fs.constructor != null)
					formFields.add(fs);	
			}
		}
	}
	
	
	private ArrayList<FieldSpecs> getDeclaredOrder() {

		ArrayList<FieldSpecs> ret = declaredFields;
		
		if (ret == null && ancestor != null)
			ret = ancestor.getDeclaredOrder();
		
		return ret;
	}

	public Collection<Component> getVisualizationComponents(int indentLevel) throws ThinklabException {
		
		
		ArrayList<Component> ret = new ArrayList<Component>();
	
		initialize();
		
		/*
		 * For each property that we do want, create the appropriate selector.  If we're
		 * grouping, group them.
		 */
		String cGroup = "";
		Grid cGbox = null; Rows rows = null;
		
		for (FieldSpecs spec : formFields) {
					
			if (spec.isHidden)
				continue;
			
			if (spec.group != null) {
				
				if (!spec.group.equals(cGroup)) {
					cGroup = spec.group;
					Groupbox gbox = new Groupbox();
					gbox.appendChild(new Caption(spec.group));
					gbox.setWidth("100%");
					cGbox = new Grid();
					cGbox.setWidth("100%");
					rows = new Rows();
					cGbox.appendChild(rows);
					gbox.appendChild(cGbox);
					ret.add(gbox);
				}
			} else if (rows != null) {
				rows = null;
				cGroup = "";
			}
			
			Component c = 
				spec.vconstructor.createVisualizationComponent(
						spec.property.property, 
						spec.label,
						indentLevel);
			
			if (c != null) {
				
				if (rows != null) 
					rows.appendChild(c);
				else
					ret.add(c);
			}
				
		}
		
		return ret;
		
	}
	
	public Collection<Component> getFormComponents(int indentLevel) throws ThinklabException {
		
		ArrayList<Component> ret = new ArrayList<Component>();
	
		initialize();
		
		/*
		 * For each property that we do want, create the appropriate selector.  If we're
		 * grouping, group them.
		 */
		String cGroup = "";
		Grid cGbox = null; Rows rows = null;
		
		for (FieldSpecs spec : formFields) {
					
			if (spec.isHidden)
				continue;
			
			if (spec.group != null) {
				
				if (!spec.group.equals(cGroup)) {
					cGroup = spec.group;
					Groupbox gbox = new Groupbox();
					gbox.appendChild(new Caption(spec.group));
					gbox.setWidth("100%");
					cGbox = new Grid();
					cGbox.setWidth("100%");
					rows = new Rows();
					cGbox.appendChild(rows);
					gbox.appendChild(cGbox);
					ret.add(gbox);
				}
			} else if (rows != null) {
				rows = null;
				cGroup = "";
			}
			
			Component c = 
				spec.constructor.createRestrictionComponent(
						spec.property.property, 
						spec.label,
						indentLevel);
			
			if (c != null) {
				
				if (rows != null) 
					rows.appendChild(c);
				else
					ret.add(c);
			}
				
		}
		
		return ret;
		
	}
	
	public Constraint getConstraint(Collection<Component> components) throws ThinklabException {
		
		Constraint ret = new Constraint(concept.concept);
		
		/* 
		 * follow the same strategy that we used to create the components to extract
		 * their restrictions and combine them in the final constraint.
		 */
		for (Component c : components) {

			if (c instanceof Groupbox) {
			
				Rows hb = null;
				for (Object cc : c.getChildren()) {
					if (cc instanceof Grid) {
						hb = (Rows)((Grid)cc).getChildren().get(0);
						break;
					}
				}
				
				if (hb == null)
					continue;
				
				for (Object cc : hb.getChildren()) {
					if (cc instanceof IRestrictionComponent) {
						
						Restriction restr = ((IRestrictionComponent)cc).getRestriction();
						if (restr != null)
							ret.restrict(restr);
					}
				}
				
			} else if (c instanceof IRestrictionComponent){

				Restriction restr = ((IRestrictionComponent)c).getRestriction();
				if (restr != null)
					ret.restrict(restr);
			}
		}
		
		return ret;
	}
	
}