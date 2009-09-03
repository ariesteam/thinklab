package org.integratedmodelling.modelling.data.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.clojure.ClojureInterpreter;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Pair;

public class ClojureAccessor implements IStateAccessor {

	String clojureCode = null;
	int[] prmOrder = null;
	Object[] parameters;
	HashMap<IConcept, String> obsToName = new HashMap<IConcept, String>();
	ArrayList<Pair<String,Integer>> parmList = new ArrayList<Pair<String,Integer>>();
	boolean isMediator;
	
	public static abstract class CLJEval {
		
		public abstract Object run(Object[] parms);
	}
	
	CLJEval _evaluator = null;
	
	public ClojureAccessor(String code, boolean isMediator) {
		clojureCode = code;
		this.isMediator = isMediator;
	}

	public void initialize() {
		
		
		/*
		 * build and eval Clojure code, returning a proxy for this calculation;
		 * put the proxy object in a safe place
		 * 
		 * will proxy something like
		 * 
		 * (proxy [org.integratedmodelling.modelling.data.adapters.CLJEval] []
	     *   	(run [parms] 
			         (let [parmN (nth parms N) ... ] (code)
			          )))
		 *      
		 * unless we're mediating, in which case we just bind "self" to parms[0] - TODO
		 * 
		 */
		String code = 
			"(proxy [org.integratedmodelling.modelling.data.adapters.ClojureAccessor.CLJEval] []\n" +
			"   (run [parms] \n" +
			"      (let [";
	
		for (int i = 0; i < parmList.size(); i++) {
			code += 
				"\n\t\t" +
				parmList.get(i).getFirst() +
				" (nth parms " +
				parmList.get(i).getSecond() +
				")";
		}
		
		code += "]\n\t\t" +  clojureCode + ")))";
		
		System.out.println(code);
		
		try {
			_evaluator = (CLJEval) new ClojureInterpreter().evalRaw(code, "tmp");
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
		
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
		obsToName.put(observable, formalName);
		return true;
	}

	@Override
	public void notifyDependencyRegister(IConcept observable, int register,
			IConcept stateType) throws ThinklabValidationException {
		parmList.add(new Pair<String, Integer>(obsToName.get(observable), register));
	}
	
	
}
