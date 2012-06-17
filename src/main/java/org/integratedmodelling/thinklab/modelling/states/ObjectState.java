package org.integratedmodelling.thinklab.modelling.states;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.modelling.interfaces.IModifiableState;
import org.integratedmodelling.thinklab.modelling.lang.Observation;

public class ObjectState extends Observation implements IModifiableState {

	Object[] _data;
	
	public ObjectState(ISemanticObject<?> observable, IContext context, IObserver observer) {
		_data = new Object[context.getMultiplicity()];
		setObservable(observable);
		setContext(context);
		setObserver(observer);
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

	@Override
	public Object getValue(int contextIndex) {
		// TODO Auto-generated method stub
		return null;
	}

}
