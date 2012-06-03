package org.integratedmodelling.thinklab.modelling.states;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.modelling.interfaces.IModifiableState;
import org.integratedmodelling.thinklab.modelling.lang.Observation;

public class NumberState extends Observation implements IModifiableState {

	double _data[];
	
	public NumberState(ISemanticObject<?> observable, IContext context) {
		_data = new double[context.getMultiplicity()];
		setObservable(observable);
		setContext(context);
	}
	
	@Override
	public Object getValue(int index) {
		return _data[index];
	}

	@Override
	public IConcept getStateType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getRawData() {
		return _data;
	}

	@Override
	public double[] getDataAsDoubles() throws ThinklabException {
		return _data;
	}

	@Override
	public double getDoubleValue(int index) throws ThinklabException {
		return _data[index];
	}

	@Override
	public int getValueCount() {
		return _data.length;
	}

	@Override
	public IState aggregate(IConcept concept) throws ThinklabException {
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

		if (value instanceof Number)
			_data[index] = ((Number)value).doubleValue();
		else 
			_data[index] = Double.NaN;
	}

}
