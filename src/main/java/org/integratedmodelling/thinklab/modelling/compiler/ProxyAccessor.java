package org.integratedmodelling.thinklab.modelling.compiler;

import java.util.HashMap;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.annotation.SemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IComputingAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IState;


/*
 * Proxy accessor that gives access to dependencies of a model through the computed
 * context of its previous observation. Used to stage execution with parallel 
 * accessors. 
 * 
 * @author Ferd
 *
 */
class ProxyAccessor implements IAccessor, IComputingAccessor {

	IContext _context;
	HashMap<String, IState> _states = new HashMap<String, IState>();
	int _idx;
	
	ProxyAccessor(IContext context) {
		// TODO Auto-generated constructor stub
		_context = context;
	}

	@Override
	public IConcept getStateType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(int contextIndex) {
		// we have no value of our own
		return null;
	}

	@Override
	public void notifyDependency(ISemanticObject<?> observable, String key) {
		// won't be called
	}

	@Override
	public void notifyExpectedOutput(ISemanticObject<?> observable, String key) {
		// find the state for the observable, record mapping key -> state 
		for (IState s : _context.getStates()) {
			if (((SemanticObject<?>)observable).getSignature().equals(
					((SemanticObject<?>)(s.getObservable())).getSignature())) 
				_states.put(key, s);
		}
	}

	@Override
	public void process(int stateIndex) throws ThinklabException {
		// just record the index for the future calls to getValue
		_idx = stateIndex;
	}

	@Override
	public void setValue(String inputKey, Object value) {
		// won't be called
	}

	@Override
	public Object getValue(String key) {
		IState s = _states.get(key);
		return s == null ? null : s.getValue(_idx);
	}

}
