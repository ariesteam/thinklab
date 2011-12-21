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

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueriable;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.runtime.ISession;

/**
 * Query result that wraps another and presents sets of results with a common value for one 
 * or more metadata fields as a single value. Results where the metadata field is empty are not
 * grouped and count as individual values. Specialized methods allow to retrieve the 
 * individual values and their multiplicity.
 * 
 * @author Ferdinando
 *
 */
public class GroupingQueryResult implements IQueryResult {

	ArrayList<Pair<String, ArrayList<Integer>>> _grouped = 
		new ArrayList<Pair<String,ArrayList<Integer>>>();
	
	IQueryResult _result = null;
	String _field = null;
	
	public GroupingQueryResult(IQueryResult result, String metadataField) 
		throws ThinklabException {
	
		_result = result;
		_field = metadataField;
		
		/* build grouping */
		for (int i = 0; i < result.getTotalResultCount(); i++) {

			IValue ss = result.getResultField(i, metadataField);

			String s  = ss == null ? "" : ss.toString();
			int j = -1;
			
			if (!s.isEmpty()) {
				for (int jj = 0; jj < _grouped.size(); jj++) {
					if (_grouped.get(jj).getFirst().equals(s)) {
						j = jj;
						break;
					}
				}
			}
			
			if (j < 0) {
				_grouped.add(new Pair<String,ArrayList<Integer>>(s, new ArrayList<Integer>()));
				j = _grouped.size() - 1;
			}
			
			_grouped.get(j).getSecond().add(i);
		}
	}
	
	@Override
	public IQueriable getQueriable() {
		return _result.getQueriable();
	}

	@Override
	public IQuery getQuery() {
		return _result.getQuery();
	}

	@Override
	public int getTotalResultCount() {
		return _grouped.size();
	}

	@Override
	public int getResultOffset() {
		return _result.getResultOffset();
	}

	@Override
	public int getResultCount() {
		return getTotalResultCount();
	}

	@Override
	public float getResultScore(int n) {
		return _grouped.get(n).getSecond().size();
	}

	/**
	 * 
	 * @param n
	 * @return
	 */
	public int getResultMultiplicity(int n) {
		return _grouped.get(n).getSecond().size();
	}

	
	@Override
	public float setResultScore(int n, float score) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IValue getResult(int n, ISession session) throws ThinklabException {
		if (_grouped.get(n).getSecond().size() > 1)
			/*
			 * TODO return as array value
			 */
			throw new ThinklabValidationException(
					"grouped result: result is multiple: cannot return through getResult");
		return _result.getResult(_grouped.get(n).getSecond().get(0), session);
	}
	
	/**
	 * 
	 * @param n
	 * @param idx
	 * @param session
	 * @return
	 * @throws ThinklabException
	 */
	public IValue getResult(int n, int idx, ISession session) throws ThinklabException {
		int oid = _grouped.get(n).getSecond().get(idx);
		return _result.getResult(oid, session);
	}

	@Override
	public IValue getResultField(int n, String schemaField)
			throws ThinklabException {
		if (_grouped.get(n).getSecond().size() > 1)
			/*
			 * TODO return as array value
			 */
			throw new ThinklabValidationException(
					"grouped result: result is multiple: cannot return through getResultField");
		return _result.getResultField(_grouped.get(n).getSecond().get(0), schemaField);
	}

	/**
	 * 
	 * @param n
	 * @param idx
	 * @param schemaField
	 * @return
	 * @throws ThinklabException
	 */
	public IValue getResultField(int n, int idx, String schemaField)
	throws ThinklabException {
		int oid = _grouped.get(n).getSecond().get(idx);
		return _result.getResultField(oid, schemaField);
	}

	@Override
	public IList getResultAsList(int n, HashMap<String, String> references)
			throws ThinklabException {
		if (_grouped.get(n).getSecond().size() > 1)
			
			/*
			 * TODO return as array value
			 */
			throw new ThinklabValidationException(
					"grouped result: result is multiple: cannot return through getResult");
		return _result.getResultAsList(_grouped.get(n).getSecond().get(0), references);
	}

	/**
	 * @param n index of result (group)
	 * @param idx index within group
	 * @param references
	 * @return
	 * @throws ThinklabException
	 */
	public IList getResultAsList(int n, int idx, HashMap<String, String> references)
			throws ThinklabException {
		int oid = _grouped.get(n).getSecond().get(idx);
		return _result.getResultAsList(oid, references);
	}

	
	@Override
	public void moveTo(int currentItem, int itemsPerPage)
			throws ThinklabException {
	}

	public String toString() {
		return "[" + _result.getTotalResultCount() + 
		" results in " + _grouped.size() + 
		" groups (by " + _field + ")]"; 
	}
	
}
