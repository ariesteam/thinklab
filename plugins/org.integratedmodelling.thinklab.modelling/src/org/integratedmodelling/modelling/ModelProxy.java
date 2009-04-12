package org.integratedmodelling.modelling;

import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

/**
 * A model proxy wraps a Model so it can receive configuration through clauses.
 * 
 * @author Ferdinando Villa
 *
 */
public class ModelProxy extends DefaultAbstractModel {

	IModel model = null;
	
	public ModelProxy(IModel model) {
		this.model = model;
	}

	@Override
	public IInstance buildObservation(IKBox kbox, ISession session)
			throws ThinklabException {
		return model.buildObservation(kbox, session);
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		return model.getCompatibleObservationType(session);
	}

	@Override
	public IModel getConfigurableClone() {
		return new ModelProxy(model);
	}

	@Override
	public IConcept getObservable() {
		return model.getObservable();
	}

	@Override
	public boolean isResolved() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	protected void validateMediatedModel(IModel model)
			throws ThinklabValidationException {
		// TODO
	}

}
