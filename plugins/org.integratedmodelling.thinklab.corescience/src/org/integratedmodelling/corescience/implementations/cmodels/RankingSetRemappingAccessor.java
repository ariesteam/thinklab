package org.integratedmodelling.corescience.implementations.cmodels;

import java.util.ArrayList;

import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.literals.MappedIntSet;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public class RankingSetRemappingAccessor implements IStateAccessor {

	IDataSource<?> ds = null;
	ArrayList<MappedIntSet> mappings = null;
	int index = 0;
	Double defVal = null;
	
	public RankingSetRemappingAccessor(ArrayList<MappedIntSet> mappings,
			IDataSource<?> ds, Double defValue) {
		this.ds = ds;
		this.mappings = mappings;
		this.defVal = defValue;
	}

	@Override
	public Object getValue(Object[] registers) {
		
		int mval = 0; 
		Double ret = defVal;
		
		Object o = ds.getValue(index++);
		if (o instanceof Double)
			mval = (int)(double)(Double)o;
		else if (o instanceof Integer)
			mval = (Integer)o;
		else if (o instanceof Float)
			mval = (int)(float)(Float)o;
		else if (o instanceof Long)
			mval = (int)(long)(Long)o;
		else if (defVal == null)
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void notifyDependencyRegister(IConcept observable, int register,
			IConcept stateType) throws ThinklabValidationException {
		// TODO Auto-generated method stub

	}

}
