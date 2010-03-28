package org.integratedmodelling.modelling.data.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Pair;

import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;

public class ClojureAccessor implements IStateAccessor {

	IFn clojureCode = null;
	int[] prmOrder = null;
	Object[] parameters;
	HashMap<IConcept, String> obsToName = new HashMap<IConcept, String>();
	ArrayList<Pair<String,Integer>> parmList = new ArrayList<Pair<String,Integer>>();
	boolean isMediator;
	String namespace = NameGenerator.newName("clj");
	ArrayList<Keyword> kwList = null;
	
	public ClojureAccessor(IFn code, boolean isMediator) {
		clojureCode = code;
		this.isMediator = isMediator;
	}

	@Override
	public Object getValue(Object[] registers) {
		
		PersistentArrayMap parms = new PersistentArrayMap(new Object[] {});
		
		if (kwList == null) {
			kwList = new ArrayList<Keyword>();
			for (int i = 0; i < parmList.size(); i++)
				kwList.add(Keyword.intern(null, parmList.get(i).getFirst()));
		}
		
		for (int i = 0; i < parmList.size(); i++) {
			parms = (PersistentArrayMap) parms.assoc(
						kwList.get(i),
						registers[parmList.get(i).getSecond()]);
		}
		
		try {
			return clojureCode.invoke(parms);
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	@Override
	public boolean isConstant() {
		return false;
	}


	@Override
	public boolean notifyDependencyObservable(IObservation o,
			IConcept observable, String formalName) throws ThinklabException {
		// TODO Auto-generated method stub
		if (!(o instanceof Topology))
			obsToName.put(observable, formalName);
		return true;
	}

	@Override
	public void notifyDependencyRegister(IObservation observation,
			IConcept observable, int register, IConcept stateType)
			throws ThinklabException {
		// TODO Auto-generated method stub
		if (!(observation instanceof Topology))
			parmList.add(new Pair<String, Integer>(obsToName.get(observable), register));
	}
	
	
}
