package org.integratedmodelling.corescience.implementations.observations;

import java.util.ArrayList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.MemDoubleContextualizedDatasource;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.literals.MappedDoubleInterval;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IRelationship;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;

@InstanceImplementation(concept=CoreScience.RANKING_INTERVAL_REMAPPER)
public class RankingIntervalRemapper extends Observation implements IndirectObservation {
	
	private ArrayList<MappedDoubleInterval> mappings = new ArrayList<MappedDoubleInterval>();
	private Double defValue = null;
	private IDataSource<?> ds = null;
	
	public class RankingIntervalRemappingAccessor implements IStateAccessor {

		int index = 0;

		@Override
		public Object getValue(int idx, Object[] registers) {
			
			int mval = 0; 
			Double ret = defValue;
			
			Object o = ds.getValue(index++, registers);
			if (o instanceof Double)
				mval = (int)(double)(Double)o;
			else if (o instanceof Integer)
				mval = (Integer)o;
			else if (o instanceof Float)
				mval = (int)(float)(Float)o;
			else if (o instanceof Long)
				mval = (int)(long)(Long)o;
			else if (defValue == null)
				throw new ThinklabRuntimeException(
						"reclassification: cannot deal with value type: " + 
						o);
			
			for (MappedDoubleInterval m : mappings) {
				if (m.contains(mval)) {
					ret = m.getValue();
					break;
				}
			}
			
			if (ret == null)
				throw new ThinklabRuntimeException(
						"reclassification: no mapping found for value " + o);
			
			return ret;
		}

		@Override
		public boolean isConstant() {
			return false;
		}

		@Override
		public boolean notifyDependencyObservable(IObservation o,
				IConcept observable, String formalName)
				throws ThinklabException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void notifyDependencyRegister(IObservation observation,
				IConcept observable, int register, IConcept stateType)
				throws ThinklabException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyState(IState dds, IObservationContext overallContext,
				IObservationContext ownContext) throws ThinklabException  {

		}


	}
	
	@Override
	public void initialize(IInstance i) throws ThinklabException {
		
		super.initialize(i);
		
		for (IRelationship r : i.getRelationships("measurement:hasMapping")) {
			mappings.add(new MappedDoubleInterval(r.getValue().toString()));
		}
		
		IValue def = i.get("measurement:hasDefaultValue");
		if (def != null)
			defValue = Double.parseDouble(def.toString());
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		
		ArrayList<Object> arr = new ArrayList<Object>();
		
		arr.add(this.getObservationClass());
		arr.add(Polylist.list(CoreScience.HAS_OBSERVABLE, getObservable().toList(null)));
		
		for (MappedDoubleInterval m : mappings) {
			arr.add(Polylist.list("measurement:hasMapping", m.toString()));
			
		if (defValue != null)
			arr.add(Polylist.list("measurement:hasDefaultValue", defValue.toString()));
		}
		
		return Polylist.PolylistFromArrayList(arr);
		
	}

	@Override
	public IState createState(int size, IObservationContext context) throws ThinklabException {
		IState ret = new MemDoubleContextualizedDatasource(getObservableClass(), size,  
				(ObservationContext)context);
		ret.getMetadata().merge(this.metadata);
		return ret;
	}

	@Override
	public IStateAccessor getAccessor(IObservationContext context) {
		return new RankingIntervalRemappingAccessor();
	}

	@Override
	public IConcept getStateType() {
		return KnowledgeManager.Double();
	}

}
