package org.integratedmodelling.thinklab.modelling.lang.datasources;

import org.integratedmodelling.common.HashableObject;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IDataSource;
import org.integratedmodelling.thinklab.api.modelling.ISerialAccessor;

/**
 * A datasource that returns the same object no matter what.
 * 
 * @author Ferd
 *
 */
public class ConstantDataSource implements IDataSource {
	
	private Object _state = null;

	class ConstantAccessor extends HashableObject implements ISerialAccessor {

		@Override
		public IConcept getStateType() {
			return Thinklab.get().getLiteralConceptForJavaClass(_state.getClass());
		}

		@Override
		public Object getValue(int key) {
			return _state;
		}
		
		public String toString() {
			return "[constant: " + _state + "]";
		}

	}
	
	public ConstantDataSource(Object state) {
		_state = state;
	}

	@Override
	public IAccessor getAccessor(IContext context) throws ThinklabException {
		return new ConstantAccessor();
	}

}
