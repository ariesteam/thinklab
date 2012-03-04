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
package org.integratedmodelling.thinklab.kbox.sql;

import java.util.Iterator;

import org.integratedmodelling.collections.ReadOnlyList;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticLiteral;
import org.integratedmodelling.thinklab.constraint.Constraint;

public class SQLQueryResult extends ReadOnlyList<Object> {

	SQLKBox kbox = null;
	Constraint query = null;
	int nResults = 0;
	int offset = -1;
	int max = -1;
	private QueryResult qresult;
	ISemanticLiteral[] instances = null;
//	float[] scores = null;
//	private String[] metadata;
//	private Map<String, IConcept> metadataCatalog;
	
	// create from results of successful query
	public SQLQueryResult(QueryResult qres,
//			int totalres, int offset, int max, 
			Constraint query, 
//			String[] metadata, 
//			Map<String, IConcept> metadataCatalog, 
			SQLKBox kbox
			) {
		
		this.kbox = kbox;
		this.query = query;
//		nResults = totalres;
		this.qresult = qres;
//		this.metadata = metadata;
//		this.metadataCatalog = metadataCatalog;
//		instances = new IValue[this.qresult.nRows()];
	}
//
	// null result
	public SQLQueryResult() {
	}
	
	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Object get(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Iterator<Object> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

//
////	public IQueriable getQueriable() {
////		return kbox;
////	}
//
//	public IQuery getQuery() {
//		return query;
//	}
//
//	public IValue getResultField(int n, String schemaField) throws ThinklabException {
//		
//		IValue ret = null;
//		
//		if (metadata != null && qresult != null) {
//			int mind = -1;
//			for (int i = 0; i < metadata.length; i++) {
//				if (metadata[i].equals(schemaField)) {
//					mind = i + 1;
//					break;
//				}
//			}
//			if (mind > 0) {
//				ret = qresult.getValue(n, mind, metadataCatalog.get(metadata[mind -1]));
//			}
//		}
//		
//		if (ret != null)
//			return ret;
//		
//		if (instances[n] != null) {
//			/*
//			 * if we have the object, try getting its property
//			 */
//			IInstance i;
//			try {
//				i = instances[n].asObject();
//				ret = i.get(schemaField);
//			} catch (ThinklabException e) {
//				// ignore, it just means it's not there
//			}
//		} else if (qresult != null) {
//			
//			// ret = qresult.getValue(n, 0, null);
//		}
//			
//		return ret;
//	}
//
//	public IList getResultAsList(int n, HashMap<String, String> references) throws ThinklabException {
//		return kbox.getObjectAsListFromID(qresult.get(n, 0), references);
//	}
//
//	public int getResultCount() {
//		return qresult == null ? 0 : qresult.nRows();
//	}
//
//	public int getResultOffset() {
//		return offset;
//	}
//
//	public float getResultScore(int n) {
//		return scores == null ? 1.0f : scores[n];
//	}
//
//	public int getTotalResultCount() {
//		return nResults;
//	}
//
//	public void moveTo(int currentItem, int itemsPerPage)
//			throws ThinklabException {
//		// TODO Auto-generated method stub
//
//	}
//
//	public IValue getResult(int n, ISession session) throws ThinklabException {
//		if (instances[n] == null)
//			instances[n] = new ObjectValue(
//					kbox.getObjectFromID(qresult.get(n, 0), session));
//		return instances[n];
//	}
//
//
////	public float setResultScore(int n, float score) {
////
////		float prev = getResultScore(n);
////		
////		if (scores == null) {
////			scores = new float[getTotalResultCount()];
////		}
////		
////		scores[n] = score;
////		
////		return prev;
////		
////	}


}
