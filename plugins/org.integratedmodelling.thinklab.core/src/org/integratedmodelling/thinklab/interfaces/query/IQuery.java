/**
 * IQuery.java
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
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.LogicalConnector;

/**
 * A query class whose purpose is to generalize the querying
 * of "objects" and the retrieval of their results. Apart from existing (so it could be passed to
 * IQueriable) it mandates the ability to serialize a query to/from a string and to check for
 * "empty" (general) queries.
 * 
 * @author Ferdinando Villa
 *
 */
public interface IQuery {

	/**
	 * Queries should be definable by creating one with the empty constructor and 
	 * passing some sort of textual specification to parse().
	 * @param query
	 * @throws ThinklabValidationException
	 */
	abstract void parse(String query) throws ThinklabValidationException;
	
	/**
	 * Queries should always be capable of returning a textual specification that can later
	 * be parsed back into a query of the same type.
	 * 
	 * @return
	 */
	abstract String asText();
	
	/**
	 * Return true if the query is empty, meaning that it will select everything that's queriable. 
	 * 
	 * @return
	 */
	abstract boolean isEmpty();

	/**
	 * Return a new query which is the logical connection of self with the passed one.
	 * 
	 * @param constraint
	 * @param intersection
	 * @return
	 */
	abstract IQuery merge(IQuery constraint, LogicalConnector intersection) throws ThinklabException ;
	
}
