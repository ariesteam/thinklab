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
		
		/*
		 * TODO use prototypes
		 */
		return null;
	}

	@Override
	public Object getRawData() {
		return _data;
	}

	@Override
	public double[] getDataAsDoubles() throws ThinklabException {
		
		double[] ret = new double[_data.length];
		for (int i = 0; i < _data.length; i++) {
			ret[i] = toDouble(_data[i]);
		}	
		return ret;
	}

	private double toDouble(Object object) {

		if (object == null)
			return Double.NaN;
		
		if (object instanceof Number)
			return ((Number)object).doubleValue();
		
		// TODO must use observer
		
		return 0;
	}


	@Override
	public double getDoubleValue(int index) throws ThinklabException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getValueCount() {
		return _data.length;
	}

	@Override
	public IState aggregate(IConcept concept) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSpatiallyDistributed() {
		return getContext().getSpace() != null && getContext().getSpace().getMultiplicity() > 1;
	}

	@Override
	public boolean isTemporallyDistributed() {
		return getContext().getTime() != null && getContext().getTime().getMultiplicity() > 1;
	}

	@Override
	public void setValue(int index, Object value) {
		_data[index] = value;
	}

	@Override
	public Object getValue(int contextIndex) {
		return _data[contextIndex];
	}

}
