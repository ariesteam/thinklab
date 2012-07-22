package org.integratedmodelling.thinklab.modelling.states;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IClassification;
import org.integratedmodelling.thinklab.api.modelling.IClassifyingObserver;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.modelling.interfaces.IModifiableState;
import org.integratedmodelling.thinklab.modelling.lang.Observation;
import org.integratedmodelling.thinklab.modelling.random.IndexedCategoricalDistribution;

public class ObjectState extends Observation implements IModifiableState {

	Object[] _data;
	IClassification _classification;
	
	public ObjectState(ISemanticObject<?> observable, IContext context, IObserver observer) {
		_data = new Object[context.getMultiplicity()];
		setObservable(observable);
		setContext(context);
		setObserver(observer);
		if (observer instanceof IClassifyingObserver) {
			_classification = ((IClassifyingObserver)observer).getClassification();
		}
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
			ret[i] = getDoubleValue(i);					
		}	
		return ret;
	}


	@Override
	public double getDoubleValue(int index) throws ThinklabException {

		Object o = _data[index];
		
		if (o != null) {
			
			if (_classification != null && o instanceof IConcept) {
				return _classification.getRank((IConcept)o);
			}

			if (o instanceof Number)
				return ((Number)o).doubleValue();
			
			if (o instanceof IndexedCategoricalDistribution) {
				return ((IndexedCategoricalDistribution)o).getMean();
			}
			
		}

		return Double.NaN;
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
