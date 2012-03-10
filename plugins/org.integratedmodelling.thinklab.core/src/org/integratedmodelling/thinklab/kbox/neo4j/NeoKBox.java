package org.integratedmodelling.thinklab.kbox.neo4j;

import java.net.URL;
import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.ISemantics;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
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

		long ret = -1;
		ISemanticObject instance = Thinklab.get().annotate(o);		
		Transaction tx = _db.beginTx();
		try {
			ret = storeInstanceInternal(instance.getSemantics(), _db.getReferenceNode(), null).getId();
		} finally {
			tx.finish();
		}
		return ret;
	}

	private Node storeInstanceInternal(ISemantics instance, Node referenceNode, final IProperty property) {

		Node node = _db.createNode();
		
		if (property != null) {
			/*
			 * create relationship
			 */
			referenceNode.createRelationshipTo(node, new RelationshipType() {
				@Override
				public String name() {
					return property.toString();
				}
			});
		}
		
		for (ISemantics s : instance.getRelationships()) {
			if (s.isLiteral()) {
				storeProperty(node, s);
			} else {
				storeObject(node, s);
			}
		}
		
		return node;
	}

	private void storeProperty(Node node, ISemantics s) {

		IProperty p = s.getProperty();
		node.setProperty(p.toString(), translateLiteral(s.getTargetLiteral()));
	}

	private Object translateLiteral(Object literal) {
		return literal;
	}

	private void storeObject(Node node, ISemantics s) {
		storeInstanceInternal(s.getTargetSemantics(), node, s.getProperty());
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
	public List<ISemanticObject> query(IQuery query) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * ----------------------------------------------------------------------------------
	 * implementation-specific methods
	 * ----------------------------------------------------------------------------------
	 */

//	long storeInstanceList(SemanticAnnotation list, Node node) {
//		
//		return 0;
//	}

	@Override
	public String getUri() {
		return _url;
	}

	@Override
	public ISemanticObject retrieve(long id) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void removeAll(IQuery query) {
		// TODO Auto-generated method stub
		
	}

}
