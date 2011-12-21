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
package org.integratedmodelling.thinklab.kbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueriable;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.literals.ObjectValue;


public class SimpleQueryResult implements IQueryResult {
	
	ArrayList<IInstance> results = new ArrayList<IInstance>();
	Hashtable<String, Integer> order = new Hashtable<String, Integer>();
	
	int offset = 0;
	int total = 0;
	
	public SimpleQueryResult(int offset, int total) {
		
		this.offset = offset;
		this.total = total;
		
	}

	/**
	 * to be called in the proper order when the results are generated.
	 * @param i
	 */
	public void add(IInstance i) {
		
		int nnext = results.size();
		results.add(i);
		order.put(i.getLocalName(), new Integer(nnext));
	}
	
	public IQueriable getQueriable() {
		return null;
	}

	public IQuery getQuery() {
		return null;
	}

	public IValue getResultField(int n, String schemaField) throws ThinklabException {
		
		IInstance obj = getObject(n);
		return obj.get(schemaField);
	}

	private IInstance getObject(int n) {
		return results.get(n);
	}

	public IList getResultAsList(int n, HashMap<String, String> references) throws ThinklabException {
		return getObject(n).asList(null);
	}

	public int getResultCount() {
		return results.size();
	}

	public int getResultOffset() {
		return offset;
	}

	public IList getResultSchema() {
		return null;
	}

	public float getResultScore(int n) {
		return (float)1.0;
	}

	public int getTotalResultCount() {
		return total;
	}

	public void moveTo(int currentItem, int itemsPerPage)
			throws ThinklabException {
		// TODO Auto-generated method stub
	}

	public IValue getResult(int n, ISession session) throws ThinklabException {
		return new ObjectValue(results.get(n));
	}

	@Override
	public float setResultScore(int n, float score) {
		// TODO Auto-generated method stub
		return 0;
	}


}
