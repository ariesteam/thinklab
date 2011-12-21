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
package org.integratedmodelling.thinklab.interfaces.storage;

import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.Quantifier;

/**
 * Objects of this class are returned by IKBox.getCapabilities() to tell us what the kbox
 * supports and doesn't. Mostly unimplemented this far.
 * 
 * @author Ferdinando Villa
 *
 */
public interface IKBoxCapabilities {

	/**
	 * True if any type of nontrivial query is supported.
	 * @return
	 */
	public abstract boolean canQuery();
	
	/**
	 * True if the null/empty query will retrieve all objects.
	 * @return
	 */
	public abstract boolean canQueryAll();
	
	/**
	 * True if retrieving an object by ID will work.
	 * @return
	 */
	public abstract boolean canRetrieveInstance();
	
	/**
	 * True if an object can be retrieved by ID as a list.
	 * @return
	 */
	public abstract boolean canRetrieveAsList();

	/**
	 * True if an object can be stored by passing a list.
	 * @return
	 */
	public abstract boolean canStoreList();
	
	/**
	 * True if an instance can be stored at all.
	 * @return
	 */
	public abstract boolean canStoreInstance();
	
	/**
	 * True if the kbox will use the reference table passed to the storage functions that use one.
	 * @return
	 */
	public abstract boolean canUseReferencesWhileStoring();

	/**
	 * True if the kbox will use the reference table passed to the retrieval functions that use one.
	 * @return
	 */
	public abstract boolean canUseReferencesWhileRetrieving();

	/**
	 * True if the given quantifier in the query will be honored.
	 * @param q
	 * @return
	 */
	public abstract boolean honorsQuantifier(Quantifier q);
	
	/**
	 * True if the given schema is understood.
	 * @param schema
	 * @return
	 */
	public abstract boolean supportsSchema(Polylist schema);
	
	/**
	 * True if the given operator (possibly with a given operand) will be understood when applied
	 * to an object of the given class.
	 * 
	 * @param target
	 * @param operand
	 * @param op
	 * @return
	 */
	public abstract boolean supportsOperator(IConcept target, IConcept operand, String op);
	
	/**
	 * True if the given query class can be used on the associated kbox.
	 * @param query
	 * @return
	 */
	public abstract boolean supportsQuery(Class<IQuery> query);
	
}
