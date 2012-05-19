package org.integratedmodelling.thinklab.kbox.neo4j;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabUnsupportedOperationException;
import org.integratedmodelling.lang.Quantifier;
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
import org.integratedmodelling.thinklab.api.metadata.IMetadata;
import org.integratedmodelling.thinklab.interfaces.knowledge.SemanticQuery;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;
import org.integratedmodelling.thinklab.interfaces.storage.KboxTypeAdapter;
import org.integratedmodelling.thinklab.kbox.KBoxResult;
import org.integratedmodelling.thinklab.query.Query;
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
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.index.lucene.QueryContext;
import org.neo4j.index.lucene.ValueContext;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.Traversal;

public class NeoKBox implements IKbox {

	static final String TYPE_PROPERTY = "_type";	
	static final String HASNODE_PROPERTY = "_hasnode";
	
	/*
	 * index names. One for types, one for properties and one (currently unused)
	 * for relationships.
	 */
	static final String BASE_INDEX = "pindex";
	static final String RELS_INDEX = "rindex";
	static final String TYPE_INDEX = "tindex";
	
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
		String type = instance.getDirectType().toString();
		node.setProperty(TYPE_PROPERTY, type);
		Index<Node> index = _db.index().forNodes(TYPE_INDEX);
		index.add(node, TYPE_PROPERTY, type);
		
		for (final Pair<IProperty, ISemanticObject<?>> s : instance.getRelationships()) {
			
			if (s.getSecond().isLiteral()) {
				storeProperty(node, s.getFirst(), s.getSecond());
			} else {
				
				Node target = storeInstanceInternal(s.getSecond(), refs);
				Relationship rel = node.createRelationshipTo(target, new RelationshipType() {
					@Override
					public String name() {
						return toId(s.getFirst());
					}
				});
				
				_db.index().forRelationships(RELS_INDEX).add(rel, "name", toId(s.getFirst()));
			}
		}
		
		/*
		 * if object has metadata, additionally store those metadata whose key is a known
		 * property and whose value has a corresponding type adapter.
		 */
		if (instance instanceof IMetadataHolder) {
			
			IMetadata metadata = ((IMetadataHolder)instance).getMetadata();
			
			for (String key : metadata.getKeys()) {
				IProperty p = Thinklab.get().getProperty(key);
				if (p == null)
					continue;
				IConcept c = 
						Thinklab.get().getLiteralConceptForJavaClass(metadata.get(key).getClass());
				if (c == null)
					continue;
				
				KboxTypeAdapter adapter = _typeAdapters.get(c);
				
				if (adapter != null) {
					adapter.setAndIndexProperty(node.getId(), this, p, metadata.get(key));
				}
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
		
		if (query != null && !(query instanceof Query)) {
			throw new ThinklabUnsupportedOperationException("query type not supported: " + query);
		}
		
		return new KBoxResult(this, queryObjects((Query) query));
	}

	@Override
	public String getUri() {
		return _url;
	}

	@Override
	public ISemanticObject<?> retrieve(long id) throws ThinklabException {
		return Thinklab.get().entify(retrieveList(_db.getNodeById(id), new HashMap<Node, IReferenceList>(), new ReferenceList()));
	}
	
	@Override
	public void removeAll(IQuery query) throws ThinklabException {
		
		for (long l : queryObjects((Query) query)) {
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
	
	private List<Long> applySorting(Set<Long> results, SemanticQuery query) {

		ArrayList<Long> ret = new ArrayList<Long>();
		if (query instanceof IMetadataHolder && ((IMetadataHolder) query).getMetadataFieldAsString(IKbox.SORT_FIELD) != null) {
			/*
			 * TODO sort if the query's metadata specify it
			 */
		} else {
			for (Long n : results) 
				ret.add(n);
		}
		
		return ret;
	}

	/**
	 * FIXME still incomplete re: handling of quantifiers and optimization - barely implemented
	 * 		 enough to support model testing.
	 * 
	 * @param query
	 * @param ncontext the nodes that we're restricting - we should be a restriction if this
	 * 	      is not null.
	 * @param pcontext the property context, either for an operator on a field or a relationship
	 *        to other objects. Can be null.
	 * @return
	 * @throws ThinklabException
	 */
	private Set<Long> retrieveMatches(Query query, Set<Long> ncontext, IProperty pcontext) throws ThinklabException {

		if (query.isConnector()) {
			
			/*
			 * get each result set and intersect according to connector
			 */
			Set<Long> ret = null;
			
			for (SemanticQuery r : query.getRestrictions()) {
				Set<Long> rest = retrieveMatches((Query) r, ncontext, null);

				if (ret == null) 
					ret = rest;
				else {
					if (query.getQuantifier().equals(Quantifier.ALL)) {
						ret.retainAll(rest);
						if (ret.isEmpty())
							return ret;
					} else if (query.getQuantifier().equals(Quantifier.ANY)) {
						ret.addAll(rest);
					} else {
						/*
						 * TODO
						 */
						throw new ThinklabUnsupportedOperationException(
								"query: implementation of quantifiers other than any and all is incomplete");
					}
				}
				
			}
			
			return ret == null ? new HashSet<Long>() : ret;
			
		} else if (query.isRestriction()) {
			
			/*
			 * nothing to restrict, no need to continue.
			 */
			if (ncontext != null && ncontext.isEmpty())
				return ncontext;
			
			Set<Long> ret = new HashSet<Long>();
			
			/*
			 * get the target node set
			 */
			Set<Long> target = retrieveMatches((Query) query.getRestrictions().iterator().next(), 
					null, query.getProperty());
			/*
			 * if we have a relationship context (meaning it wasn't an
			 * operator query) use relationship index to find all rels that
			 * lead to nodes in the set, and intersect their source nodes with the current 
			 * context.
			 */
			if (pcontext != null) {
				for (long id : ncontext) {
					Node node = _db.getNodeById(id);
					for (Relationship rel : node.getRelationships(Direction.OUTGOING)) {
						if (isProperty(rel, pcontext) && target.contains(rel.getEndNode().getId()))
							ret.add(id);
					}
				}
			} else {
				/*
				 * otherwise we are intersecting with an operator query, i.e. the result 
				 * set is the intersection of the node context with the query results.
				 */
				ret = ncontext;
				ncontext.retainAll(target);
			}
			
			return ret;
			
		} else {
			
			if (query instanceof IOperator) {
				
				/*
				 * get result set of index search after translating operator
				 */
				return getNodesMatching((IOperator)query, pcontext);
				
			} else {
				
				/*
				 * Most important case: 
				 * 
				 * select object based on concept's semantic closure and restrict to 
				 * given properties.
				 * 
				 * TODO FIXME this one really should collect all POD operators in the restrictions
				 * and apply them in one shot to the property index, then proceed intersecting
				 * only the other restrictions. Otherwise we're making potentially huge sets.
				 */
				Set<Long> main = getNodesOfType(query.getSubject().getSemanticClosure());
				
				/*
				 * restrict the result set by applying all restriction (all in AND)
				 */
				for (SemanticQuery r : query.getRestrictions()) {
					main = retrieveMatches((Query) r, main, null);
				}
				
				return main;
			}
			
		}

	}

	private boolean isProperty(Relationship rel, IProperty pcontext) {
		IProperty p = Thinklab.p(rel.getType().name());
		return p.is(pcontext);
	}

	private Set<Long> getNodesMatching(IOperator query, IProperty pcontext) throws ThinklabException {
		
		// find type adapter based on property range. If no range, assume string.
		Collection<IConcept> types = pcontext.getRange();
		KboxTypeAdapter zio = _typeAdapters.get(types.size() > 0 ? types.iterator().next() : Thinklab.TEXT);
		
		return zio.searchIndex(this, pcontext, query);
	}

	private Set<Long> getNodesOfType(Set<IConcept> zio) {
		
		HashSet<Long> ret = new HashSet<Long>();
		BooleanQuery bq = new BooleanQuery();
		for (IConcept c : zio) {
			bq.add(new TermQuery(new Term(TYPE_PROPERTY, c.toString())), Occur.SHOULD);
		}
		bq.setMinimumNumberShouldMatch(1);
		for (Node n : _db.index().forNodes(TYPE_INDEX).query(TYPE_PROPERTY, bq))
			ret.add(n.getId());
		return ret;
	}

	private List<Long> queryObjects(Query query) throws ThinklabException {
		
		List<Long> ret = new ArrayList<Long>();
		
		if (query == null || query.isEmpty()) {

			TraversalDescription td = Traversal.description().
					depthFirst().
					evaluator(Evaluators.excludeStartPosition()).
					relationships(new RelationshipType() {
						@Override
						public String name() {
							return HASNODE_PROPERTY;
						}
					}, Direction.OUTGOING);
				for (Node n : td.traverse(_db.getReferenceNode()).nodes()) {
						ret.add(n.getId());
				}
				
				return ret;
		} 
		
		/*
		 * TODO if metadata contain a start node (e.g. root), add it to the 
		 * initial node context and restructure the query appropriately
		 */
		
		Set<Long> results = retrieveMatches(query, null, null);
		return applySorting(results, query);

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
	
	class NumberTypeAdapter extends TypeAdapter {
		
		@Override
		protected void setAndIndex(Node node, IProperty property, Object value) {
			node.setProperty(toId(property), value);
			Index<Node> index = _db.index().forNodes(BASE_INDEX);
			index.add(node, toId(property), new ValueContext(value).indexNumeric());
		}

		@Override
		public Set<Long> searchIndex(IKbox kbox,
				IProperty property, IOperator operator)
				throws ThinklabException {
			
			Set<Long> ret = new HashSet<Long>();
			Index<Node> index = _db.index().forNodes(BASE_INDEX);
			Pair<IConcept, Object[]> op = operator.getQueryParameters();
			Object qc = null;
			
			if (op.getFirst().equals(NS.OPERATION_EQUALS)) {
				
				/*
				 * mah - can't find anything to match a number
				 */
				qc = QueryContext.numericRange(toId(property), (Number)(op.getSecond()[0]), (Number)(op.getSecond()[0]), true, true);
			} else if (op.getFirst().equals(NS.OPERATION_GREATER_THAN)) {
				qc = QueryContext.numericRange(toId(property), (Number)(op.getSecond()[0]), null, false, true);
			} else if (op.getFirst().equals(NS.OPERATION_LESS_THAN)) {
				qc = QueryContext.numericRange(toId(property), null, (Number)(op.getSecond()[0]), true, false);				
			} else if (op.getFirst().equals(NS.OPERATION_GREATER_OR_EQUAL)) {
				qc = QueryContext.numericRange(toId(property), (Number)(op.getSecond()[0]), null, true, true);				
			} else if (op.getFirst().equals(NS.OPERATION_LESS_OR_EQUAL)) {
				qc = QueryContext.numericRange(toId(property), null, (Number)(op.getSecond()[0]), true, true);				
			} else if (op.getFirst().equals(NS.OPERATION_NOT_EQUALS)) {
			
				/*
				 * TODO this should be an OR of a less than and a greater than
				 */
			}
			for (Node n : index.query(qc))
				ret.add(n.getId());
			
			return ret;
		}
	}

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

					@Override
					public Set<Long> searchIndex(IKbox kbox,
							IProperty property, IOperator operator)
							throws ThinklabException {

						Index<Node> index = _db.index().forNodes(BASE_INDEX);
						Pair<IConcept, Object[]> op = operator.getQueryParameters();
						IndexHits<Node> zio = null;
						if (op.getFirst().equals(NS.OPERATION_EQUALS) ||
							op.getFirst().equals(NS.OPERATION_LIKE)) {
							zio = index.query(toId(property), new QueryContext(op.getSecond()[0]));
						}
						
						Set<Long> ret = new HashSet<Long>();
						if (zio != null) {
							for (Node n : zio)
								ret.add(n.getId());
						}
						return ret;
					}
				});
		
		registerTypeAdapter(Thinklab.c(NS.INTEGER), new NumberTypeAdapter());
		registerTypeAdapter(Thinklab.c(NS.DOUBLE), new NumberTypeAdapter());		
		registerTypeAdapter(Thinklab.c(NS.FLOAT), new NumberTypeAdapter());
		registerTypeAdapter(Thinklab.c(NS.LONG), new NumberTypeAdapter());
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
