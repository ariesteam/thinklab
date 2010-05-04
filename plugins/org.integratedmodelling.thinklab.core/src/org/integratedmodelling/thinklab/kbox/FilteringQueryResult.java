package org.integratedmodelling.thinklab.kbox;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.utils.Polylist;

/**
 * A proxy query result that filters another through an arbitrary function and only 
 * exposes the results that match the conditions in it.
 * 
 * @author Ferdinando
 *
 */
public abstract class FilteringQueryResult extends ProxyQueryResult {

	ArrayList<Integer> _keep = new ArrayList<Integer>();
	
	public FilteringQueryResult(IQueryResult r) {
		super(r);
		for (int i = 0; i < r.getTotalResultCount(); i++) {
			if (isAcceptable(r, i)) {
				_keep.add(i);
			}
		}
	}

	/**
	 * Redefine this one to return true if the i-th result in the passed
	 * query result is to be kept.
	 * 
	 * @param r
	 * @param i
	 * @return
	 */
	protected abstract boolean isAcceptable(IQueryResult r, int i);

	@Override
	public IValue getResult(int n, ISession session) throws ThinklabException {
		return super.getResult(_keep.get(n), session);
	}

	@Override
	public Polylist getResultAsList(int n, HashMap<String, String> references)
			throws ThinklabException {
		return super.getResultAsList(_keep.get(n), references);
	}

	@Override
	public int getResultCount() {
		return _keep.size();
	}

	@Override
	public IValue getResultField(int n, String schemaField)
			throws ThinklabException {
		return super.getResultField(_keep.get(n), schemaField);
	}

	@Override
	public float getResultScore(int n) {
		return super.getResultScore(_keep.get(n));
	}

	@Override
	public int getTotalResultCount() {
		return _keep.size();
	}

	@Override
	public float setResultScore(int n, float score) {
		return super.setResultScore(_keep.get(n), score);
	}
		
}
