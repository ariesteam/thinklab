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
package org.integratedmodelling.searchengine;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticLiteral;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueriable;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.literals.ObjectValue;
import org.integratedmodelling.thinklab.literals.TextValue;
import org.integratedmodelling.utils.MiscUtilities;


/*
 * a simple container for results of a query. Also acts as a container for paging information, 
 * although there's no requirement for the values to represent the specific pages.
 */
public class ResultContainer implements List<Object> {

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
	public ISemanticLiteral getResultField(int n, String schemaField) {
		
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

	public IList getResultAsList(int n, HashMap<String, String> references) {

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
	
	public ISemanticLiteral getResult(int n, ISession session) throws ThinklabException {
		
		return new ObjectValue(getObjectFromDocument(results.get(n), session));
	}
//
//	@Override
//	public IValue getBestResult(ISession session) throws ThinklabException {
//
//		int max = -1;
//		float maxScore = -1.0f;
//		
//		for (int i = 0; i < getTotalResultCount(); i++)
//			if (getResultScore(i) > maxScore) {
//				max = i;
//				maxScore = getResultScore(i);
//			}
//		
//		if (max >= 0)
//			return getResult(max, session);
//		
//		return null;
//		
//	}

	public float setResultScore(int n, float score) {

		float prev = getResultScore(n);
		scores.set(n, score);
		return prev;
	}

	@Override
	public boolean add(Object e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void add(int index, Object element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addAll(Collection<? extends Object> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Object> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<Object> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<Object> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<Object> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object set(int index, Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Object> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
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

	
}
