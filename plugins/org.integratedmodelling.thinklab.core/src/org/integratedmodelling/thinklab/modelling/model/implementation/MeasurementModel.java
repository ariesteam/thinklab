package org.integratedmodelling.thinklab.modelling.model.implementation;

import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.observation.IUnit;

public class MeasurementModel extends AbstractStateModel {

	private IUnit _unit;

	public MeasurementModel(INamespace ns, IUnit unit) {
		super(ns);
		this._unit = unit;
	}

	@Override
	public IConcept getCompatibleObservationType() {
		// TODO Auto-generated method stub
		return null;
	}


}
