package org.integratedmodelling.thinklab.kbox.neo4j;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabUnsupportedOperationException;
import org.integratedmodelling.lang.Quantifier;
import org.integratedmodelling.list.ReferenceList;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.annotation.SemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IKnowledge;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.knowledge.query.IOperator;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;
import org.integratedmodelling.thinklab.interfaces.knowledge.SemanticQuery;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;
import org.integratedmodelling.thinklab.interfaces.storage.KboxTypeAdapter;
import org.integratedmodelling.thinklab.kbox.KBoxResult;
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

	static final String TYPE_PROPERTY = "_type";	
	static final String HASNODE_PROPERTY = "_hasnode";
	
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
		this._url = url;
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
			
			Node node = storeInstanceInternal(instance, new HashMap<ISemanticObject, Node>());
			_db.getReferenceNode().createRelationshipTo(node, new RelationshipType() {
				@Override
				public String name() {
					return HASNODE_PROPERTY;
				}
			});

			ret = node.getId();
			tx.success();
			
		} catch (Exception e) {
			
			tx.failure();
			throw new ThinklabIOException(e.getMessage());
	
		} finally {
		
			tx.finish();
		}
		return ret;
	}

	private Node storeInstanceInternal(ISemanticObject instance, Map<ISemanticObject, Node> refs) throws ThinklabException {

		Node node = refs.get(instance);
		
		if (node != null)
			return node;
		
		node = _db.createNode();
		refs.put(instance, node);

		node.setProperty(TYPE_PROPERTY, instance.getDirectType().toString());
		
		for (final Pair<IProperty, ISemanticObject> s : instance.getRelationships()) {
			
			if (s.getSecond().isLiteral()) {
				storeProperty(node, s.getFirst(), s.getSecond());
			} else {
				
				Node target = storeInstanceInternal(s.getSecond(), refs);
				node.createRelationshipTo(target, new RelationshipType() {
					@Override
					public String name() {
						return toId(s.getFirst());
					}
				});
			}
		}
		
		return node;
	}

	private void storeProperty(Node node, IProperty p, ISemanticObject s) throws ThinklabException {

		KboxTypeAdapter adapter = _typeAdapters.get(s.getDirectType());
		
		if (adapter == null)
			throw new ThinklabUnsupportedOperationException("kbox: cannot store literal of type " +
					s.getDirectType());
		
		adapter.setAndIndexProperty(node.getId(), this, p, s.getObject());
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
		
		if (! (query instanceof SemanticQuery)) {
			throw new ThinklabUnsupportedOperationException("query type not supported: " + query);
		}
		
		Set<Long> matches = queryObjects((SemanticQuery) query);
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
		
		return new KBoxResult(this, res);
	}

	@Override
	public String getUri() {
		return _url;
	}

	@Override
	public ISemanticObject retrieve(long id) throws ThinklabException {
		return new SemanticObject(
				retrieveList(_db.getNodeById(id), new HashMap<Node, IReferenceList>(), new ReferenceList()), 
				null);
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
		
		return new KBoxResult(this, res);
	}

	/*
	 * non-public
	 */
	private IReferenceList retrieveList(Node node, HashMap<Node, IReferenceList> refs, ReferenceList root) throws ThinklabException {
		
		IReferenceList ref = null;
		if (refs.containsKey(node)) {
			return refs.get(node);
		} else {
			ref = root.getForwardReference();
			refs.put(node, ref);
		}
		
		ArrayList<Object> rl = new ArrayList<Object>();
		IConcept concept = Thinklab.c(node.getProperty(TYPE_PROPERTY).toString());
		rl.add(concept);

		/*
		 * literals
		 */
		for (String p : node.getPropertyKeys()) {
			// skip system properties
			if (p.startsWith("_"))
				continue;
			rl.add(root.newList(Thinklab.p(fromId(p)), Thinklab.get().conceptualize(node.getProperty(p))));
		}

		/*
		 * follow outgoing relationships to other objects
		 */
		for (Relationship r : node.getRelationships(Direction.OUTGOING)) {
			rl.add(root.newList(Thinklab.p(fromId(r.getType().name())), retrieveList(r.getEndNode(), refs, root)));
		}
		
		return  (IReferenceList) ref.resolve(root.newList(rl.toArray()));		
	}

	private Set<Long> queryObjects(SemanticQuery query) {
		
		Set<Long> ret = null;
				
		for (SemanticQuery r : query.getRestrictions()) {
			
			Set<Long> rr = queryObjects(r);
			
			if (ret == null)
				ret = rr;
			
			switch (query.getQuantifier().getType()) {
			case Quantifier.ALL:
				break;
			case Quantifier.ANY:
				break;
			case Quantifier.NONE:
				break;
			case Quantifier.RANGE:
				break;
			case Quantifier.EXACT:
				break;
			}
		}
		
		if (query instanceof IOperator && ((IOperator)query).isLiteral()) {
			
//			KboxTypeAdapter ta = _typeAdapters.get(lconc);
			
		} else if ( query.getSubject() != null) {
			ret = getSemanticClosure(query.getSubject());			
		}
		
		
		return ret == null ? new HashSet<Long>() : ret;
	}

	private Set<Long> getSemanticClosure(IConcept c) {
		Set<Long> ret = new HashSet<Long>();
		return ret;
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
	 * insert default type adapters. More can be added for specific types.
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
		
		registerTypeAdapter(Thinklab.c(NS.INTEGER),
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
		
		registerTypeAdapter(Thinklab.c(NS.DOUBLE),
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
		registerTypeAdapter(Thinklab.c(NS.FLOAT),
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
		registerTypeAdapter(Thinklab.c(NS.LONG),
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

	@Override
	public void open() {
		try {
			if (_db == null) {
				URL urf = new URL(_url);
				_db = new EmbeddedGraphDatabase(urf.getFile());
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						if (_db != null)
							_db.shutdown();
					}
				});
			}
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	@Override
	public void close() {
		if (_db != null) {
			_db.shutdown();
			_db = null;
		} 
	}


	
}
