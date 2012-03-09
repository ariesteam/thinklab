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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.ISemantics;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.semanticweb.owl.model.OWLIndividual;

/**
 * <p>A Relationship connects a "source" concept to another Concept, object (Instance), or Literal through a Property.
 * Relationship objects and collections are returned in the generalized concept interface by methods called on a source
 * Concept (or Instance). Using a Relationship is crucial to enable the extended semantics of the IMA while remaining 
 * decidable and RDF- and OWL-compatible. The model also becomes a whole lot simpler to handle than RDF-heavy Jena.</p> 
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 *
 */
public class Relationship  {

	public IProperty property = null;
	public ISemanticObject     literal  = null;
	public OWLIndividual object;
	
	public Relationship(IProperty p, ISemanticObject v) {
		property = p;
		literal = v;
	}
	
	public Relationship(IProperty p, OWLIndividual instance) {
		property = p;
		object = instance;
	}
	
    /* (non-Javadoc)
     * @see org.integratedmodelling.ima.core.IRelationship#isLiteral()
     */
	public boolean isLiteral() {
		return literal != null;
	}
    
    /* (non-Javadoc)
     * @see org.integratedmodelling.ima.core.IRelationship#isClassification()
     */
    public boolean isClassification() {
        return property.isClassification();
    }
    
    /* (non-Javadoc)
     * @see org.integratedmodelling.ima.core.IRelationship#isObject()
     */
    public boolean isObject() {
        return object != null;
    }
    
    /* (non-Javadoc)
     * @see org.integratedmodelling.ima.core.IRelationship#toString()
     */
    public String toString() {
    	
    	Collection<IConcept> domain = property.getDomain();
    	
        String ret = property.toString();
        ret += 
        	" [" +
        	domain + 
        	" -> {";
        if (isLiteral()) {
        	ret += literal.getDirectType().toString();
        } else {
        	for (IConcept c : property.getRange())
        		ret += c + " ";
        }        	
    	ret += "}]";

    	if (this.hasValue())
        	ret += " = " + (literal == null ? "(null)" : literal.toString());
        return ret;
    }

	public boolean hasValue() {
		return literal != null;
	}

	public IProperty getProperty() {
		return property;
	}

	public ISemanticObject getValue() {
		return literal;
	}
	
	public IConcept getConcept() {
		return literal.getDirectType();
	}

	public IList asList(HashMap<String, String> references) throws ThinklabException {

		ArrayList<Object> alist = new ArrayList<Object>();
		
		alist.add(property);
		
		if (isObject()) {
		
			ISemantics oo = Thinklab.get().conceptualize(object);
			alist.add(oo.asList());

		} else if (isLiteral()) {
			
			if (((Property)property).entity.isOWLDataProperty()) {
				alist.add(literal.toString());
			
			} else {
				
				/* extended literal: store concept and ID, if any, as well */
				String cid = literal.getDirectType().toString();
				String lid = literal.toString();
				if (lid != null && !lid.equals(""))
					cid += "#" + lid;
				
				Object[] llist ={ cid, literal.toString() };
				alist.add(PolyList.fromArray(llist));
			}
		} else if (isClassification()) {
			alist.add(literal.getDirectType());
		}
		return PolyList.fromArray(alist.toArray());
	}
	
	public String getSignature() {
		
		String ret = "{" + property;
		
		if (isObject()) {
			
			ret += ((Instance)object).getSignature();
			
		} else if (isLiteral()) {
			
			// FIXME could be annotation property, too, not sure it gets here
			if (((Property)property).entity.isOWLDataProperty()) {
				ret += "," + literal.toString();
			
			} else {
				
				/* extended literal */
				String cid = literal.getDirectType().toString();
				ret += ",[" + cid+ "|" + literal.toString() + "]";
			}
		} else if (isClassification()) {
			ret += ",{" + literal.getDirectType() + "}";
		}
		
		return ret + "}";
		
	}

	public OWLIndividual getObject() {
		return object;
	}
}
