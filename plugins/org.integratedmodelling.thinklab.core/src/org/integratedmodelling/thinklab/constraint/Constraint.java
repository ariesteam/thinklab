/**
 * Constraint.java
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
package org.integratedmodelling.thinklab.constraint;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabConstraintValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIncompatibleConstraintException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.IQuery;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.utils.LogicalConnector;
import org.integratedmodelling.utils.MalformedListException;
import org.integratedmodelling.utils.Polylist;

/**
 *    <p>A constraint is a generalized set of restrictions on the type and properties of an instance or
 *    concept. It is defined with a list format and is meant to be compact, readable (or
 *    close to readable) and general. Conditions on terminal (literal) symbols are specified
 *    in terms of an extensible vocabulary of operators that allows complex literals (e.g.
 *    overlap checking of polygons). Such operators are defined by the correspondent Value implementation
 *    and not by the constraint language. Constraints can be merged with other constraints.
 *    </p>
 *    
 *    <p>A constraint can be used to:</p>
 *    
 *    <ul>
 *    	<li>express a query over a knowledge box. IKBox implementations must provide
 *    		methods to translate the constraint into a suitable query supported by
 *    		their internal implementation - e.g. an SQL SELECT statement.</li>
 *      <li>define a set of restrictions, e.g. read from an OWL class, to be used
 *      	as necessary.</li>
 *      <li>express any generic restriction on the content of a kbox, e.g. to filter
 *      	the queries of a specific set of users to a subset of the possible kbox
 *      	contents.</li>
 *    </ul>
 *    
 *    <p>Constraints can optionally contain an ID tag, an extended documentation string, 
 *    and a target kbox URI.</p> 
 
	  <pre>
     </pre>
 *
 * @author Ferdinando Villa
 * @date August 16, 2007 from scratch
 */
public class Constraint implements IQuery {

	public Restriction body = null;
	public String id = null;
    public String description = null;
    public String kbox = null;
    
    /* 
     * a constraint always selects a concept, and may have restrictions on the values
     * of properties.
     */
    private IConcept concept = null;
    
	
    /**
	 * Create a constraint from a concept string.
	 * @param s
	 * @throws ThinklabException 
	 */
	public Constraint(String concept) throws ThinklabException {
		this.concept = KnowledgeManager.get().requireConcept(concept);
	}
	
	/**
	 * Create a constraint from a list.
	 * @param l
	 */
	public Constraint(Polylist l)  throws ThinklabException {
		createFromList(l);
	}
	
	public Constraint() {
		this.concept = KnowledgeManager.Thing();
    }

	/**
	 * Create a constraint that will select the passed type and its subclasses.
	 * @param concept
	 * @category Creation API
	 */
	public Constraint(IConcept concept) {
		this.concept = concept == null ? KnowledgeManager.Thing() : concept;
	}
   
	/** 
	 * Define constraint from a list. 
	 * @param l a Polylist with the constraint definition.
	 * @throws ThinklabConstraintValidationException 
	 */
	private void createFromList(Polylist l) throws ThinklabException {
		
		/* just in case */
		
		Object[] def = l.array();
		ArrayList<Restriction> restrictions = new ArrayList<Restriction>();
		
		for (int i = 0; i < def.length; i++) {
			if (def[i] instanceof Polylist) {

				Polylist content = (Polylist)def[i];
				
				if (content.length() == 2 && content.first().toString().toLowerCase().equals("id")) {
					id = content.second().toString();
				} else if (content.length() == 2 && content.first().toString().toLowerCase().equals("description")) {
					description = content.second().toString();
				} else if (content.length() == 2 && content.first().toString().toLowerCase().equals("kbox")) {
					kbox = content.second().toString();
				} else {
					restrictions.add(Restriction.parseList(content));
				}
			} else {
				
				/* can't have it twice */
				if (concept != null)
					throw new ThinklabConstraintValidationException(
							"constraint: invalid token " + def[i] + "; concept has been already specified");
				
				if (def[i] instanceof IConcept)
					concept = (IConcept)def[i];
				else 
					concept = KnowledgeManager.get().requireConcept(def[i].toString());
			}
		}

		if (restrictions.size() == 1) 
			body = restrictions.get(0);
		else if (restrictions.size() > 1)
			body = Restriction.AND(restrictions.toArray(new Restriction[restrictions.size()]));
		
		if (concept == null) {
			concept = KnowledgeManager.Thing();
		}
	}

	public Polylist asList() {
		
		ArrayList<Object> def = new ArrayList<Object>();
		
		def.add(concept);
		
		
		if (id != null) {
			def.add(Polylist.list("id", description));
		}
		if (description != null) {
			def.add(Polylist.list("description", description));
		}
		if (kbox != null) {
			def.add(Polylist.list("kBox", description));
		}
		
		if (body != null)
			def.add(body.asList());
		
		return Polylist.PolylistFromArray(def.toArray());
	}
	
	/**
	 * Restrict the constraint by passing a restriction. This is the main way of 
	 * creating a constraint using the API unless the constraint is parsed from a 
	 * list literal. Constraint must have been created with the Constraint(IConcept) 
	 * constructor, so that a main class is already defined.
	 * 
	 * All restrictions are ANDed together to whatever is already there. If you need
	 * different logics, you can pass e.g. Restriction.OR(R1, R2) or use the restrict that
	 * takes a logical connector.
	 * 
	 * @param restriction
	 * @throws ThinklabIncompatibleConstraintException 
	 * @category Creation API
	 * @returns self, not a new constraint; it's done only to enable shorter idioms when creating
	 * a constraint like new Constraint(..).restrict(...);
	 */
	public Constraint restrict(Restriction ... restrictions) {
		return restrict(LogicalConnector.INTERSECTION, restrictions);
	}
	
	public Constraint restrict(Collection<Restriction> restrictions) {
		return restrict(
				restrictions.toArray(
						new Restriction[restrictions.size()]));
	}
	
	/**
	 * Restrict the current constraint by properly merging in the passed connections using the passed
	 * mode. Don't even think about passing anything but AND and OR, although no check is made.
	 * @param connector LogicalConnector.INTERSECTION or UNION. Nothing else please.
	 * @param restrictions as many new restrictions as you want
	 * @returns this, not a new constraint; it's done only to enable shorter idioms when creating
	 *    a constraint like new Constraint(..).restrict(...);
	 */
	public Constraint restrict(LogicalConnector connector, Restriction ... restrictions) {
		
		/* empty body, just add the AND of the restrictions, or if it's just one make it the body. */
		if (body == null) {
			if (restrictions.length > 1) {
				body = Restriction.AND(restrictions);
			} else {
				body = restrictions[0];
			}				
		} else if (body.isConnector() && body.connector.equals(connector)) {
			/* we're a compatible connector, merge our restrictions directly as siblings */
			for (Restriction restriction : restrictions)
				body.siblings.add(restriction);
		} else {
			
			/* 
			 * incompatible connector or other restriction: take former body and join it with whatever
			 * we're passing
			 */
			Restriction bd = body;
			if (connector.equals(LogicalConnector.INTERSECTION)) {
				body = Restriction.AND(bd);
			} else if (connector.equals(LogicalConnector.UNION)) {
				body = Restriction.OR(bd);
			} 
			
			for (Restriction restriction : restrictions)
				body.siblings.add(restriction);
		}
		
		return this;
	}
	
	/**
	 * 
	 * @param constraint
	 * @param connector
	 * @throws ThinklabIncompatibleConstraintException
	 */
	public void merge(Constraint constraint, LogicalConnector connector) throws ThinklabIncompatibleConstraintException {

		if (constraint == null)
			return;
		
		/* merge concepts if possible. Must match of course. */
        IConcept c1 = concept;
        IConcept c2 = constraint.concept;
        IConcept ck = null;
        
        if (!c1.equals(c2)) {
    
            if (c1.is(c2))
                ck = c1;
            else if (c2.is(c1))
                ck = c2;
            else 
                throw new ThinklabIncompatibleConstraintException("constraints can't be merged: scope " +
                        c1 +
                        " is incompatible with " +
                        c2);
        }
		
        concept = ck;
        
        /* merge bodies if necessary */
		if (constraint.body == null)
			return;
		
		if (body == null) {
			body = constraint.body.duplicate();
		} else {
			Restriction old = body;
			body = new Restriction(connector);
			body.siblings.add(old);
			body.siblings.add(constraint.body.duplicate());
		}
	}
	
	/**
	 * Add a restriction as a constraint on a linked object. If any other restriction exist, AND them. If
	 * another connector is wanted, use the restriction class directly or the list format.
	 * 
	 * @param propertyType
	 * @param objectConstraint
	 * @throws ThinklabException 
	 * @throws ThinklabIncompatibleConstraintException 
	 * @category Creation API
	 */
	public void addObjectRestriction(String propertyType, Constraint objectConstraint) throws ThinklabException {
		restrict(new Restriction(propertyType, objectConstraint));
	}
	
	/**
	 * Add a restriction as a constraining class on a classification property. If any other restriction exist, AND them. If
	 * another connector is wanted, use the restriction class directly or the list format.
	 * 
	 * @param propertyType
	 * @param classID
	 * @category Creation API
	 */
	public void addClassificationRestriction(String propertyType, String classID)  throws ThinklabException {
		restrict(new Restriction(propertyType, classID));
	}
	
	/**
	 * Add a restriction as the result of an operator on a linked literal. If any other restriction exist, AND them. If
	 * another connector is wanted, use the restriction class directly or the list format.
	 * 
	 * @param propertyType
	 * @param operator
	 * @param value
	 * @category Creation API
	 */
	public void addLiteralRestriction(String propertyType, String operator, String value)  throws ThinklabException  {
		restrict(new Restriction(propertyType, operator, value));
	}

	/**
	 * Add a restriction as the result of an operator on a linked literal. If any other restriction exist, AND them. If
	 * another connector is wanted, use the restriction class directly or the list format.
	 * 
	 * @param propertyType
	 * @param operator
	 * @param value
	 * @category Creation API
	 */
	public void addLiteralRestriction(String propertyType, String operator, IValue value) throws ThinklabException {
		restrict(new Restriction(propertyType, operator, value.toString()));
	}

	/**
	 * Return the restriction objects
	 * @return
	 */
	public Restriction getRestrictions() {
		return body;
	}
		
	/**
	 * reset constraint to empty.
	 *
	 */
	public void reset(IConcept concept) {
		body = null;
		this.concept = concept;
	}

	public void reset() {
		reset(KnowledgeManager.Thing());
	}
	
	public void reset(String concept) throws ThinklabException {
		reset(KnowledgeManager.get().requireConcept(concept));
	}
   
	/**
	 * Check if constraint is empty.
	 * @return true if body is null.
	 */
	public boolean empty() {
		return body == null && concept.equals(KnowledgeManager.Thing());
	}
	
	/**
	 * Get the concept to be matched. 
	 * @return
	 */
	public IConcept getConcept() {
		return concept;
	}

	
	/**
	 * Return true if the concept to be matched is not the universal Thing()-y.
	 * @return
	 */
	public boolean hasConceptScope() {
		return !concept.equals(KnowledgeManager.Thing());
	}
	
    /**
     * Validate passed object for conditions expressed in constraint. 
     * @param i a concept or instance to validate.
     * @return true if match is positive.
     * @throws ThinklabException 
     */
    public boolean match(IKnowledgeSubject i) throws ThinklabException {
        
    	boolean ok = concept.is(i.getType());
    	
    	if (ok) {
    		ok = body.match(i);
    	}
    	
    	return ok;
    }
    
    public String toString() {
    	return asList().toString();
    }

    /* this should not be used ordinarily. */
	public void setConcept(IConcept c) {
		concept = c;
	}

	public String asText() {
		return toString();
	}

	public void parse(String query) throws ThinklabValidationException {

		try {
			createFromList(Polylist.parse(query));
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
	}

	public boolean isEmpty() {
		return empty();
	}

	/**
	 * Find a restriction by property.
	 * FIXME: entirely not right, only works for what I need today - to be implemented properly.
	 * 
	 * @param property
	 * @return
	 */
	public Restriction findRestriction(String property) {

		if (getRestrictions().getProperty().toString().equals(property))
			return getRestrictions();

		for (Restriction r : getRestrictions().getChildren()) {
			if (r.getProperty().toString().equals(property))
				return r;
		}
		
		return null;
	}
    
}
