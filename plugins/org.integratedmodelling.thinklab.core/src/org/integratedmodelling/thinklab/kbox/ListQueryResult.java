package org.integratedmodelling.thinklab.kbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IQueriable;
import org.integratedmodelling.thinklab.interfaces.IQuery;
import org.integratedmodelling.thinklab.interfaces.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.ObjectReferenceValue;
import org.integratedmodelling.utils.Polylist;

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
	
	public ListQueryResult(IQuery query, IQueriable queriable, Collection<Polylist> lists) {
		
		this.query = query;
		this.queriable = queriable;
		
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
		// TODO Auto-generated method stub
		return lists.get(n);
	}

	public int getResultCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	public IValue getResultField(int n, String schemaField) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getResultOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Polylist getResultSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	public float getResultScore(int n) {
		return 1.0f;
	}

	public int getTotalResultCount() {
		return lists.size();
	}

	public void moveTo(int currentItem, int itemsPerPage)
			throws ThinklabException {
		// TODO Auto-generated method stub

	}

}
