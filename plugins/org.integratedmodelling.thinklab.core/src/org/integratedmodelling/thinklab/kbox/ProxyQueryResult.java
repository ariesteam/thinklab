package org.integratedmodelling.thinklab.kbox;

import java.util.HashMap;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueriable;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
import org.integratedmodelling.thinklab.api.runtime.ISession;

/**
 * Useless proxy class that simply forwards all methods to another query result. Used to
 * simplify creation of proxy classes that can extend this one and only reimplement the
 * methods they need.
 * 
 * @author Ferdinando
 *
 */
public class ProxyQueryResult implements IQueryResult {

	IQueryResult _proxy;
	
	public ProxyQueryResult(IQueryResult r) {
		_proxy = r;
	}
	
	@Override
	public IValue getBestResult(ISession session) throws ThinklabException {
		return _proxy.getBestResult(session);
	}

	@Override
	public IQueriable getQueriable() {
		return _proxy.getQueriable();
	}

	@Override
	public IQuery getQuery() {
		return _proxy.getQuery();
	}

	@Override
	public IValue getResult(int n, ISession session) throws ThinklabException {
		return _proxy.getResult(n, session);
	}

	@Override
	public Polylist getResultAsList(int n, HashMap<String, String> references)
			throws ThinklabException {
		return _proxy.getResultAsList(n, references);
	}

	@Override
	public int getResultCount() {
		return _proxy.getResultCount();
	}

	@Override
	public IValue getResultField(int n, String schemaField)
			throws ThinklabException {
		return _proxy.getResultField(n, schemaField);
	}

	@Override
	public int getResultOffset() {
		return _proxy.getResultOffset();
	}

	@Override
	public float getResultScore(int n) {
		return _proxy.getResultScore(n);
	}

	@Override
	public int getTotalResultCount() {
		return _proxy.getTotalResultCount();
	}

	@Override
	public void moveTo(int currentItem, int itemsPerPage)
			throws ThinklabException {
		_proxy.moveTo(currentItem, itemsPerPage);
	}

	@Override
	public float setResultScore(int n, float score) {
		return _proxy.setResultScore(n, score);
	}

}
