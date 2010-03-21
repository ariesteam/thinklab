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

import java.util.HashMap;

import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueriable;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.literals.ObjectReferenceValue;
import org.integratedmodelling.utils.Polylist;

public class SQLQueryResult implements IQueryResult {

	SQLKBox kbox = null;
	Constraint query = null;
	int nResults = 0;
	int offset = -1;
	int max = -1;
	private QueryResult qresult;
	IValue[] instances = null;
	float[] scores = null;
	
	// create from results of successful query
	public SQLQueryResult(QueryResult qres, int totalres, int offset,
			int max, Constraint query, SQLKBox kbox) {
		
		this.kbox = kbox;
		this.query = query;
		nResults = totalres;
		this.qresult = qres;
		instances = new IValue[this.qresult.nRows()];
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

	public IValue getResultField(int n, String schemaField) throws ThinklabException {
		
		IValue ret = null;
		
		if (instances[n] != null) {
			/*
			 * if we have the object, try getting its property
			 */
			IInstance i;
			try {
				i = instances[n].asObjectReference().getObject();
				ret = i.get(schemaField);
			} catch (ThinklabException e) {
				// ignore, it just means it's not there
			}
		} else if (qresult != null) {
			
			// ret = qresult.getValue(n, 0, null);
		}
			
		return ret;
	}

	public Polylist getResultAsList(int n, HashMap<String, String> references) throws ThinklabException {
		return kbox.getObjectAsListFromID(qresult.get(n, 0), references);
	}

	public int getResultCount() {
		return qresult == null ? 0 : qresult.nRows();
	}

	public int getResultOffset() {
		return offset;
	}

	public float getResultScore(int n) {
		return scores == null ? 1.0f : scores[n];
	}

	public int getTotalResultCount() {
		return nResults;
	}

	public void moveTo(int currentItem, int itemsPerPage)
			throws ThinklabException {
		// TODO Auto-generated method stub

	}

	public IValue getResult(int n, ISession session) throws ThinklabException {
		if (instances[n] == null)
			instances[n] = new ObjectReferenceValue(
					kbox.getObjectFromID(qresult.get(n, 0), session));
		return instances[n];
	}

	@Override
	public IValue getBestResult(ISession session) throws ThinklabException {
		
		int max = -1;
		float maxScore = -1.0f;
		
		for (int i = 0; i < getTotalResultCount(); i++)
			if (getResultScore(i) > maxScore) {
				max = i;
				maxScore = getResultScore(i);
			}
		
		if (max >= 0)
			return getResult(max, session);
		
		return null;
	}

	@Override
	public float setResultScore(int n, float score) {

		float prev = getResultScore(n);
		
		if (scores == null) {
			scores = new float[getTotalResultCount()];
		}
		
		scores[n] = score;
		
		return prev;
		
	}
}
