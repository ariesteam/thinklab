/**
 * SimpleQueryResult.java
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
package org.integratedmodelling.thinklab.kbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueriable;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.value.ObjectReferenceValue;
import org.integratedmodelling.utils.Polylist;

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

	public Polylist getResultAsList(int n, HashMap<String, String> references) throws ThinklabException {
		return getObject(n).toList(null, references);
	}

	public int getResultCount() {
		return results.size();
	}

	public int getResultOffset() {
		return offset;
	}

	public Polylist getResultSchema() {
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
		return new ObjectReferenceValue(results.get(n));
	}

	@Override
	public IValue getBestResult(ISession session) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float setResultScore(int n, float score) {
		// TODO Auto-generated method stub
		return 0;
	}


}
