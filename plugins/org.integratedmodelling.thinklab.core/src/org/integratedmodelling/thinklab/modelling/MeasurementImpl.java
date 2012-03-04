package org.integratedmodelling.thinklab.modelling;

import org.integratedmodelling.lang.model.Observer;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IMeasuringObserver;
import org.integratedmodelling.thinklab.api.modelling.units.IUnit;

public class MeasurementImpl extends ObserverImpl implements
		IMeasuringObserver {

	public MeasurementImpl(Observer bean) {
		super(bean);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IAccessor getAccessor(IContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getStateType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IUnit getUnit() {
		// TODO Auto-generated method stub
		return null;
	}


}
