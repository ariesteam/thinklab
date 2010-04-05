package org.integratedmodelling.corescience.implementations.observations;

import java.util.ArrayList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.MemDoubleContextualizedDatasource;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.literals.MappedIntSet;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Polylist;

@InstanceImplementation(concept=CoreScience.RANKING_SET_REMAPPER)
public class RankingSetRemapper extends Ranking implements IndirectObservation {
	
	private ArrayList<MappedIntSet> mappings = new ArrayList<MappedIntSet>();
	private Double defValue = null;
	
	public class RankingSetRemappingAccessor implements IStateAccessor {

		int index = 0;
		
		@Override
		public Object getValue(Object[] registers) {
			
			int mval = 0; 
			Double ret = defValue;
			
			Object o = getDataSource().getValue(index++, registers);
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
			
			for (MappedIntSet m : mappings) {
				if (m.contains(mval)) {
					ret = (double)m.getValue();
					break;
				}
			}
			
			if (ret == null)
				throw new ThinklabRuntimeException(
						"reclassification: no mapping found for value " + o);
			
			// System.out.println(o + "->" + ret + ";");
			
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


	}

	@Override
	public IConcept getStateType() {
		/* FIXME this should be an integer, no time to deal with the consequences right now */
		return KnowledgeManager.Double();
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		super.initialize(i);
		
		for (IRelationship r : i.getRelationships("measurement:hasMapping")) {
			mappings.add(new MappedIntSet(r.getValue().toString()));
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
		
		for (MappedIntSet m : mappings) {
			arr.add(Polylist.list("measurement:hasMapping", m.toString()));
			
		if (defValue != null)
			arr.add(Polylist.list("measurement:hasDefaultValue", defValue.toString()));
		}
		
		return Polylist.PolylistFromArrayList(arr);
		
	}


	@Override
	public IState createState(int size, IObservationContext context) throws ThinklabException {
		return new MemDoubleContextualizedDatasource(
				getObservableClass(), size, (ObservationContext)context);
	}


	@Override
	public IStateAccessor getAccessor() {
		return new RankingSetRemappingAccessor();
	}

}
