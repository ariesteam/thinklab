package org.integratedmodelling.corescience.implementations.observations;

import java.util.ArrayList;
import java.util.Properties;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.implementations.cmodels.SimpleEmbeddedConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.corescience.literals.MappedDoubleInterval;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Polylist;

@InstanceImplementation(concept=CoreScience.RANKING_INTERVAL_REMAPPER)
public class RankingIntervalRemapper extends Observation implements IConceptualizable {
	
	private ArrayList<MappedDoubleInterval> mappings = new ArrayList<MappedDoubleInterval>();
	private Double defValue = null;
	private IDataSource<?> ds = null;
	
	public class RankingIntervalRemappingAccessor implements IStateAccessor {

		int index = 0;

		@Override
		public Object getValue(Object[] registers) {
			
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
			
			System.out.println(o + "->" + ret + ";");
			
			return ret;
		}

		@Override
		public boolean hasInitialState() {
			return false;
		}

		@Override
		public boolean isConstant() {
			return false;
		}

		@Override
		public boolean notifyDependencyObservable(IConcept observable)
				throws ThinklabValidationException {
			return false;
		}

		@Override
		public void notifyDependencyRegister(IConcept observable, int register,
				IConcept stateType) throws ThinklabValidationException {
		}

	}
	public class RankingIntervalRemappingModel extends SimpleEmbeddedConceptualModel {
		
		@Override
		public IStateAccessor getStateAccessor(IConcept stateType,
				IObservationContext context) {
			return new RankingIntervalRemappingAccessor();
		}

		@Override
		public IConcept getStateType() {
			return KnowledgeManager.Double();
		}
	}
	
	@Override
	protected IConceptualModel createMissingConceptualModel()
			throws ThinklabException {
		return new RankingIntervalRemappingModel();
	}

	@Override
	public void initialize(IInstance i, Properties properties) throws ThinklabException {
		
		super.initialize(i, properties);
		
		for (IRelationship r : i.getRelationships("measurement:hasMapping")) {
			mappings.add(new MappedDoubleInterval(r.getValue().toString()));
		}
		
		IValue def = i.get("measurement:hasDefaultValue");
		if (def != null)
			defValue = Double.parseDouble(def.toString());
		
		ds = getDataSource();
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

}
