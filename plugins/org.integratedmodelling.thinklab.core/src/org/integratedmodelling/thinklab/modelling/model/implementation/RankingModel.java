package org.integratedmodelling.thinklab.modelling.model.implementation;

import java.util.HashMap;

import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservation;
import org.integratedmodelling.thinklab.api.modelling.observation.IState;

public class RankingModel extends AbstractStateModel {

	public RankingModel(INamespace ns) {
		super(ns);
		// TODO Auto-generated constructor stub
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
