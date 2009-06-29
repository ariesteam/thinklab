/**
 * SQLKBox.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabSQLPlugin.
 * 
 * ThinklabSQLPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabSQLPlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.sql;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.interfaces.storage.IKBoxCapabilities;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.utils.MalformedListException;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;

/**
 * We wrap a SQLThinklab server into a KBox interface. This implementation
 * creates its own server from the URL passed, and the server personalizes the
 * schema for each different host architecture, so this can be a concrete class
 * and no more KBoxes are required to bridge to other DBs.
 * 
 * @author Ferdinando Villa
 * 
 */
public class SQLKBox extends SQLThinklabServer implements IKBox {
	
	Hashtable<String, Integer> totalsCache = new Hashtable<String, Integer>();
	String uri = null;
	
	public SQLKBox(String uri, String protocol, String serverURL, Properties properties)
			throws ThinklabException {
		super(protocol, SQLPlugin.get().createSQLServer(serverURL, properties),
				properties);
		this.uri = uri;
	}

	public IQuery parseQuery(String toEval) throws ThinklabException {

		Polylist l = null;
		try {
			l = Polylist.parse(toEval);
		} catch (MalformedListException e) {
			throw new ThinklabValidationException(e);
		}
		return new Constraint(l);
	}
	
	public IInstance getObjectFromID(String id, ISession session)
			throws ThinklabException {

		Polylist list = this.retrieveObjectAsList(id);
		return session.createObject(list);
	}

	public IInstance getObjectFromID(String id, ISession session,
			HashMap<String, String> refTable) throws ThinklabException {

		Polylist list = this.retrieveObjectAsList(id, refTable);
		return session.createObject(list);
	}

	@Override
	public String storeObject(IInstance object, String id, Map<String, IValue> metadata, ISession session)
			throws ThinklabException {

		String ret = null;

		Pair<String, String> sql = storeInstanceSQL(object, session, id, metadata);

		if (sql != null && sql.getSecond() != null && sql.getFirst() != "") {
			server.execute(sql.getSecond());
			ret = sql.getFirst();
		}

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.integratedmodelling.ima.core.interfaces.IKBox#storeObject(org.integratedmodelling.ima.core.interfaces.IInstance,
	 *      org.integratedmodelling.ima.core.interfaces.ISession,
	 *      java.util.HashSet)
	 * 
	 */
	@Override
	public String storeObject(IInstance object, String id, Map<String, IValue> metadata,
			ISession session, HashMap<String, String> references) throws ThinklabException {
		String ret = null;

		Pair<String, String> sql = storeInstanceSQL(object, session, references, id, metadata);

		if (sql != null && sql.getSecond() != null && sql.getFirst() != "") {
			server.execute(sql.getSecond());
			ret = sql.getFirst();
		}

		return ret;
	}

	public IKBoxCapabilities getKBoxCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	public IQueryResult query(IQuery q, int offset, int maxResults)
			throws ThinklabException {
		return query(q, getMetadataSchema(), offset, maxResults);
	}

	public IQueryResult query(IQuery q, Polylist resultSchema, int offset,
			int maxResults) throws ThinklabException {

		if (q != null && !q.isEmpty() && !(q instanceof Constraint)) {
			throw new ThinklabStorageException(
					"only constraint queries are admitted for the SQL kbox");
		}

		/* no tables, no results */
		if (!isStorageInitialized())
			return new SQLQueryResult();
		
		String query = null;

		if (q == null || q.isEmpty()) {
			query = getAllObjectsQuery();
		} else {
			query = translateConstraint((Constraint) q);
		}

		/* no context, no result */
		if (query == null)
			return new SQLQueryResult();

		/* empty query, we want it all */
		if (query.equals(""))
			query = getAllObjectsQuery();

		int total = -1;

		/*
		 * retrieve total count of results; we cache the result to avoid running
		 * the query twice next time. The SQL is machine generated, so caching
		 * the string should be reliable.
		 */
		Integer tt = totalsCache.get(query);
		if (tt == null) {

			String csql = query.replaceFirst("SELECT object_id",
					"SELECT COUNT(object_id)");
			QueryResult res = server.query(csql);
			total = res.getInt(0, 0);
			totalsCache.put(query, total);

		} else {
			total = tt;
		}

		/* inject other fields in expected result */
		query = addSchemaFieldsToQuery(query, resultSchema);

		/* add limits if any */
		query = addLimitsToQuery(query, offset, maxResults);

		/*
		 * TODO do we want this? Should we make it an option? How? only select
		 * "main" objects (explicitly stored)
		 */
		// query = addMainObjectConstraintToQuery(query);

		/*
		 * go for it
		 */
		QueryResult res = server.query(query);

		return new SQLQueryResult(res, total, offset, maxResults,
				(Constraint) q, this);
	}

	@Override
	public String storeObject(Polylist list, String id, Map<String, IValue> metadata, ISession session) throws ThinklabException {

		String ret = null;
		/*
		 * FIXME/TODO: this is quite demanding, but the only way to do it right
		 * when there are evaluated fields. We will need to assess the
		 * feasibility of doing this on a large scale. It is only likely to be
		 * used in import and copy sessions, which can typically wait.
		 */
		if (session == null)
			session = new Session();
		
		IInstance object = session.createObject(list);

		Pair<String, String> sql = storeInstanceSQL(object, session, id, metadata);

		if (sql != null && sql.getSecond() != null && sql.getFirst() != "") {
			server.execute(sql.getSecond());
			ret = sql.getFirst();
		}

		return ret;
	}
	
	@Override
	public String storeObject(Polylist list, String id, Map<String, IValue> metadata,
			ISession session, HashMap<String, String> refTable) throws ThinklabException {

		String ret = null;
		/*
		 * FIXME/TODO: this is quite demanding, but the only way to do it right
		 * when there are evaluated fields. We will need to assess the
		 * feasibility of doing this on a large scale. It is only likely to be
		 * used in import and copy sessions, which can typically wait.
		 */
		if (session == null)
			session = new Session();
		
		IInstance object = session.createObject(list);

		Pair<String, String> sql = storeInstanceSQL(object, session, refTable, id, metadata);

		if (sql != null && sql.getSecond() != null && sql.getFirst() != "") {
			server.execute(sql.getSecond());
			ret = sql.getFirst();
		}

		return ret;
	}

	public IQueryResult query(IQuery q) throws ThinklabException {
		return query(q, getMetadataSchema(), 0, -1);
	}

	public Polylist getObjectAsListFromID(String id,
			HashMap<String, String> refTable) throws ThinklabException {
		return retrieveObjectAsList(id, refTable);
	}

	public Polylist getMetadataSchema()  throws ThinklabException  {
		return KBoxManager.get().parseSchema(getProperties());
	}

	@Override
	public String getUri() {
		return uri;
	}

	@Override
	public long getObjectCount() {
		long ret = 0l;
		try {
			ret = super.getObjectCount();
		} catch (ThinklabStorageException e) {
		}
		return ret;
	}

	@Override
	public void resetToEmpty() throws ThinklabException {
		super.resetToEmpty();
	}

}
