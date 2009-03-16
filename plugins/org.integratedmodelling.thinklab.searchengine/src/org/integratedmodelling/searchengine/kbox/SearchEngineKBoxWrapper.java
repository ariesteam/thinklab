/**
 * SearchEngineKBoxWrapper.java
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
package org.integratedmodelling.searchengine.kbox;

import java.util.Properties;

import org.integratedmodelling.searchengine.QueryString;
import org.integratedmodelling.searchengine.SearchEngine;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBoxCapabilities;
import org.integratedmodelling.thinklab.kbox.KBoxWrapper;
import org.integratedmodelling.thinklab.kbox.RankingKBox;
import org.integratedmodelling.utils.Polylist;

/**
 * Adds textual search capabilities to a kbox by expanding it with a search engine.
 * Intercepts textual queries and passes the others along. Does the same with
 * the kbox's capabilities, so that external applications will be able to know that
 * a search query is now supported.
 * 
 * @author Ferdinando Villa
 *
 */
public class SearchEngineKBoxWrapper extends KBoxWrapper implements RankingKBox {

	SearchEngine searchEngine = null;
	
	@Override
	public IKBoxCapabilities getKBoxCapabilities() {
		return new SearchEngineKBoxCapabilities(super.getKBoxCapabilities());
	}

	@Override
	public IQueryResult query(IQuery q, int offset, int maxResults)
			throws ThinklabException {

		if (q instanceof QueryString) {
			//
		}
		return kbox.query(q, offset, maxResults);
	}

	@Override
	public IQueryResult query(IQuery q, Polylist resultSchema, int offset,
			int maxResults) throws ThinklabException {
		
		if (q instanceof QueryString) {
			//
		}
		return kbox.query(q, resultSchema, offset, maxResults);
	}

	@Override
	public IQueryResult query(IQuery q) throws ThinklabException {
		
		if (q instanceof QueryString) {
			//
		}
		return kbox.query(q);
	}

	public IQuery parseQuery(String toEval) throws ThinklabException {
		
		if (toEval.trim().startsWith("("))
			return kbox.parseQuery(toEval);
		return new QueryString(toEval);
	}

	public Polylist getMetadataSchema()  throws ThinklabException  {
		return kbox.getMetadataSchema();
	}

	@Override
	public String getUri() {
		return kbox.getUri();
	}

	@Override
	public Properties getProperties() {
		return kbox.getProperties();
	}

	
}
