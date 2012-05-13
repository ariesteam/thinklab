package org.integratedmodelling.thinklab.annotation;

import java.util.ArrayList;
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
 * ALL semantic objects are properly hashed with a thread-unique ID and can be used as keys
 * in hashes. Their default equals() method checks for identity - only the same objects will
 * respond true.
 * 
 * @author Ferd
 *
 */
public abstract class SemanticObject<T> implements ISemanticObject<T> {
	
    private static final AtomicLong nextId = new AtomicLong(0);
    private static final ThreadLocal<Long> threadId =
        new ThreadLocal<Long>() {
            @Override protected Long initialValue() {
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
				_semantics = Thinklab.get().conceptualize(this);
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		return _semantics;
	}

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
		return 
			object instanceof IConcept &&
			getDirectType().is((IConcept)object);
		
		/*
		 * TODO implement is(ISemanticObject) by generating a 
		 * SemanticQuery from the argument and matching it to
		 * our semantics.
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
					ret.add(new Pair<IProperty, ISemanticObject<?>>(p,obj));
			}
		}
		for (PropertyEdge p : _graph.outgoingEdgesOf(this)) {
			ret.add(new Pair<IProperty, ISemanticObject<?>>(p.property, p.getTo()));
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
		// TODO link to OWL validation. Doubt it's useful for the current applications.
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

		int ret =  _graph.outgoingEdgesOf(this).size();
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
		return arg0 instanceof SemanticObject && ((SemanticObject<?>)arg0)._id == _id;
	}
	
	@Override
	public int hashCode() {
		return new Long(_id).hashCode();
	}
	
	/*
	 * ------------------------------------------------------------------------------------------
	 * next methods are only for AnnotationManager and they should remain invisible outside the
	 * package.
	 * ------------------------------------------------------------------------------------------
	 */
	void setLiteralRelationship(IProperty p, ISemanticObject<?> tg) {
		if (_literals == null)
			_literals = new HashMap<IProperty, List<ISemanticObject<?>>>();
		if (!_literals.containsKey(p))
			_literals.put(p, new ArrayList<ISemanticObject<?>>());
		_literals.get(p).add(tg);
	}


}
