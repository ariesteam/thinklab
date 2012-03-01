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
package org.integratedmodelling.searchengine;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.lang.LogicalConnector;
import org.integratedmodelling.lang.SemanticAnnotation;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticLiteral;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.knowledge.query.IRestriction;
import org.integratedmodelling.thinklab.api.lang.IList;

/**
 * A textual query that is initialized from a string. We may want to preprocess it and
 * intercept specific semantically relevant fields, plus things related to context etc.
 * 
 * If the query goes to a kbox, we may direct those fields to an actual Constraint query
 * to intersect results with, according to the kbox's capabilities.
 * 
 * TODO find a smart way to implement the full contract of IQuery while preserving
 * the fuzzy search capabilities.
 * 
 * @author Ferdinando Villa
 *
 */
public class QueryString implements IQuery {

	private String query;

	public QueryString(String s) {
		query = s;
	}
	
	public String asText() {
		return query;
	}

	public void parse(String query) throws ThinklabValidationException {
		this.query = query;
	}

	public boolean isEmpty() {
		return query == null || query.trim().equals("");
	}

	@Override
	public IQuery merge(IQuery constraint, LogicalConnector connector)
			throws ThinklabException {
		
		if (! (constraint instanceof QueryString))
			throw new ThinklabValidationException("query string can't be merged with a different query");
		
		String txt = query;
		if (constraint != null) {
			txt += 
				connector.equals(LogicalConnector.INTERSECTION) ? " AND " : " OR " +
				((QueryString)constraint).query;
		}
		
		return new QueryString(txt);
	}

	@Override
	public IList asList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQuery restrict(LogicalConnector connector,
			IRestriction... restrictions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addObjectRestriction(String propertyType, IQuery objectQuery)
			throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addClassificationRestriction(String propertyType, String classID)
			throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addLiteralRestriction(String propertyType, String operator,
			String value) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addLiteralRestriction(String propertyType, String operator,
			ISemanticLiteral value) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IRestriction getRestrictions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean match(Object i) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

}
