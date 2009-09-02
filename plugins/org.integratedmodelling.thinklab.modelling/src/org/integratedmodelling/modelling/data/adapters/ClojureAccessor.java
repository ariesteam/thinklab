package org.integratedmodelling.modelling.data.adapters;

import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public class ClojureAccessor implements IStateAccessor {

	Object clojureCode = null;
	int[] prmOrder = null;
	Object[] parameters;
	
	public static abstract class CLJEval {
		
		public abstract Object run(Object[] parms);
	}
	
	CLJEval _evaluator = null;
	
	public void initialize() {
		
		/*
		 * figure out order of parameters
		 */

		/*
		 * build and eval Clojure code, returning a proxy for this calculation;
		 * put the proxy object in a safe place
		 */
	
	}

	@Override
	public Object getValue(Object[] registers) {
		if (_evaluator == null)
			initialize();
		return _evaluator.run(registers);
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean notifyDependencyObservable(IConcept observable, String formalName)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void notifyDependencyRegister(IConcept observable, int register,
			IConcept stateType) throws ThinklabValidationException {
		// TODO Auto-generated method stub
		
	}
	
	
}
