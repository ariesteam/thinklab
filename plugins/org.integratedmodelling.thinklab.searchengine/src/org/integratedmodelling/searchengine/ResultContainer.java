/**
 * ResultContainer.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabSearchEnginePlugin.
 * 
 * ThinklabSearchEnginePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabSearchEnginePlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.searchengine;

import java.util.HashMap;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IQueriable;
import org.integratedmodelling.thinklab.interfaces.IQuery;
import org.integratedmodelling.thinklab.interfaces.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.ObjectReferenceValue;
import org.integratedmodelling.utils.Polylist;


/*
 * a simple container for results of a query. Also acts as a container for paging information, 
 * although there's no requirement for the values to represent the specific pages.
 */
public class ResultContainer implements IQueryResult {

	ArrayList<Document> results = new ArrayList<Document>();
	ArrayList<Float> scores = new ArrayList<Float>();
	
	private SearchEngine searchEngine;
	private IQuery queryString;
	private int totalResultCount;
	private int offset;
	private int maxResults;
	private Polylist schema;
	
	public ResultContainer(SearchEngine s, IQuery q, Polylist schema, int offset, int max) {
		this.searchEngine = s;
		this.queryString = q;
		this.offset = offset;
		this.maxResults = max;
		this.schema = schema;
	}
	
	private IInstance getObjectFromDocument(Document doc, ISession session) throws ThinklabException {
		
		String id = doc.get("id");
		IInstance ret = null;
		try {
			ret = session.importObject(id);
		} catch (ThinklabException e) {
			// FIXME a temp fix - just return null, but this shouldn't happen
		}
		return ret;
	}
	
	public IQueriable getQueriable() {
		return searchEngine;
	}

	public IQuery getQuery() {
		return queryString;
	}

	public Object getResultField(int n, String schemaField) {
		
		Field f = results.get(n).getField(schemaField);
		return f == null ? null : f.stringValue();
	}

	public Object getResultField(int n, int schemaIndex) {
		return getResultField(n, schema.array()[schemaIndex].toString());
	}

	public int getResultCount() {
		return scores.size();
	}

	public int getResultOffset() {
		// TODO Auto-generated method stub
		return offset;
	}

	public Polylist getResultSchema() {
		return schema;
	}

	public float getResultScore(int n) {
		return scores.get(n);
	}

	public int getTotalResultCount() {
		return totalResultCount;
	}

	public void moveTo(int currentItem, int itemsPerPage)
			throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	public void parseResultSchema(Polylist list) {
		// TODO Auto-generated method stub
	}

	public Polylist getResultAsList(int n, HashMap<String, String> references) {

//		try {
//			return getObjectFromDocument(results.get(n)).toList(null, references);
//		} catch (ThinklabException e) {
//			throw new ThinklabRuntimeException(e);
//		}
		return null;
	}


	public void setResultCount(int length) {
		totalResultCount = length;
	}

	void addDocument(Document doc, float score) {
		results.add(doc);
		scores.add((float) Math.round(score * 100.0));
	}
	
	@Override
	public IValue getResult(int n, ISession session) throws ThinklabException {
		
		return new ObjectReferenceValue(getObjectFromDocument(results.get(n), session));
	}

	@Override
	public HashMap<String, IValue> getResultMetadata(int n) {
		// TODO Auto-generated method stub
		return null;
	}
	
//	int offset = 0;
//	int max = 10;
//	int page = 0;
//	
//	public class Result {
//		public IKnowledgeSubject result;
//		public double score;
//		
//		public Result(IKnowledgeSubject result, double d) {
//			this.result = result;
//			this.score = d;
//		}
//		
//		public IKnowledgeSubject getObject() {
//			return result;
//		}
//		
//		public int getScore() {
//			return (int) Math.round(score * 100.0);
//		}
//	}
//	
//	ArrayList<Result> results = new ArrayList<Result>();
//	private int totalResultCount;
//	
//	public ResultContainer(int offset, int max, int page) {
//		this.offset = offset;
//		this.max = max;
//		this.page = page;
//	}
//
//	public ResultContainer() {
//		// TODO Auto-generated constructor stub
//	}
//	
//	public void add(IKnowledgeSubject result, double d) {
//		results.add(new Result(result, d));
//	}
//
//	public void add(String resultID, float score) throws ThinklabException {
//
//		IKnowledgeSubject result = KnowledgeManager.KM().retrieveConcept(resultID);
//		if (result == null)
//			result = KnowledgeManager.KM().retrieveInstance(resultID);
//		
//		if (result == null)
//			throw new ThinklabResourceNotFoundException("internal: resource " + resultID + " not found in knowledge base");
//		
//		results.add(new Result(result, score));
//	}
//
//		
//	public IKnowledgeSubject getResult(int i) {
//		return results.get(i).result;
//	}
//	
//	public int getScore(int i) {
//		return (int) Math.round(results.get(i).score * 100.0);
//	}
//	
//	public void setTotalResultCount(int ret) {
//		totalResultCount = ret;
//	}
//	
//	public int getTotalResultCount() {
//		return totalResultCount;
//	}
	
}
