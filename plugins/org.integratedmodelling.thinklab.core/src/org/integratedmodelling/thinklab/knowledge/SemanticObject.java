package org.integratedmodelling.thinklab.knowledge;

import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.lang.Semantics;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;

/**
 * Base class for a general non-literal semantic object.
 * 
 * @author Ferd
 *
 */
public class SemanticObject implements ISemanticObject {

	private Semantics _semantics;
	private Object    _object;
	
	public SemanticObject(Semantics semantics) {
		this._semantics = semantics;
	}
	
	public SemanticObject(Object object) {
		this._object = object;
	}
	
	public SemanticObject(Semantics semantics, Object object) {
		this._semantics = semantics;
		this._object = object;
	}

	@Override
	public Semantics getSemantics() {
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
		return null;
	}

	@Override
	public IConcept getDirectType() {
		return getSemantics().getConcept();
	}

	@Override
	public boolean is(IConcept concept) {
		return getSemantics().getConcept().is(concept);
	}

	@Override
	public boolean is(ISemanticObject object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ISemanticObject get(IProperty property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ISemanticObject> getAll(IProperty property) {
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
	public void validate() throws ThinklabValidationException {
		// TODO validate semantics if we have a reasoner.
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

	public String toString() {
		return "[" + getDirectType() + " " + _object + "]";
	}
	
}
