package org.integratedmodelling.thinklab.modelling.states;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.modelling.interfaces.IModifiableState;
import org.integratedmodelling.thinklab.modelling.lang.Observation;

public class IndexedObjectState extends Observation implements IModifiableState {

	@Override
	public Object getValue(int overallContextIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getStateType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getRawData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getDataAsDoubles() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getDoubleValue(int index) throws ThinklabException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getValueCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IState aggregate(IConcept concept) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSpatiallyDistributed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTemporallyDistributed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setValue(int index, Object value) {
		// TODO Auto-generated method stub
		
	}

}