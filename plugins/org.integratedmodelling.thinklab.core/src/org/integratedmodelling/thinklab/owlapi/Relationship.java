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

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Polylist;

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
	public IValue     literal  = null;
	
	public Relationship(IProperty p, IValue v) {
		property = p;
		literal = v;
	}
	
    /* (non-Javadoc)
     * @see org.integratedmodelling.ima.core.IRelationship#isLiteral()
     */
	public boolean isLiteral() {
		return literal != null && literal.isLiteral();
	}
    
    /* (non-Javadoc)
     * @see org.integratedmodelling.ima.core.IRelationship#isClassification()
     */
    public boolean isClassification() {
        return literal != null && literal.isClass();
    }
    
    /* (non-Javadoc)
     * @see org.integratedmodelling.ima.core.IRelationship#isObject()
     */
    public boolean isObject() {
        return literal != null && literal.isObjectReference();
    }
    
    /* (non-Javadoc)
     * @see org.integratedmodelling.ima.core.IRelationship#toString()
     */
    public String toString() {
    	
    	Collection<IConcept> domain = property.getDomain();
    	
        String ret = property.getSemanticType().toString();
        ret += 
        	" [" +
        	domain + 
        	" -> {";
        if (isLiteral()) {
        	ret += literal.getConcept().getSemanticType().toString();
        } else {
        	for (IConcept c : property.getRange())
        		ret += c.getSemanticType().toString() + " ";
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

	public IValue getValue() {
		return literal;
	}
	
	public IConcept getConcept() {
		return literal.getConcept();
	}

	public Polylist asList(HashMap<String, String> references) throws ThinklabException {

		ArrayList<Object> alist = new ArrayList<Object>();
		
		alist.add(property);
		
		if (isObject()) {
		
			IInstance oo = literal.asObjectReference().getObject();
			alist.add(((Instance)oo).toListInternal(null, references));

		} else if (isLiteral()) {
			
			// FIXME could be annotation property, too, not sure it gets here
			if (((Property)property).entity.isOWLDataProperty()) {
				alist.add(literal.toString());
			
			} else {
				
				/* extended literal: store concept and ID, if any, as well */
				String cid = literal.getConcept().toString();
				String lid = literal.getID();
				if (lid != null && !lid.equals(""))
					cid += "#" + lid;
				
				Object[] llist ={ cid, literal.toString() };
				alist.add(Polylist.PolylistFromArray(llist));
			}
		} else if (isClassification()) {
			alist.add(literal.getConcept());
		}
		return Polylist.PolylistFromArray(alist.toArray());
	}
}
