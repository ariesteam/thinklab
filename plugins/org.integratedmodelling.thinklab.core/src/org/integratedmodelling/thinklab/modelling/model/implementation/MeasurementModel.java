package org.integratedmodelling.thinklab.modelling.model.implementation;

import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.observation.IUnit;
import org.integratedmodelling.thinklab.modelling.model.DefaultAbstractModel;

public class MeasurementModel extends DefaultAbstractModel {

	private IUnit _unit;

	public MeasurementModel(INamespace ns, IUnit unit) {
		super(ns);
		this._unit = unit;
	}


}
