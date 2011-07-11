/**
 * QueryString.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabSearchEnginePlugin.
 * 
 * ThinklabSearchEnginePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabSearchEnginePlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.searchengine;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
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
