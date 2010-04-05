package org.integratedmodelling.modelling.implementations.observations;

import java.util.ArrayList;
import java.util.Map;

import org.integratedmodelling.corescience.context.ContextMapper;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.MemDoubleContextualizedDatasource;
import org.integratedmodelling.corescience.implementations.datasources.MemObjectContextualizedDatasource;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.storage.SwitchLayer;
import org.integratedmodelling.modelling.Model;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

import clojure.lang.IFn;

/**
 * An observation capable of building a merged datasource to properly handle contingencies.
 * Currently the only type of observation that is given contingencies; this may change, but
 * for now there's no guarantee of contingencies working with any other kind of observation.
 * 
 * @author Ferdinando
 */
@InstanceImplementation(concept=ModellingPlugin.MERGER_OBSERVATION)
public class ObservationMerger extends Observation implements IndirectObservation {

	// reflected 
	public SwitchLayer<IState> switchLayer = null;
	public Model contextModel = null;
	public ObservationContext contextExt = null;
	public Map<String, IState> contextStateMap = null;

	public ArrayList<IFn> conditionals = null;
	ContextMapper[] contextMappers = null;
	int[] idxMap = null; 
	
	IConcept stateType = null;
	
	class SwitchingAccessor implements IStateAccessor {

		@Override
		public Object getValue(Object[] registers) {

			Object ret = null;

			/*
			 * scan the dependencies in priority order until one of them returns a non-null
			 * value that satisfies its conditionals. A glorified, high-level logical expression
			 * with an OR at the upper level.
			 */
			for (int i = 0; i < idxMap.length; i++) {
				
				Object val = registers[idxMap[i]];
				if (val != null) {
					
					if (conditionals != null && conditionals.get(i) != null) {
						
						/*
						 * create new parameter map
						 */
						
						/*
						 * call closure
						 */
						
						/*
						 * if closure returns true, set value and break
						 */
						
					} else {
						ret = val;
						break;
					}
				}
			}
			
			return ret;
		}

		@Override
		public boolean isConstant() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean notifyDependencyObservable(IObservation o,
				IConcept observable, String formalName)
				throws ThinklabException {
			// we want them all
			return true;
		}

		@Override
		public void notifyDependencyRegister(IObservation observation,
				IConcept observable, int register, IConcept stateType)
				throws ThinklabException {
			
			// remember where in the registers we'll find the state in order of priority
			idxMap[((Observation)observation).contingencyOrder] = register;
			
			// TODO build the context mapper between the obs and the context if we have a context model
			contextMappers[((Observation)observation).contingencyOrder] =
				contextExt == null ?
					null :
					/* new ContextMapper(contextExt, FUCK) */ null;
		}
	}
	
// TODO this stuff goes in the datasource accessor
//	if (switchLayer != null) {
//
//		int modelId = 0;
//
//		for (int i = 0; i < _contingents.size(); i++) {
//
//			if (switchLayer.isCovered())
//				break;
//
//			modelId++;
//
//			// TODO initialize with the global ctx = to and the
//			// contingent model's one = from
//			ContextMapper cmap = null;
//			boolean wasActive = false;
//			
//			IFn where = _conditionals.get(i);
//			for (int st = 0; st < switchLayer.size(); st++) {
//
//				boolean active = cmap.isCovered(st);
//
//				if (active && where != null && contextStateMap != null) {
//					
//					/*
//					 * get the state map for context i and eval the
//					 * closure
//					 */
//					Map<?, ?> state = cmap.getLocalState(
//							contextStateMap, st);
//					try {
//						active = (Boolean) where.invoke(state);
//					} catch (Exception e) {
//						throw new ThinklabValidationException(e);
//					}
//					
//					if (!wasActive && active)
//						wasActive = true;
//					
//				}
//
//				if (active) {
//					switchLayer.set(st, modelId);
//				}
//			}
//			
//			if (wasActive)
//				chosen.add(_contingents.get(i));
//		}
//	}

	@Override
	public IState createState(int size, IObservationContext context)
			throws ThinklabException {
		
		if (stateType.is(KnowledgeManager.Number()))
			return new MemDoubleContextualizedDatasource(stateType, size, (ObservationContext) context);

		/*
		 * TODO if all deps are classifications, we should produce an indexed state instead of storing all those
		 * concepts.
		 */
		return new MemObjectContextualizedDatasource(stateType, size, (ObservationContext) context);
	}

	@Override
	public IStateAccessor getAccessor() {
		return new SwitchingAccessor();
	}

	@Override
	public IConcept getStateType() {
		return stateType;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.implementations.observations.Observation#initialize(org.integratedmodelling.thinklab.interfaces.knowledge.IInstance)
	 */
	@Override
	public void initialize(IInstance instance) throws ThinklabException {
		
		super.initialize(instance);

		/*
		 * determine common state type
		 */
		ArrayList<IConcept> cs = new ArrayList<IConcept>();
		for (int i = 0; i < dependencies.length; i++) {
			if (dependencies[i] instanceof IndirectObservation)
				cs.add(((IndirectObservation)dependencies[i]).getStateType());
		}
		this.stateType = KnowledgeManager.get().getLeastGeneralCommonConcept(cs);
		
		/*
		 * build index to register mapper to preserve datasource order, which we will fill in when dependency
		 * registers are notified.
		 */
		this.idxMap = new int[dependencies.length];
		
		/*
		 * if we have a context model, run it 
		 */
	}


}
