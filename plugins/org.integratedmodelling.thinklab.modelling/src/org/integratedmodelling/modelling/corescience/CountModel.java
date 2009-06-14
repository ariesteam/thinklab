package org.integratedmodelling.modelling.corescience;

import org.integratedmodelling.modelling.DefaultDynamicAbstractModel;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Polylist;

public class CountModel extends DefaultDynamicAbstractModel {

	@Override
	protected void validateMediatedModel(IModel model)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Object validateState(Object state)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel getConfigurableClone() {
		// TODO configure it
		return new CountModel();
	}

	@Override
	public Polylist buildDefinition() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

}
