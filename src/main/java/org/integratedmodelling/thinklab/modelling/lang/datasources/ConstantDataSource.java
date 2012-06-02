package org.integratedmodelling.thinklab.modelling.lang.datasources;

import java.util.Map;

import org.integratedmodelling.common.HashableObject;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IDataSource;
import org.integratedmodelling.thinklab.api.modelling.ISerialAccessor;
import org.integratedmodelling.thinklab.api.modelling.IState;

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
		public IState createState(int size, IContext context)
				throws ThinklabException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object get(String key) throws ThinklabException {
			return null;
		}

		@Override
		public void notifyDependency(String key, IAccessor accessor) {
			// should never happen
		}

		@Override
		public Object getValue(int overallContextIndex,
				Map<String, Object> context) {
			return _state;
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
