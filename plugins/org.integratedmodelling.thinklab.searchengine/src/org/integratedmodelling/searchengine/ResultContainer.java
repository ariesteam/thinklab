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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueriable;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.literals.ObjectReferenceValue;
import org.integratedmodelling.thinklab.literals.TextValue;
import org.integratedmodelling.utils.MiscUtilities;
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
	
	public ResultContainer(SearchEngine s, IQuery q, int offset, int max) {
		this.searchEngine = s;
		this.queryString = q;
		this.offset = offset;
	}
	
	private IInstance getObjectFromDocument(Document doc, ISession session) throws ThinklabException {
		
		String id = doc.get("id");
		// FIXME don't know what this should be doing
		return /* session.importObject(id) */null;
	}
	
	public IQueriable getQueriable() {
		return searchEngine;
	}

	public IQuery getQuery() {
		return queryString;
	}

	/**
	 * TODO ensure we use proper type info and validation if fields have property names
	 */
	public IValue getResultField(int n, String schemaField) {
		
		Field f = results.get(n).getField(schemaField);
		return f == null ? null : new TextValue(f.stringValue());
	}

	public int getResultCount() {
		return scores.size();
	}

	public int getResultOffset() {
		return offset;
	}

	static String spaces(String name, String right, int max) {
		int nsp = max - name.length() - right.length();
		if (nsp <= 0)
			return " ";
		return MiscUtilities.createWhiteSpace(nsp, 0);
	}
	
	public void printResult(int n, PrintStream out) {
		String id = n + ". " + results.get(n).get("id");
		String score = "(score: " + scores.get(n) + ")";
		out.println(id + spaces(id, score, 80) + score);
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
		scores.set(n, score);
		return prev;
	}

	
}
