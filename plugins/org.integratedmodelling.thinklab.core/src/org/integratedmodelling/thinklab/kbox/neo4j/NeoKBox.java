package org.integratedmodelling.thinklab.kbox.neo4j;

import java.net.URL;
import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.neo4j.graphdb.Node;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class NeoKBox implements IKbox {

	EmbeddedGraphDatabase _db = null;
	String _url = null;
	
	public NeoKBox(String url) {
		
		URL urf = null;
		this._url = url;
		try {
			urf = new URL(url);
			_db = new EmbeddedGraphDatabase(urf.getFile());
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					_db.shutdown();
				}
			});
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
	}
	
	@Override
	public int store(Object o) throws ThinklabException {
		
		IList instance = null;
		if (o instanceof IList) {
			instance = (IList)o;
		} else if (o instanceof IConceptualizable) {
			instance = ((IConceptualizable)o).conceptualize();
		} else {
			throw new ThinklabValidationException("kbox: object cannot be stored as knowledge " + o + ": " + _url);
		}
		
		return storeInstanceList(instance, _db.getReferenceNode());
	}

	@Override
	public void remove(int handle) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Object> query(IQuery query) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * ----------------------------------------------------------------------------------
	 * implementation-specific methods
	 * ----------------------------------------------------------------------------------
	 */

	int storeInstanceList(IList list, Node node) {
		
		return 0;
	}
}
