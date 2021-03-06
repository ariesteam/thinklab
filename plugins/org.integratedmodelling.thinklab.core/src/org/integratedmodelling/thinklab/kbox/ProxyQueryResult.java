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
package org.integratedmodelling.thinklab.kbox;

import java.util.HashMap;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueriable;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.utils.Polylist;

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
