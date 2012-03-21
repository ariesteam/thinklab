package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.lang.parsing.IMeasuringObserverDefinition;
import org.integratedmodelling.thinklab.api.lang.parsing.IUnitDefinition;
import org.integratedmodelling.thinklab.api.modelling.IUnit;

@Concept(NS.MEASURING_OBSERVER)
public class Measurement extends Observer<Measurement> implements IMeasuringObserverDefinition {

	IUnit _unit;

	public IUnit getUnit() {
		return _unit;
	}

	@Override
	public void setUnit(IUnitDefinition unit) {

		/*
		 * TODO produce unit
		 */
		
	}

	@Override
	public Measurement demote() {
		return this;
	}
	
}
