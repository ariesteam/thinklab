///**
// * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
// * www.integratedmodelling.org. 
//
//   This file is part of Thinklab.
//
//   Thinklab is free software: you can redistribute it and/or modify
//   it under the terms of the GNU General Public License as published
//   by the Free Software Foundation, either version 3 of the License,
//   or (at your option) any later version.
//
//   Thinklab is distributed in the hope that it will be useful, but
//   WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//   General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.integratedmodelling.thinklab.kbox;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import org.integratedmodelling.collections.Pair;
//import org.integratedmodelling.exceptions.ThinklabException;
//import org.integratedmodelling.exceptions.ThinklabStorageException;
//import org.integratedmodelling.thinklab.api.knowledge.IValue;
//import org.integratedmodelling.thinklab.api.knowledge.query.IQueriable;
//import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
//import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
//import org.integratedmodelling.thinklab.api.lang.IList;
//import org.integratedmodelling.thinklab.api.runtime.ISession;
//
///**
// * a query result that presents a set of query results as a single cursor.
// * @author Ferdinando
// *
// */
//public class MultipleQueryResult implements IQueryResult {
//
//	int max = -1;
//	int ofs = 0;
//	int tot = 0;
//	
//	IQuery query = null;		
//	ArrayList<IQueryResult> results = new ArrayList<IQueryResult>();
//	ArrayList<Integer> counts = new ArrayList<Integer>();
//	
//	public MultipleQueryResult(IQuery q, int max, int ofs) {
//		this.query = q;
//		this.max = max;
//		this.ofs = ofs;
//	}
//	
//	public MultipleQueryResult(IQuery q) {
//		this.query = q;
//	}
//	
//	/*
//	 * add result; if we have more than we want, 
//	 * return false to notify it (the return value 
//	 * is basically an answer to "want more?")
//	 */
//	public boolean add(IQueryResult result) {
//		if (max > 0 && tot >= max)
//			return false;
//		int rc = result.getResultCount();
//		results.add(result);
//		counts.add(rc);
//		tot += rc;
//		return (max > 0) ? (tot < max) : true;
//	}
//
//	private Pair<Integer, IQueryResult> pickResult(int n) {
//		
//		int nr = 0, t = 0; 
//		IQueryResult q = null;
//		
//		if (n >= tot || n < 0)
//			return null;
//		
//		for (int i = 0; i < counts.size(); i++) {
//			if (n < (t + counts.get(i))) {
//				q = results.get(i);
//				nr = n - t;
//				break;
//			}
//			t += counts.get(i);
//		}
//		
//		return new Pair<Integer,IQueryResult>(nr, q);
//	}
//
//	@Override
//	public IQueriable getQueriable() {
//		return KBoxManager.get();
//	}
//
//	@Override
//	public IQuery getQuery() {
//		return query;
//	}
//
//	@Override
//	public IValue getResult(int n, ISession session)
//			throws ThinklabException {
//
//		Pair<Integer,IQueryResult> rr = pickResult(n);
//		if (rr != null) {
//			return rr.getSecond().getResult(rr.getFirst(), session);
//		}			
//		return null;
//	}
//
//	@Override
//	public IList getResultAsList(int n,
//			HashMap<String, String> references) throws ThinklabException {
//		Pair<Integer,IQueryResult> rr = pickResult(n);
//		if (rr != null) {
//			return rr.getSecond().getResultAsList(rr.getFirst(), references);
//		}			
//		return null;
//	}
//
//	@Override
//	public int getResultCount() {
//		return tot;
//	}
//
//	@Override
//	public IValue getResultField(int n, String schemaField)
//			throws ThinklabException {
//		Pair<Integer,IQueryResult> rr = pickResult(n);
//		if (rr != null) {
//			return rr.getSecond().getResultField(rr.getFirst(), schemaField);
//		}			
//		return null;
//	}
//
//	@Override
//	public int getResultOffset() {
//		return ofs;
//	}
//
//	@Override
//	public float getResultScore(int n) {
//		Pair<Integer,IQueryResult> rr = pickResult(n);
//		if (rr != null) {
//			return rr.getSecond().getResultScore(rr.getFirst());
//		}			
//		return 0.0f;
//	}
//
//	@Override
//	public int getTotalResultCount() {
//		return tot;
//	}
//
//	@Override
//	public void moveTo(int currentItem, int itemsPerPage)
//			throws ThinklabException {
//		throw new ThinklabStorageException("global kbox is read only");
//	}
//
//	@Override
//	public float setResultScore(int n, float score) {
//		Pair<Integer,IQueryResult> rr = pickResult(n);
//		if (rr != null) {
//			return rr.getSecond().setResultScore(rr.getFirst(), score);
//		}			
//		return 0.0f;
//	}
//	
//	@Override
//	public String toString() {
//		return "[" + getTotalResultCount() + " results]";
//	}
//}