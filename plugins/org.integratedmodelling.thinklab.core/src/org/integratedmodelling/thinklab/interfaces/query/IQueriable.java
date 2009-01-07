/**
 * IQueriable.java
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
package org.integratedmodelling.thinklab.interfaces.query;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.utils.Polylist;


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
	 * result schema and no query boundaries.
	 */
	public abstract IQueryResult query(IQuery q) throws ThinklabException;
	
	/**
	 * Submit the query and return results. Do not use any specific schema for the results;
	 * leave it to the implementation to decide the schema to be used.
	 * 
	 * @param q
	 * @param offset
	 * @param maxResults
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IQueryResult query(IQuery q, int offset, int maxResults) throws ThinklabException;

	/**
	 * Submit the query with a specified schema and return results.
	 * 
	 * @param q
	 * @param resultSchema
	 * @param offset
	 * @param maxResults
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IQueryResult query(IQuery q, Polylist resultSchema, int offset, int maxResults) throws ThinklabException;

}
