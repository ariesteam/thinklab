/**
 * IQueryResult.java
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

import java.util.HashMap;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Polylist;

/**
 * Queries in thinklab return knowledge, of course, and they may use a "schema"
 * to specify things that we want to know about these objects. Typically we
 * don't want query result to contain initialized objects but we want the query
 * to return either elements from the result schema or list representations that
 * objects can be created from. So we do not pass sessions to any of these
 * methods. Query results should store the queriable and the query so that paged
 * access is possible without having to go back to the queriables.
 * 
 * @author Ferdinando
 * 
 */
public interface IQueryResult {

	String ID_FIELD_NAME = null;
	String CLASS_FIELD_NAME = null;
	String LABEL_FIELD_NAME = null;
	String DESCRIPTION_FIELD_NAME = null;

	/**
	 * Return the queriable object that was queried to produce us.
	 * @return
	 */
	public abstract IQueriable getQueriable();

	/**
	 * Return the query that produces us.
	 * @return
	 */
	public abstract IQuery getQuery();

	/**
	 * 
	 * @return
	 */
	public int getTotalResultCount();

	/**
	 * 
	 * @return
	 */
	public int getResultOffset();

	/**
	 * 
	 * @return
	 */
	public int getResultCount();

	/**
	 * A score from 0 to 1. The meaning is implementation dependent, typically
	 * is relevance, quality, accuracy. If you have no notions that could use
	 * a score, return 1.0f.
	 * 
	 * @param n
	 * @return
	 */
	public float getResultScore(int n);

	/**
	 * Ultimately the results of any search is an IInstance. Still, we can
	 * return any IValue. Many implementations will want to defer the creation
	 * of the instance to the moment it is asked for. We need to pass a session
	 * if we need to get the result instance. If we know the result is not 
	 * an instance, we can pass null.
	 * 
	 * @param n
	 * @return
	 * @throws ThinklabException
	 */
	public IValue getResult(int n, ISession session) throws ThinklabException;

	/**
	 * 
	 * @param n
	 * @param schemaField
	 * @return
	 * TODO this should return a IValue, not an Object
	 * @throws ThinklabException 
	 */
	public IValue getResultField(int n, String schemaField) throws ThinklabException;


	/**
	 * Ignore the schema and just return the n-th "object" as a list
	 * representation. If the second argument is passed, make sure the list only
	 * references the objects that are in there, and memorize the references to
	 * the ones that we have defined.
	 * 
	 * @param n
	 * @param references
	 * TODO
	 * @return
	 * @throws ThinklabException
	 */
	public Polylist getResultAsList(int n, HashMap<String, String> references)
			throws ThinklabException;

	/**
	 * This must only be implemented if it is economical to load pages in
	 * advance. Some implementations may call it if they're tracking the paging;
	 * whether to act on it depends on the strategy implemented in the result
	 * object.
	 * 
	 * @param currentItem
	 * @param itemsPerPage
	 * @throws ThinklabException
	 */
	public void moveTo(int currentItem, int itemsPerPage)
			throws ThinklabException;
}
