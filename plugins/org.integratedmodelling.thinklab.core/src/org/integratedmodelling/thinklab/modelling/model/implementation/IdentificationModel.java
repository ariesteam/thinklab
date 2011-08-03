package org.integratedmodelling.thinklab.modelling.model.implementation;

import java.util.HashMap;

import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservation;
import org.integratedmodelling.thinklab.api.modelling.observation.IState;

public class IdentificationModel extends DefaultAbstractModel {

	public IdentificationModel(INamespace ns) {
		super(ns);
	}

	@Override
	public IObservation createObservation(HashMap<IInstance, IState> known) {
		// TODO Auto-generated method stub
		return null;
	}

}
