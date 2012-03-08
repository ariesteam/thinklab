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
package org.integratedmodelling.thinklab.constraint;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.lang.LogicalConnector;
import org.integratedmodelling.lang.Semantics;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.query.IOperator;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.knowledge.Value;


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

		constraint := (type restriction*)

		restriction :=

             ({"and"|"or"} restriction+) |
             ([quantifier] {object-property|class-property} constraint) |
             ([quantifier] literal-property operator arg+)
             (metadata-field operator arg+)
     
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
	public Constraint(IList l)  throws ThinklabException {
		createFromList(l);
	}
	
	public Constraint() {
		this.concept = KnowledgeManager.Thing();
    }

	public Restriction getMetadataRestrictions() {
		return body == null ? null : body.getMetadataRestrictions();
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
	 * @param l a IList with the constraint definition.
	 * @throws ThinklabConstraintValidationException 
	 */
	private void createFromList(IList l) throws ThinklabException {
		
		/* just in case */
		
		Object[] def = l.array();
		ArrayList<Restriction> restrictions = new ArrayList<Restriction>();
		
		for (int i = 0; i < def.length; i++) {
			if (def[i] instanceof IList) {

				IList content = (IList)def[i];
				
				if (content.length() == 2 && content.first().toString().toLowerCase().equals("id")) {
					id = content.nth(1).toString();
				} else if (content.length() == 2 && content.first().toString().toLowerCase().equals("description")) {
					description = content.nth(1).toString();
				} else if (content.length() == 2 && content.first().toString().toLowerCase().equals("kbox")) {
					kbox = content.nth(1).toString();
				} else {
					restrictions.add(Restriction.parseList(content));
				}
			} else {
				
				/* can't have it twice */
				if (concept != null)
					throw new ThinklabValidationException(
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
			body = (Restriction)Restriction.AND(restrictions.toArray(new Restriction[restrictions.size()]));
		
		if (concept == null) {
			concept = KnowledgeManager.Thing();
		}
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.IConstraint#asList()
	 */
	@Override
	public IList asList() {
		
		ArrayList<Object> def = new ArrayList<Object>();
		
		def.add(concept);
		
		
		if (id != null) {
			def.add(PolyList.list("id", description));
		}
		if (description != null) {
			def.add(PolyList.list("description", description));
		}
		if (kbox != null) {
			def.add(PolyList.list("kBox", description));
		}
		
		if (body != null)
			def.add(body.asList());
		
		return PolyList.fromArray(def.toArray());
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
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.IConstraint#restrict(java.util.Collection)
	 */
	public Constraint restrict(Collection<Restriction> restrictions) {
		return restrict(
				restrictions.toArray(
						new Restriction[restrictions.size()]));
	}
	
	public Constraint restrict(String property, String operator, Object value) throws ThinklabException {
		return restrict(LogicalConnector.INTERSECTION, new Restriction(property, operator, value));
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.IConstraint#restrict(org.integratedmodelling.lang.LogicalConnector, org.integratedmodelling.thinklab.constraint.Restriction)
	 */
	public Constraint restrict(LogicalConnector connector, Restriction ... restrictions) {
		
		/*
		 * remove all NULLs from the restriction array. A bit messy but the convenience is 
		 * priceless.
		 */
		int nulls = 0;
		for (Restriction r: restrictions) 
			if (r == null)
				nulls++;
		if (nulls > 0) {
			Restriction[] repl = new Restriction[restrictions.length - nulls];
			int i = 0;
			for (Restriction r : restrictions) {
				if (r != null)
					repl[i++] = (Restriction)r;
			}
			restrictions = repl;
		}
		
		/* empty body, just add the AND of the restrictions, or if it's just one make it the body. */
		if (body == null) {
			if (restrictions.length > 1) {
				body = (Restriction)Restriction.AND(restrictions);
			} else {
				body = (Restriction)restrictions[0];
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
				body = (Restriction)Restriction.AND(bd);
			} else if (connector.equals(LogicalConnector.UNION)) {
				body = (Restriction)Restriction.OR(bd);
			} 
			
			for (Restriction restriction : restrictions)
				body.siblings.add(restriction);
		}
		
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.IConstraint#merge(org.integratedmodelling.thinklab.api.knowledge.query.IQuery, org.integratedmodelling.lang.LogicalConnector)
	 */
	@Override
	public IQuery merge(IQuery query, LogicalConnector connector) throws ThinklabException {

		if (query == null)
			// COW
			return this;
		
		if (! (query instanceof Constraint)) {
			throw new ThinklabValidationException("constraints are incompatible");
		}
		
		Constraint constraint = (Constraint)query;		
		Constraint ret = null;
		
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
                throw new ThinklabValidationException("constraints can't be merged: scope " +
                        c1 +
                        " is incompatible with " +
                        c2);
        }
		
        ret = new Constraint(ck);
        
        /* merge bodies if necessary */
		if (constraint.body == null)
			return ret;
		
		if (body == null) {
			ret.body = constraint.body.duplicate();
		} else {
			Restriction old = body;
			ret.body = new Restriction(connector);
			ret.body.siblings.add(old);
			ret.body.siblings.add(constraint.body.duplicate());
		}
		
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.IConstraint#addObjectRestriction(java.lang.String, org.integratedmodelling.thinklab.constraint.Constraint)
	 */
	public void addObjectRestriction(String propertyType, IQuery objectConstraint) throws ThinklabException {
		restrict(new Restriction(propertyType, (Constraint)objectConstraint));
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.IConstraint#addClassificationRestriction(java.lang.String, java.lang.String)
	 */
	public void addClassificationRestriction(String propertyType, String classID)  throws ThinklabException {
		restrict(new Restriction(propertyType, classID));
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.IConstraint#addLiteralRestriction(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void addLiteralRestriction(String propertyType, String operator, String value)  throws ThinklabException  {
		restrict(new Restriction(propertyType, operator, value));
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.IConstraint#addLiteralRestriction(java.lang.String, java.lang.String, org.integratedmodelling.thinklab.api.knowledge.IValue)
	 */
	public void addLiteralRestriction(String propertyType, String operator, Value value) throws ThinklabException {
		restrict(new Restriction(propertyType, operator, value.toString()));
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.IConstraint#getRestrictions()
	 */
	public Restriction getRestrictions() {
		return body == null ? null : body.getRestrictions();
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
	
    /* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.IConstraint#match(org.integratedmodelling.thinklab.api.knowledge.IInstance)
	 */
    @Override
	public boolean match(Object i) throws ThinklabException {
        
    	Semantics ilist = Thinklab.get().conceptualize(i);
    	
    	boolean ok = concept.is(ilist.getConcept());
    	
    	if (ok && body != null) {
    		ok = body.match(ilist);
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

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.IConstraint#asText()
	 */
	@Override
	public String asText() {
		return toString();
	}

	public void parse(String query) throws ThinklabValidationException {
		try {
			createFromList(PolyList.parse(query));
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
	}

	public static Constraint parseConstraint(String query) throws ThinklabValidationException {

		Constraint ret = new Constraint();
		try {
			ret.createFromList(PolyList.parse(query));
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.IConstraint#isEmpty()
	 */
	@Override
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

	@Override
	public IQuery restrict(IProperty property, IOperator... operator) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQuery restrict(LogicalConnector connector, IProperty property,
			IOperator... restrictions) {
		// TODO Auto-generated method stub
		return null;
	}


    
}
