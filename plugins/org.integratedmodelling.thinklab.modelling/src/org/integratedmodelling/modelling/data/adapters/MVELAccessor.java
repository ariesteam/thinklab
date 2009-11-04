package org.integratedmodelling.modelling.data.adapters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.corescience.Obs;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Pair;
import org.mvel2.MVEL;

public class MVELAccessor implements IStateAccessor {

	String mvelCode = null;
	int[] prmOrder = null;
	Object[] parameters;
	HashMap<IConcept, String> obsToName = new HashMap<IConcept, String>();
	ArrayList<Pair<String,Integer>> parmList = new ArrayList<Pair<String,Integer>>();
	boolean isMediator;
	String namespace = NameGenerator.newName("clj");
	private Serializable bytecode;
	
	public MVELAccessor(String code, boolean isMediator) {
		mvelCode = code;
		this.bytecode = MVEL.compileExpression(mvelCode);
		this.isMediator = isMediator;
	}

	@Override
	public Object getValue(Object[] registers) {
		
		HashMap<String, Object> parms = new HashMap<String, Object>();
		
		for (int i = 0; i < parmList.size(); i++) {
			parms.put(parmList.get(i).getFirst(),
					registers[parmList.get(i).getSecond()]);
		}
		
		try {
			return MVEL.executeExpression(this.bytecode, parms);
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean notifyDependencyObservable(IObservation o, IConcept observable, String formalName)
			throws ThinklabException {
		if (!Obs.isExtent(o))
			obsToName.put(observable, formalName);
		return true;
	}

	@Override
	public void notifyDependencyRegister(IObservation observation, IConcept observable,
			int register, IConcept stateType) throws ThinklabException {
		if (!Obs.isExtent(observation))
			parmList.add(new Pair<String, Integer>(obsToName.get(observable), register));
	}
	
	
}
