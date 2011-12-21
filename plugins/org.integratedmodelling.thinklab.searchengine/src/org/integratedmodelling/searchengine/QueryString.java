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

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.utils.LogicalConnector;

/**
 * A textual query that is initialized from a string. We may want to preprocess it and
 * intercept specific semantically relevant fields, plus things related to context etc.
 * 
 * If the query goes to a kbox, we may direct those fields to an actual Constraint query
 * to intersect results with, according to the kbox's capabilities.
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

}
