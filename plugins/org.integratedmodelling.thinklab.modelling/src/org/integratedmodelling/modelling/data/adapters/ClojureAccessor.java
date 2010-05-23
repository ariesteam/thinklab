package org.integratedmodelling.modelling.data.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.corescience.implementations.datasources.DefaultAbstractAccessor;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.time.TimePlugin;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Pair;

import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;

public abstract class ClojureAccessor extends DefaultAbstractAccessor {

	IFn clojureCode = null;
	IFn changeCode = null;
	boolean changeIsDerivative = false;
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
	
	// index of time dimension in overall context; if -1, we have no time
	int timeIndex = -1;
	private boolean storeState = false;
	
	public ClojureAccessor(
			IFn code, Observation obs, boolean isMediator, 
			IObservationContext context, IFn change, IFn derivative) {
		
		clojureCode = code;
		if (change != null)
			changeCode = change;
		if (derivative != null) {
			changeCode = derivative;
			changeIsDerivative = true;
		}
		
		this.isMediator = isMediator;
		this.obs = obs;
		
		if (isMediator) {
			selfLabel = ((Observation)obs).getFormalName();
			if (selfLabel == null)
				selfLabel = obs.getObservableClass().getLocalName().toLowerCase();
			selfId = Keyword.intern(null, selfLabel);
		}
		
		int i = 0;
		for (IConcept c : context.getDimensions()) {
			if (c.is(TimePlugin.get().TimeObservable()))
				timeIndex = i;
			i++;
		}
		
	}

	@Override
	public Object getValue(int idx, Object[] registers) {
		
		PersistentArrayMap parms = new PersistentArrayMap(new Object[] {});

		// current offset in own state
		int stateOffset = cmapper.getIndex(idx);
		
		/*
		 * current offset in time dimension. This only gets redefined if we have and need to 
		 * compute change equations instead of state equations.
		 */
		int[] eidx = cursor.getElementIndexes(idx);
		int ctime = timeIndex < 0 ? -1 : eidx[timeIndex];
		
		/*
		 * signals we should use the change equations instead of the state equations if we have 
		 * them.
		 */
		boolean changing = ctime > 0;
		
		
		/*
		 * set values for self. It should be:
		 * 
		 * - undefined if we're not changing or mediating;
		 * - mediated value if we're mediating and are not changing;
		 * - previous value if we're changing and have change expressions. If that's the case, also have
		 *   other extents set their additional contextualized versions of "self" (e.g. neighborhoods).  
		 */
		Object self = null;
		if (isMediator && !changing) {
			self = processMediated(registers[mediatedIndex]);
		} else if (changing && changeCode != null) {

			/*
			 * retrieve previous value with all other extents being equal
			 */
			int[] iidx = cursor.getElementIndexes(stateOffset);
			iidx[timeIndex] = iidx[timeIndex] - 1;
			int previousOffset = cursor.getElementOffset(iidx);
			self = this.state.getDataAt(previousOffset);
			
			/*
			 * give other extents a chance to define their own values
			 */
			int ii = 0;
			for (IConcept extc : this.overallContext.getDimensions()) {

				IExtent ext = this.overallContext.getExtent(extc);

				/*
				 * add the index of each extent and the corresponding subextent value with the
				 * name of the concept space to which the concept belongs.
				 */
				try {
					IValue vv = ext.getState(eidx[ii]);
					String kk = extc.getConceptSpace();
					parms = (PersistentArrayMap) parms.assoc(Keyword.intern(null, kk), vv);
				} catch (ThinklabException e) {
					throw new ThinklabRuntimeException(e);
				}

				// TODO this may change when we support closures, so we can allow arbitrarily
				// parameterized history access
				if (ii == timeIndex)
					continue;
				
				Collection<Pair<String, Integer>> mvars = ext.getStateLocators(eidx[ii]);
				
				int zeroIdx = iidx[ii];
				if (mvars != null)
					for (Pair<String, Integer> mv : mvars) {

						String kwid = selfId.toString().substring(1) + "/" + mv.getFirst();
						iidx[ii] = zeroIdx + mv.getSecond();
						previousOffset = cursor.getElementOffset(iidx);
						Object ov = this.state.getDataAt(previousOffset);
						parms = (PersistentArrayMap) parms.assoc(Keyword.intern(null, kwid), ov);			
					}
				ii++;
			}

			
		}
		
		if (self != null) {
			parms = (PersistentArrayMap) parms.assoc(selfId, self);			
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
		
		Object ret = null;
		
		try {
			
			ret = 
				changing ? 
					(changeIsDerivative ? /* TODO */ null : changeCode.invoke(parms)) :
					clojureCode.invoke(parms);
					
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
		
		if (this.storeState)
			this.state.addValue(stateOffset, ret);
		
		return ret;
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
					label = observation.getObservableClass().getLocalName().toLowerCase();

				parmList.add(new Pair<String, Integer>(label, register));
			}
		}
	}
	
	protected IState createMissingState(IObservation observation, IObservationContext ctx) throws ThinklabException {

		IState ret = null;
		if (changeCode != null) {
			ret = 
				((IndirectObservation)(ctx.getObservation())).
					createState(
						ctx.getMultiplicity(),
						ctx);
			
			// remind our getValue that the state must be stored internally
			this.storeState  = true;
		}
		return ret;
	}

}
