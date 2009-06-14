package org.integratedmodelling.modelling;

import java.util.Collection;

import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.utils.Polylist;

/**
 * A model proxy wraps a Model so it can receive configuration through clauses.
 * 
 * @author Ferdinando Villa
 *
 */
public class ModelProxy extends DefaultAbstractModel {

	Model model = null;
	
	public ModelProxy(Model model) {
		this.model = model;
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


	@Override
	public Polylist buildDefinition() throws ThinklabException {
		// TODO Auto-generated method stub
		return model.buildDefinition();
	}
	
	/*
	 * We may need to run a proxy, so just proxy it.
	 */
	public IInstance run(ISession session, Collection<Object> params) throws ThinklabException {
		return model.run(session,params);
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		return model.conceptualize();
	}
}
