package org.integratedmodelling.thinklab.annotation;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;

public class DefaultSemanticObject extends SemanticObject<Object> {

	Object _object;
	
	protected DefaultSemanticObject(IReferenceList semantics, Object object) {
		super(semantics);
		_object = object;
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
	public Object demote() {
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
	public String toString() {
		return "[" + getDirectType() + " " + (_object == null ? "<uninstantiated>" : _object) + "]";
	}

}
