package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IClassification;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.parsing.IClassifyingObserverDefinition;
import org.integratedmodelling.thinklab.modelling.states.ObjectState;

@Concept(NS.CLASSIFYING_OBSERVER)
public class Classification extends Observer<Classification> implements IClassifyingObserverDefinition {

	IClassification _classification;
	
	@Override
	public IClassification getClassification() {
		return _classification;
	}

	@Override
	public Classification demote() {
		return this;
	}

	@Override
	public IState createState(ISemanticObject<?> observable, IContext context) throws ThinklabException {
		return new ObjectState(observable, context, this);
	}

	@Override
	public IAccessor getNaturalAccessor() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setClassification(IClassification classification) {
		_classification = classification;
	}

}
