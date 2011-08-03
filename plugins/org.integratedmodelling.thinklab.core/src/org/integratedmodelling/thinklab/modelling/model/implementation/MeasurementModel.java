package org.integratedmodelling.thinklab.modelling.model.implementation;

import java.util.HashMap;

import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservation;
import org.integratedmodelling.thinklab.api.modelling.observation.IState;
import org.integratedmodelling.thinklab.api.modelling.units.IUnit;

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

	@Override
	public IObservation createObservation(HashMap<IInstance, IState> known) {
		// TODO Auto-generated method stub
		return null;
	}


}
