package org.integratedmodelling.modelling.data.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator;
import org.integratedmodelling.corescience.implementations.datasources.DefaultAbstractAccessor;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.literals.DistributionValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.time.TimePlugin;
import org.integratedmodelling.time.extents.RegularTimeGridExtent;
import org.integratedmodelling.time.literals.TimeValue;
import org.integratedmodelling.utils.CamelCase;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Pair;

import clojure.lang.IFn;
import clojure.lang.IPersistentMap;
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
	String selfLabel = null;
	int mediatedIndex = 0;
	Object initialValue = null;
	long startMilli = -1l;
	long stepMilli = 0;
	
	// index of time dimension in overall context; if -1, we have no time
	int timeIndex = -1;
	private boolean storeState = false;
	
	class ChangeDerivative implements FirstOrderDifferentialEquations {

		private IPersistentMap _parm;

		public ChangeDerivative(IPersistentMap  parms) {
			_parm = parms;
		}
		
		@Override
		public void computeDerivatives(double t, double[] y, double[] ydot)
				throws DerivativeException {
			
			Object o = null;
			try {
				// TODO add the actual time point corresponding to the computed index
				_parm = _parm.assoc(Keyword.intern(null, "time#index"), new Double(t));

				if (startMilli >= 0) {
					TimeValue tv = new TimeValue((long)(startMilli + stepMilli * t));
					_parm = _parm.assoc(Keyword.intern(null, "time#now"), tv);
				}
				
				o = changeCode.invoke(_parm);
			} catch (Exception e) {
				throw new DerivativeException(e);
			}
			if (o != null && o instanceof Number)
				ydot[0] = ((Number)o).doubleValue();
			else 
				ydot[0] = 0.0;
			
		}

		@Override
		public int getDimension() {
			return 1;
		}
		
	}
	
	private Object integrate(IPersistentMap parms, double initialState, double start, double end) {
		
		// TODO support other integrators using a metadata keyword to selects
		FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
		FirstOrderDifferentialEquations ode = new ChangeDerivative(parms);
		double[] y = new double[] { initialState}; 
		try {
			dp853.integrate(ode, start, y, end, y);
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
		
		return new Double(y[0]);
	}
	

	
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
		
		selfLabel = ((Observation)obs).getFormalName();
		if (selfLabel == null)
			selfLabel = CamelCase.toLowerCase(obs.getObservableClass().getLocalName(),'-');

		int i = 0;
		for (IConcept c : context.getDimensions()) {
			if (c.is(TimePlugin.get().TimeObservable()))
				timeIndex = i;
			i++;
		}
		
	}

	protected void setInitialValue(Object object) {
		initialValue = object;
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
		int[] eidx = cursor.getElementIndexes(stateOffset);
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
		Object self = 
			(initialValue instanceof DistributionValue) ? 
				new Double(((DistributionValue)initialValue).draw()) : 
				initialValue;
				
		if (isMediator && !changing) {
			self = processMediated(registers[mediatedIndex]);
		}
		
		// mediators change, too
		if (changing && changeCode != null) {

			/*
			 * retrieve previous value with all other extents being equal
			 */
			int[] iidx = cursor.getElementIndexes(stateOffset);
			iidx[timeIndex] = iidx[timeIndex] - 1;
			int previousOffset = cursor.getElementOffset(iidx);
			self = this.state.getValue(previousOffset);
			
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
				Object vv = ext.getValue(eidx[ii] - (ii == timeIndex ? 1 : 0));
				String kk = extc.getConceptSpace();
				parms = (PersistentArrayMap) parms.assoc(Keyword.intern(null, kk), vv);

				// TODO this may change when we support closures, so we can allow arbitrarily
				// parameterized history access
				if (ii == timeIndex) {
					
					if (startMilli < 0 && ext instanceof RegularTimeGridExtent) {
						startMilli = ((RegularTimeGridExtent)ext).getStart().getMillis();
						stepMilli = ((RegularTimeGridExtent)ext).getStep();
					}
					
					continue;
				}
				
				Collection<Pair<String, Integer>> mvars = ext.getStateLocators(eidx[ii]);
				
				int zeroIdx = iidx[ii];
				if (mvars != null)
					for (Pair<String, Integer> mv : mvars) {

						String kwid = selfLabel + "#" + mv.getFirst();
						iidx[ii] = zeroIdx + mv.getSecond();
						previousOffset = cursor.getElementOffset(iidx);
						Object ov = this.state.getValue(previousOffset);
						parms = (PersistentArrayMap) parms.assoc(Keyword.intern(null, kwid), ov);			
					}
				ii++;
			}
		}
		
		if (self != null) {
			parms = (PersistentArrayMap) parms.assoc(Keyword.intern(null, selfLabel), self);			
		}
		
		if (kwList == null) {
			kwList = new ArrayList<Keyword>();
			for (int i = 0; i < parmList.size(); i++)
				kwList.add(Keyword.intern(null, parmList.get(i).getFirst()));
		}
		
		for (int i = 0; i < parmList.size(); i++) {

			Object val = registers[parmList.get(i).getSecond()];

			// if we have any nodata dependency, we eval to nodata
//			if (val == null || (val instanceof Double && ((Double)val).isNaN()))
//				return null;
			
			parms = (PersistentArrayMap) parms.assoc(kwList.get(i), val);
		}
		
		Object ret = self;
		try {
			if (changing && changeCode != null) {
				ret = 
					(changeIsDerivative ? 
						integrate(parms, ((Number)self).doubleValue(), ctime - 1, ctime) : 
						changeCode.invoke(parms));
			} else if (clojureCode != null) { 
				ret = clojureCode.invoke(parms);
			}				
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
		
		if (this.storeState)
			this.state.setValue(stateOffset, ret);
		
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
					label = CamelCase.toLowerCase(observation.getObservableClass().getLocalName(), '-');

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
