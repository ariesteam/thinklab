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
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Properties;
//
//import org.integratedmodelling.exceptions.ThinklabException;
//import org.integratedmodelling.thinklab.api.knowledge.IInstance;
//import org.integratedmodelling.thinklab.api.knowledge.IValue;
//import org.integratedmodelling.thinklab.api.knowledge.query.IQueriable;
//import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
//import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
//import org.integratedmodelling.thinklab.api.lang.IList;
//import org.integratedmodelling.thinklab.api.runtime.ISession;
//import org.integratedmodelling.thinklab.literals.ObjectValue;
//
///**
// * Results that are initialized with lists only. Very incomplete.
// * 
// * @author Ferdinando Villa
// *
// */
//public class ListQueryResult implements IQueryResult {
//
//	ArrayList<IList> lists = new ArrayList<IList>();
//	IQuery query = null;
//	IQueriable queriable = null;
//	float[] scores = null;
//	Properties properties;
//	
//	public ListQueryResult(IQuery query, IQueriable queriable, Collection<IList> lists, Properties properties) {
//		
//		this.query = query;
//		this.queriable = queriable;
//		this.properties = properties;
//		
//		if (lists != null)
//			for (IList l : lists) {
//				this.lists.add(l);
//			}
//	}
//	
//	public void addList(IList l) {
//		lists.add(l);
//	}
//	
//	public IQueriable getQueriable() {
//		return queriable;
//	}
//
//	public IQuery getQuery() {
//		return query;
//	}
//
//	public IValue getResult(int n, ISession session) throws ThinklabException {
//		
//		IList l = lists.get(n);
//		IInstance i = session.createObject(l);
//		return new ObjectValue(i);
//	}
//
//	public IList getResultAsList(int n, HashMap<String, String> references)
//			throws ThinklabException {
//		return lists.get(n);
//	}
//
//	public int getResultCount() {
//		return lists.size();
//	}
//
//	public IValue getResultField(int n, String schemaField) {
//		return null;
//	}
//
//	public int getResultOffset() {
//		return 0;
//	}
//
//	public IList getResultSchema() {
//		return null;
//	}
//
//	public float getResultScore(int n) {
//		return scores == null ? 1.0f : scores[n];
//	}
//
//	public int getTotalResultCount() {
//		return lists.size();
//	}
//
//	public void moveTo(int currentItem, int itemsPerPage)
//			throws ThinklabException {
//	}
//
//	@Override
//	public float setResultScore(int n, float score) {
//		
//		float prev = getResultScore(n);
//		
//		if (scores == null) {
//			scores = new float[getTotalResultCount()];
//		}
//		
//		scores[n] = score;
//		
//		return prev;
//	}
//
//}
