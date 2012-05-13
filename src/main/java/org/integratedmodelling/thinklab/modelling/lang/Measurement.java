package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IMeasuringObserver;
import org.integratedmodelling.thinklab.api.modelling.IMediatingObserver;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.IUnit;
import org.integratedmodelling.thinklab.api.modelling.parsing.IMeasuringObserverDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IUnitDefinition;
import org.integratedmodelling.thinklab.modelling.Unit;

@Concept(NS.MEASURING_OBSERVER)
public class Measurement extends Observer<Measurement> implements IMeasuringObserverDefinition, IMediatingObserver {

	IUnit _unit;

	public IUnit getUnit() {
		return _unit;
	}

	@Override
	public void setUnit(IUnitDefinition unit) {
		_unit = new Unit(unit.getStringExpression());
	}

	@Override
	public Measurement demote() {
		return this;
	}

	@Override
	public IAccessor getMediator(IObserver observer) throws ThinklabException {

		if (! (observer instanceof IMeasuringObserver))
			throw new ThinklabValidationException("a measurement can only mediate to another measurement");
		
		// TODO Auto-generated method stub
		return null;
	}
	
}
