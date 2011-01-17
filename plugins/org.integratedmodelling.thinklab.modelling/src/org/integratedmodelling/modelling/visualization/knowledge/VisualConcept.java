/**
 * VisualConcept.java
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
package org.integratedmodelling.modelling.visualization.knowledge;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.modelling.visualization.knowledge.TypeManager.TypeDecoration;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;

/**
 * Adapted for a concept that provides easy access to anything about the concept that 
 * is helpful in visualization. Managed through TypeManager, which handles additional
 * user configuration through plugin extensions to customize look and feel.
 * 
 * @author Ferdinando Villa
 *
 */
public class VisualConcept extends VisualKnowledge {

	enum Ownership {
		OWN,
		OTHER,
		ALL
	}
	
	public final static Ownership OWN = Ownership.OWN;
	public final static Ownership OTHER = Ownership.OTHER;
	public final static Ownership ALL = Ownership.ALL;
	
	IConcept concept;
	
	// array of type decorations in inverse order, so we can scan them easily to see how
	// any property should look according to decreasingly specialized definitions.
	TypeDecoration[] decorations;

	/*
	 * these should only be created by the Type Manager, which caches them.
	 */
	VisualConcept(IConcept c, TypeManager typeManager) {
		
		concept = c;
		
		/* populate arrays of visualization descriptors and save them. */
		ArrayList<TypeDecoration> tds = typeManager.getAllTypeDecorations(c);

		int n = 0;
		decorations = new TypeDecoration[tds.size()];
		for (int i = tds.size() -1 ; i >= 0; i--) {
			decorations[n++] = tds.get(i);
		}		
	}
	
	/**
	 * Return the relative URL path to an icon that graphically represents the concept.
	 * 
	 * @param concept
	 * @param context any string context attached to the icon, or null for the
	 *        "default" icon
	 * @return
	 */
	public String getTypeIcon(String context) {

		String ret = null;
		
		for (TypeDecoration d : decorations) {
			if (context == null && d.icon != null) {
				ret = d.icon;
				break;
			} else if (context != null) {
				ret = d.pIcons.get(context);
				if (ret != null)
					break;
			}
		}
		
		return ret;
		
	}
	
	public Collection<VisualProperty> getObjectProperties(Ownership own) throws ThinklabException {
		
		ArrayList<VisualProperty> ret = new ArrayList<VisualProperty>();
		Collection<IProperty> ownp = concept.getProperties();		
		
		for (IProperty p : concept.getAllProperties()) {
			if (p.isObjectProperty()) {

				if (own.equals(OWN) && !ownp.contains(p))
					continue;
				else if (own.equals(OTHER) && ownp.contains(p))
					continue;

				ret.add(TypeManager.get().getVisualProperty(p));
			}
		}
		
		return ret;		
	}

	public Collection<VisualProperty> getClassificationProperties(Ownership own) throws ThinklabException {
		
		ArrayList<VisualProperty> ret = new ArrayList<VisualProperty>();
		Collection<IProperty> ownp = concept.getProperties();
		
		for (IProperty p : concept.getAllProperties()) {
			if (p.isClassification())  {
				
				if (own.equals(OWN) && !ownp.contains(p))
					continue;
				else if (own.equals(OTHER) && ownp.contains(p))
					continue;
				
				ret.add(TypeManager.get().getVisualProperty(p));
			}
		}
		
		return ret;		
	}

	public Collection<VisualProperty> getLiteralProperties(Ownership own) throws ThinklabException {
		
		ArrayList<VisualProperty> ret = new ArrayList<VisualProperty>();	
		Collection<IProperty> ownp = concept.getProperties();
		
		for (IProperty p : concept.getAllProperties()) {
			if (p.isLiteralProperty()) {
				
				if (own.equals(OWN) && !ownp.contains(p))
					continue;
				else if (own.equals(OTHER) && ownp.contains(p))
					continue;
				
				ret.add(TypeManager.get().getVisualProperty(p));
			}
		}
		
		return ret;		
	}
	
	public String getLabel() {

		for (TypeDecoration td : decorations) {
			if (td.label != null &&!td.label.trim().equals(""))
				return td.label;
		}
		
		String ret = concept.getLabel();
		
		if (ret == null || ret.equals("")) {
			ret = concept.getLocalName();
			/* reparse camel into sentence */
			ret = checkUpperCamel(ret);
		}
		
		return ret.trim();
	}
	
	

	/**
	 * Return the range of the passed property in the context of this concept,
	 * processing any restrictions.
	 * 
	 * @return
	 * @throws ThinklabException 
	 */
	public Collection<VisualConcept> getPropertyRange(IProperty property) throws ThinklabException {
		
		ArrayList<VisualConcept> ret = new ArrayList<VisualConcept>();
		
		for (IConcept c : concept.getPropertyRange(property)) {
			ret.add(TypeManager.get().getVisualConcept(c));
		}
		
		return ret;

	}
	
	public Collection<VisualConcept> getPropertyRange(VisualProperty property) throws ThinklabException {
		return getPropertyRange(property.getProperty());
	}

	public String getConceptSpace() {
		
		String ret = concept.getConceptSpace();
		// FIXME should never happen
		if (ret != null) ret = checkUpperCamel(ret);
		
		return ret.trim();
	}


	public String getDescription() {
		
		for (TypeDecoration td : decorations) {
			if (td.comment != null)
				return td.comment;
		}

		String ret = concept.getDescription();
		if (ret == null || ret.equals(""))
			ret = "No description given";
		return ret.trim();
	}
	
	/**
	 * Description cut to given max size, plus ellipsis if size was bigger.
	 * 
	 * @param maxlen
	 * @return
	 */
	public String getDescription(int maxlen) {
		
		String ret = getDescription();
		
		if (ret.length() > maxlen) {
			ret = ret.substring(0, maxlen - 3) + "...";
		}
		
		return ret.trim();
	}
	
	/**
	 * Returns the text that describes the cardinality of the passed property in
	 * the context of this concept.
	 * 
	 * @param property
	 * @return
	 */
	public String getPropertyCardinalityDescription(IProperty property) {
		
		String ret = "any number of";
		
		int min = concept.getMinCardinality(property);
		int max = concept.getMinCardinality(property);

		if (min == 1 &&	max == 1)	
			ret = "one";
		else if (min == 1 && max == 0)	
			ret = "one or more";
		else if (min == 0 && max == 0)	
			ret = "zero or more";
		
		return ret;
	}

	public String getPropertyCardinalityDescription(VisualProperty property) {
		return getPropertyCardinalityDescription(property.getProperty());
	}
	
	public IConcept getConcept() {
		return concept;
	}

	/**
	 * Uses label in type decoration if any, but if not there, uses beautified local
	 * name instead of label.
	 * 
	 * @return
	 */
	public String getName() {
		
		for (TypeDecoration td : decorations) {
			if (td.label != null &&!td.label.trim().equals(""))
				return td.label;
		}
		
		String ret = concept.getLocalName();
		/* reparse camel into sentence */
		ret = checkUpperCamel(ret);
		
		return ret.trim();
	}

}
