package org.integratedmodelling.thinklab.modelling.lang.datasources;

import org.integratedmodelling.common.HashableObject;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IDataSource;
import org.integratedmodelling.thinklab.api.modelling.IState;

/**
 * A datasource that returns the same object no matter what.
 * 
 * @author Ferd
 *
 */
public class ConstantDataSource implements IDataSource {
	
	private Object _state = null;

	class ConstantAccessor extends HashableObject implements IAccessor {

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
			return _state;
		}

		@Override
		public void notifyDependencyKey(String key, IConcept concept) {
		}
		
	}
	
	public ConstantDataSource(Object state) {
		_state = state;
	}

	@Override
	public IAccessor contextualize(IContext context) throws ThinklabException {
		return new ConstantAccessor();
	}

}
