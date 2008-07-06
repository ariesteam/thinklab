/**
 * SQLQueryResult.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IQueriable;
import org.integratedmodelling.thinklab.interfaces.IQuery;
import org.integratedmodelling.thinklab.interfaces.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.thinklab.value.ObjectReferenceValue;
import org.integratedmodelling.utils.Polylist;

public class SQLQueryResult implements IQueryResult {

	SQLKBox kbox = null;
	Constraint query = null;
	int nResults = 0;
	int offset = -1;
	int max = -1;
	Polylist schema = null;
	Hashtable<String, Integer> schemaIdx = new Hashtable<String, Integer>();
	private QueryResult qresult;
	IValue[] instances = null;
	
	// create from results of successful query
	public SQLQueryResult(QueryResult qres, Polylist schema, int totalres, int offset,
			int max, Constraint query, SQLKBox kbox) {
		
		this.kbox = kbox;
		this.query = query;
		nResults = totalres;
		this.qresult = qres;
		instances = new IValue[this.qresult.nRows()];
		parseResultSchema(schema);
	}

	// null result
	public SQLQueryResult() {
	}

	public IQueriable getQueriable() {
		return kbox;
	}

	public IQuery getQuery() {
		return query;
	}

	public Object getResultField(int n, String schemaField) {
		return qresult.get(n, schemaIdx.get(schemaField)+1);
	}

	public Object getResultField(int n, int schemaIndex) {
		return qresult.get(n, schemaIndex);
	}

	public Polylist getResultAsList(int n, HashMap<String, String> references) throws ThinklabException {
		return kbox.getObjectAsListFromID(qresult.get(n, 0), references);
	}

	public int getResultCount() {
		return qresult.nRows();
	}

	public int getResultOffset() {
		return offset;
	}

	public Polylist getResultSchema() {
		return schema;
	}

	public float getResultScore(int n) {
		return (float) 1.0;
	}

	public int getTotalResultCount() {
		return nResults;
	}

	public void moveTo(int currentItem, int itemsPerPage)
			throws ThinklabException {
		// TODO Auto-generated method stub

	}

	public void parseResultSchema(Polylist list) {

		int i = 0;
		for (Object o : list.array()) {
			schemaIdx.put(o.toString(), new Integer(i++));
		}
	}

	public IValue getResult(int n, ISession session) throws ThinklabException {
		if (instances[n] == null)
			instances[n] = new ObjectReferenceValue(
					kbox.getObjectFromID(qresult.get(n, 0), session));
		return instances[n];
	}

	public HashMap<String, IValue> getResultMetadata(int n) throws ThinklabException  {

		// TODO 
		Object[] sch = schema.array();
		ArrayList<Object> vals = new ArrayList<Object>();
		
		for (Object s : sch) {
			vals.add(getResultField(n, s.toString()));
		}
		
		return
			KBoxManager.get().
				createResult(schema, Polylist.PolylistFromArray(vals.toArray()));
	}

}
