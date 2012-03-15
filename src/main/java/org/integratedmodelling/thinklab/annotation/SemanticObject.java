package org.integratedmodelling.thinklab.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabCircularDependencyException;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.annotation.SemanticGraph.PropertyEdge;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IMetadataHolder;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;
import org.integratedmodelling.thinklab.api.metadata.IMetadata;

/**
 * Base class for a general non-literal semantic object.
 * Proxies the object's metadata if it has any.
 * 
 * TODO make it proxy other things such as IComparable and hash/equals.
 * TODO check what should be done (if anything) for cloning.
 * 
 * @author Ferd
 *
 */
public class SemanticObject implements ISemanticObject, IMetadataHolder {

	IReferenceList _semantics;
	Object _object;
	private HashMap<IProperty, List<ISemanticObject>> _literals;
	private boolean _isLiteral = false;
	private long _id;
	
	SemanticGraph _graph = null;

	public SemanticObject(IReferenceList semantics, Object object) {
		
		if (semantics == null && object == null) {
			throw new ThinklabRuntimeException("invalid null semantic object");
		}

		this._semantics = semantics;
		this._object = object;
		this._id = semantics.getId();

		this._isLiteral = 
				object != null && 
				Thinklab.get().isLiteralConcept(getDirectType());
	}

	@Override
	public IReferenceList getSemantics() {
		if (_semantics == null) {
			try {
				_semantics = Thinklab.get().conceptualize(_object);
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		return _semantics;
	}

	@Override
	public Object getObject() {
		if (_object == null) {
			try {
				_object = Thinklab.get().instantiate(_semantics);
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);				
			}
		}
		return _object;
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
	}

	@Override
	public ISemanticObject get(IProperty property) {

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
		return _isLiteral;
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
	public boolean asBoolean() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int asInteger() {
		return _object instanceof Number ?
				((Number)_object).intValue() :
				0;
	}

	@Override
	public double asDouble() {
		return _object instanceof Number ?
				((Number)_object).doubleValue() :
				Double.NaN;	
	}

	@Override
	public float asFloat() {
		return _object instanceof Number ?
				((Number)_object).floatValue() :
				Float.NaN;	
	}

	@Override
	public String asString() {
		return _object instanceof String ?
				((String)_object) :
				toString();	
	}

	@Override
	public String toString() {
		return "[" + getDirectType() + " " + (_object == null ? "<uninstantiated>" : _object) + "]";
	}

	@Override
	public IMetadata getMetadata() {
		return 
			(_object instanceof IMetadataHolder) ?
				((IMetadataHolder)_object).getMetadata() :
				null;
	}

	@Override
	public List<Pair<IProperty, ISemanticObject>> getRelationships() {
		
		if (_graph == null)
			_graph = new SemanticGraph(_semantics, this);

		List<Pair<IProperty, ISemanticObject>> ret = new ArrayList<Pair<IProperty, ISemanticObject>>();
		if (_literals != null) {
			for (IProperty p : _literals.keySet()) {
				for (ISemanticObject obj : _literals.get(p))
					ret.add(new Pair<IProperty, ISemanticObject>(p,obj));
			}
		}
		for (PropertyEdge p : _graph.outgoingEdgesOf(this)) {
			ret.add(new Pair<IProperty, ISemanticObject>(p.property, p.getTo()));
		}

		return ret;
	}

	@Override
	public List<ISemanticObject> getRelationships(IProperty property) {
		
		if (_graph == null)
			_graph = new SemanticGraph(_semantics, this);

		List<ISemanticObject> ret = new ArrayList<ISemanticObject>();
		if (_literals != null) {
			for (IProperty p : _literals.keySet()) {
				if (p.is(property)) {
					for (ISemanticObject obj : _literals.get(p))
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValid() {
		// TODO link to OWL validation. Doubt it's useful for the current applications.
		return true;
	}

	@Override
	public List<ISemanticObject> getSortedRelationships(IProperty property)
			throws ThinklabCircularDependencyException {
		// TODO Auto-generated method stub
		return null;
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
		return arg0 instanceof SemanticObject && ((SemanticObject)arg0)._id == _id;
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
	
	SemanticObject setSemantics(IReferenceList semantics) {
		_semantics = semantics;
		return this;
	}

	SemanticObject setObject(Object o) {
		_object = o;
		return this;
	}


	void setLiteralRelationship(IProperty p, ISemanticObject tg) {
		if (_literals == null)
			_literals = new HashMap<IProperty, List<ISemanticObject>>();
		if (!_literals.containsKey(p))
			_literals.put(p, new ArrayList<ISemanticObject>());
		_literals.get(p).add(tg);
	}


}
