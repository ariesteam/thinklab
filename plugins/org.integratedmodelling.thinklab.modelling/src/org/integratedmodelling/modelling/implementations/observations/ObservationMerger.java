package org.integratedmodelling.modelling.implementations.observations;

import java.util.ArrayList;

import org.integratedmodelling.corescience.ObservationFactory;
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
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.corescience.storage.SwitchLayer;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.utils.Triple;

import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;

/**
 * An observation capable of building a merged datasource to properly handle contingencies.
 * Currently the only type of observation that is given contingencies; this may change, but
 * for now there's no guarantee of contingencies working with any other kind of observation.
 * 
 * @author Ferdinando
 */
@InstanceImplementation(concept=ModellingPlugin.STATEFUL_MERGER_OBSERVATION)
public class ObservationMerger extends Observation implements IndirectObservation {

	// reflected 
	public SwitchLayer<IState> switchLayer = null;
	public ArrayList<Topology> contextExt = null;
	public IObservation        contextObs = null;
	public ArrayList<IFn> conditionals = null;

	ContextMapper[] contextMappers = null;
	int[] idxMap = null; 
	
	IConcept stateType = null;
	private IObservationContext ourContext;
	
	ArrayList<Triple<Keyword,IState,ContextMapper>> pmap = 
			new ArrayList<Triple<Keyword,IState,ContextMapper>>();
	
	class SwitchingAccessor implements IStateAccessor {

		int index = 0;

		@Override
		public Object getValue(int idx, Object[] registers) {

			Object ret = null;
			
			/*
			 * scan the dependencies in priority order until one of them returns a non-null
			 * value that satisfies its conditionals. A glorified, high-level logical expression
			 * with an OR at the upper level.
			 */
			for (int i = 0; i < idxMap.length; i++) {
				
				Object val = registers[idxMap[i]];
				if (val != null && !(val instanceof Double && Double.isNaN((Double)val))) {
					
					if (conditionals != null && conditionals.get(i) != null) {
						
						/*
						 * create new parameter map
						 */
						PersistentArrayMap parms = new PersistentArrayMap(new Object[]{});
						
						for (int pp = 0; pp < pmap.size(); pp++) {
							parms = (PersistentArrayMap) parms.assoc(
									pmap.get(pp).getFirst(), 
									pmap.get(pp).getSecond().
										getValue(pmap.get(pp).getThird().getIndex(index), null));
						}
						
						/*
						 * call closure
						 */
						try {
							if ((Boolean)conditionals.get(i).invoke(parms)) {
								ret = val;
								break;
							}
						} catch (Exception e) {
							throw new ThinklabRuntimeException(e);
						}
						
					} else {
						ret = val;
						break;
					}
				}
			}
			
			index ++;
			
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
			
		}

		@Override
		public void notifyState(IState dds, IObservationContext overallContext,
				IObservationContext ownContext) throws ThinklabException  {
			// TODO Auto-generated method stub
			
		}
	}

	@Override
	public IState createState(int size, IObservationContext context)
			throws ThinklabException {
		
		IState ret = null;
		
		if (stateType.is(KnowledgeManager.Number())) {
			ret = new MemDoubleContextualizedDatasource(stateType, size, (ObservationContext) context);
		} else {
			ret = new MemObjectContextualizedDatasource(stateType, size, (ObservationContext) context);
		}
		
		/*
		 * inherit the merged metadata of the dependencies
		 */
		for (String key : metadata.keySet())
			ret.getMetadata().put(key, metadata.get(key));
		
		if (contextObs != null) {

			// get all states of context and prepare for mediation at the corresponding
			// state
			for (IState s : ObservationFactory.getStates(contextObs)) {
				
				IModel mod = (IModel) s.getMetadata().get(Metadata.DEFINING_MODEL);
				String label = mod == null ? s.getObservableClass().getLocalName() : mod.getId();
				ContextMapper cmap = new ContextMapper(s, context);
				
				pmap.add(new Triple<Keyword,IState,ContextMapper>(
							Keyword.intern(null, label), 
							s, 
							cmap));
			}
			
		}
		
		return ret;

	}

	@Override
	public IStateAccessor getAccessor(IObservationContext context) {
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
			if (dependencies[i] instanceof IndirectObservation) {
				cs.add(((IndirectObservation)dependencies[i]).getStateType());
				this.metadata.merge(((Observation)dependencies[i]).metadata);
			}
		}
		this.stateType = KnowledgeManager.get().getLeastGeneralCommonConcept(cs);
		
		/*
		 * build index to register mapper to preserve datasource order, which we will fill in when dependency
		 * registers are notified.
		 */
		this.idxMap = new int[dependencies.length];
		this.contextMappers = new ContextMapper[dependencies.length];
		
		/*
		 * if we have a context model, run it 
		 */
	}


}
