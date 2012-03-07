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
import org.integratedmodelling.lang.SemanticAnnotation;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.IRelationship;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticLiteral;
import org.integratedmodelling.thinklab.api.lang.IList;

import edu.stanford.smi.protegex.owl.swrl.bridge.query.ObjectValue;

/**
 * <p>A Relationship connects a "source" concept to another Concept, object (Instance), or Literal through a Property.
 * Relationship objects and collections are returned in the generalized concept interface by methods called on a source
 * Concept (or Instance). Using a Relationship is crucial to enable the extended semantics of the IMA while remaining 
 * decidable and RDF- and OWL-compatible. The model also becomes a whole lot simpler to handle than RDF-heavy Jena.</p> 
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 *
 */
public class Relationship implements IRelationship {

	public IProperty property = null;
	public ISemanticLiteral     literal  = null;
	public IInstance object;
	
	public Relationship(IProperty p, ISemanticLiteral v) {
		property = p;
		literal = v;
	}
	
	public Relationship(IProperty p, IInstance instance) {
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
        	ret += literal.getConcept().toString();
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

	public ISemanticLiteral getValue() {
		return literal;
	}
	
	public IConcept getConcept() {
		return literal.getConcept();
	}

	public IList asList(HashMap<String, String> references) throws ThinklabException {

		ArrayList<Object> alist = new ArrayList<Object>();
		
		alist.add(property);
		
		if (isObject()) {
		
			SemanticAnnotation oo = object.conceptualize();
			alist.add(oo.asList());

		} else if (isLiteral()) {
			
			// FIXME could be annotation property, too, not sure it gets here
			if (((Property)property).entity.isOWLDataProperty()) {
				alist.add(literal.toString());
			
			} else {
				
				/* extended literal: store concept and ID, if any, as well */
				String cid = literal.getConcept().toString();
				String lid = null; // literal.toString();
				if (lid != null && !lid.equals(""))
					cid += "#" + lid;
				
				Object[] llist ={ cid, literal.toString() };
				alist.add(PolyList.fromArray(llist));
			}
		} else if (isClassification()) {
			alist.add(literal.getConcept());
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
				String cid = literal.getConcept().toString();
				ret += ",[" + cid+ "|" + literal.toString() + "]";
			}
		} else if (isClassification()) {
			ret += ",{" + literal.getConcept() + "}";
		}
		
		return ret + "}";
		
	}

	@Override
	public IInstance getObject() {
		return object;
	}
}
