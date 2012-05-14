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
import org.integratedmodelling.list.ReferenceList;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IKnowledge;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.knowledge.query.IOperator;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.lang.IMetadataHolder;
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
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.Traversal;

public class NeoKBox implements IKbox {

	static final String TYPE_PROPERTY = "_type";	
	static final String HASNODE_PROPERTY = "_hasnode";
	
	/*
	 * 
	 */
	static final String BASE_INDEX = "pindex";
	
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
		ISemanticObject<?> instance = Thinklab.get().annotate(o);		

		Transaction tx = _db.beginTx();

		try {
			
			Node node = storeInstanceInternal(instance, new HashMap<ISemanticObject<?>, Node>());
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

	private Node storeInstanceInternal(ISemanticObject<?> instance, Map<ISemanticObject<?>, Node> refs) throws ThinklabException {

		Node node = refs.get(instance);
		
		if (node != null)
			return node;
		
		node = _db.createNode();
		refs.put(instance, node);

		node.setProperty(TYPE_PROPERTY, instance.getDirectType().toString());
		
		for (final Pair<IProperty, ISemanticObject<?>> s : instance.getRelationships()) {
			
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

	private void storeProperty(Node node, IProperty p, ISemanticObject<?> s) throws ThinklabException {

		KboxTypeAdapter adapter = _typeAdapters.get(s.getDirectType());
		
		if (adapter == null)
			throw new ThinklabUnsupportedOperationException("kbox: cannot store literal of type " +
					s.getDirectType());
		
		adapter.setAndIndexProperty(node.getId(), this, p, s.demote());
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
	public List<ISemanticObject<?>> query(IQuery query) throws ThinklabException {
		
		if (query != null && !(query instanceof SemanticQuery)) {
			throw new ThinklabUnsupportedOperationException("query type not supported: " + query);
		}
		
		return new KBoxResult(this, queryObjects((SemanticQuery) query));
	}
	
//	private List<ISemanticObject> applySortingStrategy(Set<Long> matches) {
//		
//		ArrayList<Long> res = new ArrayList<Long>(matches);
//		
//		/*
//		 * if we have a list of properties for the top node to sort against,
//		 * create the appropriate Comparator and sort the nodes based on their
//		 * properties.
//		 */
//		if (_sortProperties != null) {
//			
//			/*
//			 * TODO
//			 */
//		}
//		
//		return new KBoxResult(this, res);
//	}

	@Override
	public String getUri() {
		return _url;
	}

	@Override
	public ISemanticObject<?> retrieve(long id) throws ThinklabException {
		return Thinklab.get().entify(retrieveList(_db.getNodeById(id), new HashMap<Node, IReferenceList>(), new ReferenceList()));
	}
	
	@Override
	public void removeAll(IQuery query) {
		for (long l : queryObjects((SemanticQuery) query)) {
			try {
				remove(l);
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
	}

	@Override
	public List<ISemanticObject<?>> retrieveAll() throws ThinklabException {
		
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

	private List<ISemanticObject<?>> query(SemanticQuery query) {
		
		Set<Node> results = retrieveMatches(query, new HashSet<Node>());
		return applySorting(results, query);
	}
	
	private List<ISemanticObject<?>> applySorting(Set<Node> results, SemanticQuery query) {

		ArrayList<Long> ret = new ArrayList<Long>();
		if (query instanceof IMetadataHolder) {
			/*
			 * TODO sort if the query's metadata specify it
			 */
		} else {
			for (Node n : results) 
				ret.add(n.getId());
		}
		
		return new KBoxResult(this, ret);
	}

	private Set<Node> retrieveMatches(SemanticQuery query, HashSet<Node> hashSet) {

		HashSet<Node> ret = new HashSet<Node>();
		
		/*
		 * create the base index search following the restrictions structure
		 */
		
		/*
		 * lookup matching nodes; if 0, return empty set
		 */
		
		/*
		 * for each object query (or special literal), recurse and intersect
		 * result set appropriately
		 */
		
		return ret;
	}

	
	/*
	 * THE METHODS BELOW ARE NOT WHAT WE SHOULD USE 
	 */
	private List<Long> queryObjects(SemanticQuery query) {
		
		List<Long> ret = new ArrayList<Long>();
		
		TraversalDescription td = rewrite(query);
		
		if (td != null) {
			for (Node n : td.traverse(_db.getReferenceNode()).nodes()) {
				ret.add(n.getId());
			}
		}
		
		return ret;
	}
	
	private TraversalDescription rewrite(SemanticQuery query) {
		
		/*
		 * create the "total" traverser and pass it downstream to restrict according
		 * to the query. We only look for object we stored explicitly, i.e. those
		 * that connect directly to the root node.
		 */
		return rewriteInternal(query, 
				Traversal.description().
					depthFirst().
					evaluator(Evaluators.excludeStartPosition())).
					relationships(new RelationshipType() {
						@Override
						public String name() {
							return HASNODE_PROPERTY;
						}
					}, Direction.OUTGOING);
	}

	
	/*
	 * TODO this is entirely not clarified. At the moment it only gets first-class objects (directly inserted) and does not
	 * use the index at all. Probably this is what should happen:
	 * 
	 * 1. walk the query depth-first and recursively rewrite each subquery (not instanceof IOperator)'
	 * 	  when done, execute it and collect the nodes in a HashSet with which to initialize an evaluator for the next level
	 *	  If level = 0, the nodes are the result 
	 *
	 * 2. for IOperators, we should use the indexes - which being Lucene-based, can use multiple properties so we should
	 *    rewrite the parts we can rewrite into queries and add evaluators for the rest
	 *    
	 *    The current implementation always starts at the top node, the one above will not - what's best?
	 * 
	 */
	private TraversalDescription rewriteInternal(SemanticQuery query,
			TraversalDescription td) {
		
		if (query == null)
			return td;
		
		/*
		 * chain any further evaluators.
		 */
		if (query instanceof IOperator) {
			
			/*
			 * must have registered its query adapter.
			 */
			
		} else {
			
			/*
			 * default behavior for SemanticQuery: handle 
			 * semantic closures and downstream nodes.
			 */
			if (query.getSubject() != null)
				td = td.evaluator(Algorithms.semanticClosure(query.getSubject()));
			else {
				/*
				 * connector
				 */
			}
		}
			
		for (SemanticQuery r : query.getRestrictions()) {
			
		}

		return td;
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
	 * 
	 * TODO ensure value is stored appropriately if numeric
	 */
	private void initializeTypeAdapters() {

		registerTypeAdapter(Thinklab.c(NS.TEXT),
				new TypeAdapter() {
					
					@Override
					protected void setAndIndex(Node node, IProperty property, Object value) {
						node.setProperty(toId(property), value);
						Index<Node> index = _db.index().forNodes(BASE_INDEX);
						index.add(node, toId(property), value);
					}
				});
		
		registerTypeAdapter(Thinklab.c(NS.INTEGER),
				new TypeAdapter() {
					
					@Override
					protected void setAndIndex(Node node, IProperty property, Object value) {
						node.setProperty(toId(property), value);
						Index<Node> index = _db.index().forNodes(BASE_INDEX);
						index.add(node, toId(property), value);
					}
				});
		
		registerTypeAdapter(Thinklab.c(NS.DOUBLE),
				new TypeAdapter() {
					
					@Override
					protected void setAndIndex(Node node, IProperty property, Object value) {
						node.setProperty(toId(property), value);
						Index<Node> index = _db.index().forNodes(BASE_INDEX);
						index.add(node, toId(property), value);
					}
				});
		
		registerTypeAdapter(Thinklab.c(NS.FLOAT),
				new TypeAdapter() {
					
					@Override
					protected void setAndIndex(Node node, IProperty property, Object value) {
						node.setProperty(toId(property), value);
						Index<Node> index = _db.index().forNodes(BASE_INDEX);
						index.add(node, toId(property), value);
					}

				});
		registerTypeAdapter(Thinklab.c(NS.LONG),
				new TypeAdapter() {
					
					@Override
					protected void setAndIndex(Node node, IProperty property, Object value) {
						node.setProperty(toId(property), value);
						Index<Node> index = _db.index().forNodes(BASE_INDEX);
						index.add(node, toId(property), value);
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
