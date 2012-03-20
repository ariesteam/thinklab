package org.integratedmodelling.thinklab.modelling;

import org.integratedmodelling.thinklab.api.lang.parsing.IMeasuringObserverDefinition;
import org.integratedmodelling.thinklab.api.lang.parsing.IUnitDefinition;
import org.integratedmodelling.thinklab.api.modelling.IUnit;

public class Measurement extends Observer implements IMeasuringObserverDefinition {

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
	
}
