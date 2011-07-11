package org.integratedmodelling.thinklab.kbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueriable;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.literals.ObjectReferenceValue;

/**
 * Results that are initialized with lists only. Very incomplete.
 * 
 * @author Ferdinando Villa
 *
 */
public class ListQueryResult implements IQueryResult {

	ArrayList<Polylist> lists = new ArrayList<Polylist>();
	IQuery query = null;
	IQueriable queriable = null;
	float[] scores = null;
	Properties properties;
	
	public ListQueryResult(IQuery query, IQueriable queriable, Collection<Polylist> lists, Properties properties) {
		
		this.query = query;
		this.queriable = queriable;
		this.properties = properties;
		
		if (lists != null)
			for (Polylist l : lists) {
				this.lists.add(l);
			}
	}
	
	public void addList(Polylist l) {
		lists.add(l);
	}
	
	public IQueriable getQueriable() {
		return queriable;
	}

	public IQuery getQuery() {
		return query;
	}

	public IValue getResult(int n, ISession session) throws ThinklabException {
		
		Polylist l = lists.get(n);
		IInstance i = session.createObject(l);
		return new ObjectReferenceValue(i);
	}

	public Polylist getResultAsList(int n, HashMap<String, String> references)
			throws ThinklabException {
		return lists.get(n);
	}

	public int getResultCount() {
		return lists.size();
	}

	public IValue getResultField(int n, String schemaField) {
		return null;
	}

	public int getResultOffset() {
		return 0;
	}

	public Polylist getResultSchema() {
		return null;
	}

	public float getResultScore(int n) {
		return scores == null ? 1.0f : scores[n];
	}

	public int getTotalResultCount() {
		return lists.size();
	}

	public void moveTo(int currentItem, int itemsPerPage)
			throws ThinklabException {
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
