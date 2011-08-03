package org.integratedmodelling.thinklab.modelling.model.implementation;

import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.classification.IClassification;

public class ClassificationModel extends AbstractStateModel {

	public ClassificationModel(INamespace ns, IClassification iClassification) {
		super(ns);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IConcept getCompatibleObservationType() {
		// TODO Auto-generated method stub
		return null;
	}

}
