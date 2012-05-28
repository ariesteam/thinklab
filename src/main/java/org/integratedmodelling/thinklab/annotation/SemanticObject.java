package org.integratedmodelling.thinklab.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabCircularDependencyException;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.annotation.SemanticGraph.PropertyEdge;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;

/**
 * Base class for a general non-literal semantic object.
 * 
 * ALL semantic objects are properly hashed with a thread-unique ID and can be
 * used as keys in hashes. Their default equals() method checks for identity -
 * only the same objects will respond true.
 * 
 * @author Ferd
 * 
 */
public abstract class SemanticObject<T> implements ISemanticObject<T> {

	private static final AtomicLong nextId = new AtomicLong(0);
	private static final ThreadLocal<Long> threadId = new ThreadLocal<Long>() {
		@Override
		protected Long initialValue() {
			return nextId.getAndIncrement();
		}
	};

	// Returns the current thread's unique ID, assigning it if necessary
	private static long nextId() {
		Long id = threadId.get();
		threadId.set(new Long(id.longValue() + 1l));
		return id;
	}

	long _id = nextId();

	IReferenceList _semantics;
	private HashMap<IProperty, List<ISemanticObject<?>>> _literals;

	SemanticGraph _graph = null;
	private boolean _beingConceptualized;
	String _signature;

	protected SemanticObject() {
	}

	protected SemanticObject(IReferenceList semantics) {

		this._semantics = semantics;
		if (_semantics != null)
			_id = semantics.getId();
	}

	@Override
	public IReferenceList getSemantics() {
		if (_semantics == null)
			try {
				/*
				 * by setting _beingConceptualized = true, we prevent this from
				 * causing infinite recursion in conceptualize() while allowing
				 * SemanticObject members to be handled properly.
				 */
				_beingConceptualized = true;
				_semantics = Thinklab.get().conceptualize(this);
				_beingConceptualized = false;
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		return _semantics;
	}

	/*
	 * DO NOT USE unless for internal purposes. Semantic objects are immutable,
	 * if that contract is breached you're on your own.
	 */
	public void setSemantics(IReferenceList semantics) {
		_semantics = semantics;
		_graph = null;
	}

	@Override
	public IConcept getDirectType() {
		return Thinklab.c(getSemantics().first().toString());
	}

	@Override
	public boolean is(Object object) {
		return object instanceof IConcept
				&& getDirectType().is((IConcept) object);

		/*
		 * TODO implement is(ISemanticObject) by generating a SemanticQuery from
		 * the argument and matching it to our semantics.
		 */
	}

	@Override
	public ISemanticObject<?> get(IProperty property) {

		if (_graph == null)
			_graph = new SemanticGraph(_semantics, this);

		if (_literals != null && _literals.containsKey(property)) {
			return _literals.get(property).get(0);
		}

		for (PropertyEdge p : _graph.outgoingEdgesOf(this)) {
			if (p.property.is(property))
				return p.getTo();
		}

		return null;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public boolean isConcept() {
		return false;
	}

	@Override
	public boolean isObject() {
		return !isLiteral();
	}

	@Override
	public List<Pair<IProperty, ISemanticObject<?>>> getRelationships() {

		if (_graph == null)
			_graph = new SemanticGraph(_semantics, this);

		List<Pair<IProperty, ISemanticObject<?>>> ret = new ArrayList<Pair<IProperty, ISemanticObject<?>>>();
		if (_literals != null) {
			for (IProperty p : _literals.keySet()) {
				for (ISemanticObject<?> obj : _literals.get(p))
					ret.add(new Pair<IProperty, ISemanticObject<?>>(p, obj));
			}
		}
		for (PropertyEdge p : _graph.outgoingEdgesOf(this)) {
			ret.add(new Pair<IProperty, ISemanticObject<?>>(p.property, p
					.getTo()));
		}

		return ret;
	}

	@Override
	public List<ISemanticObject<?>> getRelationships(IProperty property) {

		if (_graph == null)
			_graph = new SemanticGraph(_semantics, this);

		List<ISemanticObject<?>> ret = new ArrayList<ISemanticObject<?>>();
		if (_literals != null) {
			for (IProperty p : _literals.keySet()) {
				if (p.is(property)) {
					for (ISemanticObject<?> obj : _literals.get(p))
						ret.add(obj);
				}
			}
		}

		for (PropertyEdge p : _graph.outgoingEdgesOf(this)) {
			if (p.property.is(property))
				ret.add(p.getTo());
		}

		return ret;
	}

	@Override
	public boolean isCyclic() {
		if (_graph == null)
			_graph = new SemanticGraph(_semantics, this);
		return _graph.hasCycles();
	}

	@Override
	public boolean isValid() {
		// TODO link to OWL validation. Doubt it's useful for the current
		// applications.
		return true;
	}

	@Override
	public List<ISemanticObject<?>> getSortedRelationships(IProperty property)
			throws ThinklabCircularDependencyException {
		if (_graph == null)
			_graph = new SemanticGraph(_semantics, this);
		return _graph.getTopologicalSorting();
	}

	@Override
	public int getRelationshipsCount() {

		if (_graph == null)
			_graph = new SemanticGraph(_semantics, this);

		int ret = _graph.outgoingEdgesOf(this).size();
		if (_literals != null) {
			for (IProperty p : _literals.keySet()) {
				ret += _literals.get(p).size();
			}
		}
		return ret;
	}

	@Override
	public int getRelationshipsCount(IProperty property) {

		if (_graph == null)
			_graph = new SemanticGraph(_semantics, this);

		int n = 0;
		if (_literals != null) {
			for (IProperty p : _literals.keySet()) {
				if (p.is(property)) {
					n += _literals.get(p).size();
				}
			}
		}
		for (PropertyEdge p : _graph.outgoingEdgesOf(this)) {
			if (p.property.is(property))
				n++;
		}
		return n;
	}

	@Override
	public boolean equals(Object arg0) {
		return arg0 instanceof SemanticObject
				&& ((SemanticObject<?>) arg0)._id == _id;
	}

	@Override
	public int hashCode() {
		return new Long(_id).hashCode();
	}
	
	/**
	 * A signature is a packed representation of the semantics that will be
	 * identical for objects that are semantically equivalent, regardless of the
	 * ordering of relationships. As such, it can be used to index objects based
	 * on their semantics, use them in hash tables or quickly check their
	 * identity.
	 * 
	 * Signatures take a little time to create (and extract the semantics if not
	 * available) so they are cached. Again, a SemanticObject MUST be treated as
	 * immutable. Literals are indexed with their string value - so they may not
	 * be correct or they may be very large, be aware of that.
	 * 
	 * @return
	 */
	public String getSignature() {

		if (_signature == null) {

			ArrayList<String> s = new ArrayList<String>();
			int size = 0;

			s.add(getDirectType().toString());
			size += s.get(0).length();

			for (Pair<IProperty, ISemanticObject<?>> pp : getRelationships()) {

				String ss = "/" + pp.getFirst().toString();

				if (pp.getSecond() instanceof SemanticLiteral<?>)
					ss += "@"
							+ ((SemanticLiteral<?>) pp.getSecond()).demote()
									.toString();
				else
					ss += "#"
							+ ((SemanticObject<?>) pp.getSecond())
									.getSignature();

				size += ss.length();
				s.add(ss);
			}

			Collections.sort(s);

			StringBuffer sb = new StringBuffer(size);

			for (String ss : s)
				sb.append(ss);

			_signature = sb.toString();

		}

		return _signature;
	}
	/*
	 * --------------------------------------------------------------------------
	 * ---------------- next methods are only for AnnotationManager and they
	 * should remain invisible outside the package.
	 * ------------------------------
	 * ------------------------------------------------------------
	 */
	void setLiteralRelationship(IProperty p, ISemanticObject<?> tg) {
		if (_literals == null)
			_literals = new HashMap<IProperty, List<ISemanticObject<?>>>();
		if (!_literals.containsKey(p))
			_literals.put(p, new ArrayList<ISemanticObject<?>>());
		_literals.get(p).add(tg);
	}

	/*
	 * required ugliness (at least until I figure out something better) to allow
	 * conceptualizing objects that are themselves instances of SemanticObject
	 * but need their semantics instantiated.
	 */
	public boolean beingConceptualized() {
		return _beingConceptualized;
	}



}
