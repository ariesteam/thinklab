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
package org.integratedmodelling.thinklab.interfaces.query;

import org.integratedmodelling.thinklab.exception.ThinklabException;


/**
 * The interface implemented by any object that can be queried, such as a kbox, a textual
 * search engine, etc.
 * @author Ferdinando Villa
 */
public interface IQueriable {
	
	/**
	 * Return an IQuery that this kbox will like by parsing the given string. Most
	 * queriables have a preferred query type so it should be easy.
	 * 
	 * @param toEval
	 * @return
	 */
	public abstract IQuery parseQuery(String toEval) throws ThinklabException;
	
	/**
	 * The simplest query operation just returns all results that match the query, with no
	 * metadata schema (meaning all metadata are retrieved) and no query boundaries.
	 */
	public abstract IQueryResult query(IQuery q) throws ThinklabException;
	
	/**
	 * Submit the query and return results with specified offsets and max number of results. All
	 * metadata will be included in the results.
	 * 
	 * @param q
	 * @param offset
	 * @param maxResults
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IQueryResult query(IQuery q, int offset, int maxResults) throws ThinklabException;

	/**
	 * Submit the query with specified metadata and return results.
	 * 
	 * @param q
	 * @param metadata
	 * @param offset
	 * @param maxResults
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IQueryResult query(IQuery q, String[] metadata, int offset, int maxResults) throws ThinklabException;

}
