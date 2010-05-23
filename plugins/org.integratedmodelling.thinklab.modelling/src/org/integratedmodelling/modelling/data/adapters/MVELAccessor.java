package org.integratedmodelling.modelling.data.adapters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Pair;
import org.mvel2.MVEL;

/**
 * MVEL-enabled accessor is called when expressions are strings, parsed as MVEL. NOTE: this must
 * be finished, at this point it only handles basic parameters but no change, derivative or 
 * contextualized expressions. 
 * 
 * @author Ferdinando
 *
 */
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
	public Object getValue(int idx, Object[] registers) {
		
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

	@Override
	public void notifyState(IState dds, IObservationContext overallContext,
			IObservationContext ownContext)  throws ThinklabException {
		// TODO Auto-generated method stub
		
	}
	
}
