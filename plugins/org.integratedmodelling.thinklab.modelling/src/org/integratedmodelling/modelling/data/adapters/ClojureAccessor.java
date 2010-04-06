package org.integratedmodelling.modelling.data.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.corescience.implementations.observations.Observation;
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

public abstract class ClojureAccessor implements IStateAccessor {

	IFn clojureCode = null;
	int[] prmOrder = null;
	Object[] parameters;
	HashMap<IConcept, String> obsToName = new HashMap<IConcept, String>();
	ArrayList<Pair<String,Integer>> parmList = new ArrayList<Pair<String,Integer>>();
	boolean isMediator;
	String namespace = NameGenerator.newName("clj");
	ArrayList<Keyword> kwList = null;
	int index = 0;
	Observation obs = null;
	Keyword selfId = null;
	String selfLabel = null;
	int mediatedIndex = 0;
	
	public ClojureAccessor(IFn code, Observation obs, boolean isMediator) {
		clojureCode = code;
		this.isMediator = isMediator;
		this.obs = obs;
		
		if (isMediator) {
			selfLabel = ((Observation)obs).getFormalName();
			if (selfLabel == null)
				selfLabel = obs.getObservableClass().getLocalName();
			selfId = Keyword.intern(null, selfLabel);
		}
	}

	@Override
	public Object getValue(Object[] registers) {
		
		PersistentArrayMap parms = new PersistentArrayMap(new Object[] {});
		
		/*
		 * set whatever we are mediating to its mediated value so we can use the post-mediation
		 * result.
		 */
		if (isMediator) {
			parms = (PersistentArrayMap) parms.assoc(selfId, processMediated(registers[mediatedIndex]));
		}

		if (kwList == null) {
			kwList = new ArrayList<Keyword>();
			for (int i = 0; i < parmList.size(); i++)
				kwList.add(Keyword.intern(null, parmList.get(i).getFirst()));
		}
		
		for (int i = 0; i < parmList.size(); i++) {

			Object val = registers[parmList.get(i).getSecond()];

			// if we have any nodata dependency, we eval to nodata
			if (val == null || (val instanceof Double && ((Double)val).isNaN()))
				return null;
			
			parms = (PersistentArrayMap) parms.assoc(kwList.get(i), val);
		}
		
		try {
			return clojureCode.invoke(parms);
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	/**
	 * This one is called if this accessor is for a mediator; if so, it must process
	 * the passed object implementing the mediation strategy. The mediated object will become
	 * available to the code using the :as id or the concept as usual.
	 * 
	 * @param object
	 * @return
	 */
	protected abstract Object processMediated(Object object);

	@Override
	public boolean isConstant() {
		return false;
	}


	@Override
	public boolean notifyDependencyObservable(IObservation observation,
			IConcept observable, String formalName) throws ThinklabException {
		return true;
	}

	@Override
	public void notifyDependencyRegister(IObservation observation,
			IConcept observable, int register, IConcept stateType)
			throws ThinklabException {
		
		if (!(observation instanceof Topology)) {
			
			if (isMediator && observation.isMediated() && 
					observation.getMediatorObservation().equals(this.obs)) {
				mediatedIndex = register;
			} else {
				
				String label = ((Observation)observation).getFormalName();
				if (label == null)
					label = observation.getObservableClass().getLocalName();

				parmList.add(new Pair<String, Integer>(label, register));
			}
		}
	}
	
}
