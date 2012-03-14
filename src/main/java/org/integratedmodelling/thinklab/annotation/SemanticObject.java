package org.integratedmodelling.thinklab.annotation;

import java.util.List;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabCircularDependencyException;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.lang.IMetadataHolder;
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

	private IList _semantics;
	private Object _object;
	
	public SemanticObject(IList semantics, Object object) {
		
		if (semantics == null && object == null) {
			throw new ThinklabRuntimeException("invalid null semantic object");
		}

		this._semantics = semantics;
		this._object = object;
		
		processSemantics();
	}

	private void processSemantics() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IList getSemantics() {
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
		return (IConcept) getSemantics().first();
	}

	@Override
	public boolean is(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ISemanticObject get(IProperty property) {
		// TODO Auto-generated method stub
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
		return true;
	}

	@Override
	public boolean asBoolean() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int asInteger() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double asDouble() {
		return Double.NaN;
	}

	@Override
	public float asFloat() {
		return Float.NaN;
	}

	@Override
	public String asString() {
		return getSemantics().toString();
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
	public boolean equals(Object obj) {
		return 
			obj instanceof SemanticObject &&
			((SemanticObject)obj).getSemantics().equals(getSemantics());
	}

	@Override
	public int hashCode() {
		return getSemantics().hashCode();
	}

	@Override
	public List<Pair<IProperty, ISemanticObject>> getRelationships() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ISemanticObject> getRelationships(IProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCyclic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<ISemanticObject> getSortedRelationships(IProperty property)
			throws ThinklabCircularDependencyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRelationshipsCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRelationshipsCount(IProperty _subject) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	// next two are only for AnnotationManager and they should remain invisible outside the
	// package.
	
	SemanticObject setSemantics(IList semantics) {
		_semantics = semantics;
		return this;
	}

	SemanticObject setObject(Object o) {
		_object = o;
		return this;
	}

}
