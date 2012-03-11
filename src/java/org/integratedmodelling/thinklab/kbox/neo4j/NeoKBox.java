package org.integratedmodelling.thinklab.kbox.neo4j;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IKnowledge;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.ISemantics;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;
import org.integratedmodelling.thinklab.interfaces.storage.KboxTypeAdapter;
import org.integratedmodelling.thinklab.knowledge.SemanticObject;
import org.integratedmodelling.thinklab.knowledge.Semantics;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class NeoKBox implements IKbox {

	private static final String TYPE_PROPERTY = "_type";	
	private static final String HASNODE_PROPERTY = "_hasnode";
	
	private static IntelligentMap<KboxTypeAdapter> _typeAdapters = null;
	
	private static String toId(IKnowledge c) {
		return c.toString().replace(':', '_');
	}

	private static String fromId(String c) {
		return c.toString().replace('_', ':');
	}

	EmbeddedGraphDatabase _db = null;
	String _url = null;
	List<IProperty> _sortProperties;
	
	public NeoKBox(String url) {
		
		if (_typeAdapters == null) {
			initializeTypeAdapters();
		}
		
		URL urf = null;
		this._url = url;

		try {
			urf = new URL(url);
			_db = new EmbeddedGraphDatabase(urf.getFile());
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					if (_db != null)
						_db.shutdown();
				}
			});
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
	}
	
	/**
	 * Register a new type adapter for the a literal type that we want handled.
	 * 
	 * @param type
	 * @param type
	 */
	public static void registerTypeAdapter(IConcept type, KboxTypeAdapter adapter) {
		
		if (_typeAdapters == null)	{
			_typeAdapters = new IntelligentMap<KboxTypeAdapter>();
		}
		_typeAdapters.put(type, adapter);
		
	}
	
	@Override
	public synchronized long store(Object o) throws ThinklabException {

		long ret = -1;
		ISemanticObject instance = Thinklab.get().annotate(o);		
		Transaction tx = _db.beginTx();

		try {
			ret = storeInstanceInternal(instance.getSemantics(), _db.getReferenceNode(), null).getId();
			tx.success();
		} catch (Exception e) {
			tx.failure();
			throw new ThinklabIOException(e.getMessage());
		} finally {
			tx.finish();
		}
		return ret;
	}

	private Node storeInstanceInternal(ISemantics instance, Node referenceNode, final IProperty property) throws ThinklabException {

		Node node = _db.createNode();
		node.setProperty(TYPE_PROPERTY, instance.getConcept().toString());
		
		if (property != null) {
			/*
			 * create relationship
			 */
			referenceNode.createRelationshipTo(node, new RelationshipType() {
				@Override
				public String name() {
					return toId(property);
				}
			});
		} else {
			referenceNode.createRelationshipTo(node, new RelationshipType() {
				@Override
				public String name() {
					return HASNODE_PROPERTY;
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

	private void storeProperty(Node node, ISemantics s) throws ThinklabException {

		IProperty p = s.getProperty();
		KboxTypeAdapter adapter = _typeAdapters.get(s.getTargetSemantics().getConcept());
		
		if (adapter == null)
			throw new ThinklabUnimplementedFeatureException("kbox: cannot store literal of type " +
					s.getTargetSemantics().getConcept());
		
		adapter.setAndIndexProperty(node.getId(), this, p, s.getTargetSemantics().getLiteral());
	}


	private void storeObject(Node node, ISemantics s) throws ThinklabException {
		storeInstanceInternal(s.getTargetSemantics(), node, s.getProperty());
	}

	@Override
	public void remove(long handle) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() throws ThinklabException {

		try {
			URL urf = new URL(_url);
			File dir = new File(urf.getFile());
			_db.shutdown();
			FileUtils.deleteDirectory(dir);
			_db = new EmbeddedGraphDatabase(urf.getFile());
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
	}

	@Override
	public List<ISemanticObject> query(IQuery query) throws ThinklabException {
		Set<Long> matches = queryObjects(query);
		return applySortingStrategy(matches);
	}
	
	private List<ISemanticObject> applySortingStrategy(Set<Long> matches) {
		
		ArrayList<Long> res = new ArrayList<Long>(matches);
		
		/*
		 * if we have a list of properties for the top node to sort against,
		 * create the appropriate Comparator and sort the nodes based on their
		 * properties.
		 */
		if (_sortProperties != null) {
			
			/*
			 * TODO
			 */
		}
		
		return new NeoKBoxResult(this, res);
	}

	@Override
	public String getUri() {
		return _url;
	}

	@Override
	public ISemanticObject retrieve(long id) throws ThinklabException {
		IList semantics = retrieveList(_db.getNodeById(id));
		return new SemanticObject(new Semantics(semantics, Thinklab.get()), null);
	}
	
	@Override
	public void removeAll(IQuery query) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ISemanticObject> retrieveAll() throws ThinklabException {
		
		ArrayList<Long> res = new ArrayList<Long>();
		Traverser traverser = _db.getReferenceNode().traverse(
				Order.DEPTH_FIRST, 
				StopEvaluator.DEPTH_ONE, 
				ReturnableEvaluator.ALL_BUT_START_NODE, 
				new RelationshipType() {
					@Override
					public String name() {
						return HASNODE_PROPERTY;
					}
				},
				Direction.OUTGOING);
				
		for (Node n : traverser.getAllNodes()) {
			res.add(n.getId());
		}
		
		return new NeoKBoxResult(this, res);
	}

	/*
	 * non-public
	 */
	private IList retrieveList(Node node) throws ThinklabException {
		
		ArrayList<Object> rl = new ArrayList<Object>();
		IConcept concept = Thinklab.c(node.getProperty(TYPE_PROPERTY).toString());
		rl.add(concept);
		
		/*
		 * follow outgoing relationships to other objects
		 */
		for (Relationship r : node.getRelationships(Direction.OUTGOING)) {
			rl.add(PolyList.list(Thinklab.p(fromId(r.getType().name())), retrieveList(r.getEndNode())));
		}

		/*
		 * literals
		 */
		for (String p : node.getPropertyKeys()) {
			// skip system properties
			if (p.startsWith("_"))
				continue;
			rl.add(PolyList.list(Thinklab.p(fromId(p)), Thinklab.get().conceptualize(node.getProperty(p))));
		}
		
		return PolyList.fromCollection(rl);		
	}

	private Set<Long> queryObjects(IQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	/*
	 * class to simplify handling of base types. 
	 */
	public abstract class TypeAdapter implements KboxTypeAdapter {

		protected abstract void setAndIndex(Node node, IProperty property, Object value);

		@Override
		public void setAndIndexProperty(long id, IKbox kbox,
				IProperty property, Object value) {
			
			Node node = _db.getNodeById(id);
			setAndIndex(node, property, value);
		}	
	}
	
	/*
	 * -----------------------------------------------------------------------------------------------------
	 * type handling
	 * -----------------------------------------------------------------------------------------------------
	 */
	
	/*
	 * insert default type adapters
	 */
	private void initializeTypeAdapters() {

		registerTypeAdapter(Thinklab.c(NS.TEXT),
				new TypeAdapter() {
					
					@Override
					protected void setAndIndex(Node node, IProperty property, Object value) {
						node.setProperty(toId(property), value);
						Index<Node> index = _db.index().forNodes(toId(property));
						index.add(node, toId(property), value);
					}

					@Override
					public Set<Long> submitLiteralQuery(IProperty property,
							IKbox kbox, IConcept queryType, Object... arguments) {
						// TODO Auto-generated method stub
						return null;
					}
				});

	}


	
}
