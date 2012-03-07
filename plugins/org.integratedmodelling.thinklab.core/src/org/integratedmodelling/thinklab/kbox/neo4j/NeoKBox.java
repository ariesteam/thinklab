package org.integratedmodelling.thinklab.kbox.neo4j;

import java.net.URL;
import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.lang.SemanticAnnotation;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
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
	public long store(Object o) throws ThinklabException {
		
		SemanticAnnotation instance = Thinklab.get().conceptualize(o);		
		return storeInstanceList(instance, _db.getReferenceNode());
	}

	@Override
	public void remove(long handle) throws ThinklabException {
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

	long storeInstanceList(SemanticAnnotation list, Node node) {
		
		return 0;
	}

	@Override
	public String getUri() {
		return _url;
	}

	@Override
	public Object retrieve(long id) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object retrieve(long id, int flags) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeAll(IQuery query) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Object> query(IQuery query, int flags) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}
}
