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
package org.integratedmodelling.thinklab.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.lang.Quantifier;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IKnowledge;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.interfaces.knowledge.SemanticQuery;

public class Query implements IQuery, IParseable, SemanticQuery {

	/*
	 * either a property or a concept, whose nature defines what we match in the
	 * object we're passed - either the object or its relationships. If it's null
	 * we are just a connector - our match() passes on the subject to the 
	 * restrictions, then evaluates the result based on the quantifier.
	 */
	IKnowledge _subject;
	
	/*
	 * these only matter if (_subject instanceof IProperty)
	 */
	Quantifier _quantifier = Quantifier.ANY();
	List<IQuery> _restrictions = null;
	
	protected Query() {
		
	}
	
	protected Query(IConcept c) {
		_subject = c;
	}

	protected Query(IProperty p, IQuery[] queries) {
		_subject = p;
		_quantifier = Quantifier.ALL();
		if (queries != null)
			_restrictions = Arrays.asList(queries);
	}

	protected Query(Quantifier q, IQuery[] queries) {
		_quantifier = q;
		_restrictions = Arrays.asList(queries);
	}

	/*
	 * -----------------------------------------------------------------------------------
	 * Static API - the way to create default queries.
	 * -----------------------------------------------------------------------------------
	 */
		
	/**
	 * Produce a query that will select objects that incarnate the passed concept.
	 * 
	 * @param c
	 * @return
	 */
	public static Query select(IConcept c) {
		return new Query(c);
	}
	
	/**
	 * Produce a query that ANDs all the passed queries, selecting only
	 * objects that match them all.
	 *  
	 * @param queries
	 * @return
	 */
	public static Query and(IQuery ... queries) {
		return new Query(Quantifier.ALL(), queries);
	}

	/**
	 * Produce a query that ORs all the passed queries, selecting objects
	 * that match one or more of them.
	 *  
	 * @param queries
	 * @return
	 */
	public static Query or(IQuery ... queries) {
		return new Query(Quantifier.ANY(), queries);
	}

	/**
	 * Produce a query that selects objects that do not
	 * match any of the passed queries.
	 * 
	 * @param queries
	 * @return
	 */
	public static Query no(IQuery ... queries) {
		return new Query(Quantifier.NONE(), queries);
	}
	
	/**
	 * Produce a query that selects object that match exactly n of the passed
	 * queries.
	 * 
	 * @param n
	 * @param queries
	 * @return
	 */
	public static Query exactly(int n, IQuery ... queries) {
		return new Query(Quantifier.EXACTLY(n), queries);
	}

	/**
	 * Produce a query that selects objects that match at least n of
	 * the passed queries.
	 * 
	 * @param n
	 * @param queries
	 * @return
	 */
	public static Query atLeast(int n, IQuery ... queries) {
		return new Query(Quantifier.RANGE(n, Quantifier.INFINITE), queries);
	}

	/**
	 * Produce a query that selects objects that match at most n of
	 * the passed queries.
	 * 
	 * @param n
	 * @param queries
	 * @return
	 */
	public static Query atMost(int n, IQuery ... queries) {
		return new Query(Quantifier.RANGE(Quantifier.INFINITE, n), queries);
	}

	/**
	 * Produce a query that selects objects that match at between min and max of
	 * the passed queries.
	 * 
	 * @param min
	 * @param max
	 * @param queries
	 * @return
	 */
	public static Query between(int min, int max, IQuery ... queries) {
		return new Query(Quantifier.RANGE(min, max), queries);
	}
	
	/*
	 * -----------------------------------------------------------------------------------
	 * Non-API introspection methods from SemanticQuery, meant to allow query rewriting in kboxes.
	 * -----------------------------------------------------------------------------------
	 */
	
	@Override
	public List<SemanticQuery> getRestrictions() {
		List<SemanticQuery> ret = new ArrayList<SemanticQuery>();
		for (IQuery q : _restrictions) {
			ret.add((SemanticQuery) q);
		}
		return ret;
	}
	
	@Override
	public IConcept getSubject() {
		return (IConcept) _subject;
	}
	
	@Override
	public Quantifier getQuantifier() {
		return _quantifier;
	}

	/*
	 * -----------------------------------------------------------------------------------
	 * The API we're supposed to have
	 * -----------------------------------------------------------------------------------
	 */
	
	
	@Override
	public void parse(String string) throws ThinklabException {

	}

	@Override
	public String asText() {
		return asList().toString();
	}

	@Override
	public IList asList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQuery restrict(IProperty property, IQuery... queries) {

		if (_restrictions == null) {
			_restrictions = new ArrayList<IQuery>();
		}
		
		_restrictions.add(new Query(property, queries));
		
		return this;
	}

	@Override
	public boolean match(Object i) throws ThinklabException {

		ISemanticObject semantics = Thinklab.get().annotate(i);
		if (semantics == null) {
			throw new ThinklabValidationException("query: object " + i + " cannot be conceptualized");
		}
		
		boolean match = true;
		
		if (_subject instanceof IConcept) {
			
			match = semantics.is((IConcept)_subject);
			
		} else if (_subject instanceof IProperty) {

			/*
			 * match each restrictions over each target of the property. 
			 * Property restrictions are always in AND. If there are 
			 * no restrictions, all we want is that there is a relationship.
			 */
			if (_restrictions == null) {
				match = semantics.getRelationshipsCount((IProperty)_subject) > 0;
			} else {
				for (ISemanticObject target : semantics.getRelationships((IProperty)_subject)) {
					for (IQuery q : _restrictions) {
						if (! (match = q.match(target))) {
							match = false;
							break;
						}
					}
				}
			}
		} else {
			
			/*
			 * just a connector - match the object and behave according to connector. 
			 */
			boolean ret = false;
			
	        int matches = 0;
	        
	        for (IQuery restriction : _restrictions) {
	            
	            ret = restriction.match(i);
	            
	            if (_quantifier.is(Quantifier.ALL) && !ret) {
	                ret = false;
	                break;
	            }
	            if (_quantifier.is(Quantifier.ANY) && ret) {
	                ret = true;
	                break;
	            }
	            
	            if (ret)
	                matches++;
	        }
	        
	        if (_quantifier.is(Quantifier.NONE)) {
	            match = matches == 0;
	        } else if (_quantifier.is(Quantifier.EXACT) || _quantifier.is(Quantifier.RANGE)) {
	            match = _quantifier.match(matches);
	        } else {
	        	match = ret;
	        }
	        
	    	return ret;

		}
		
		return match;
	}


}
