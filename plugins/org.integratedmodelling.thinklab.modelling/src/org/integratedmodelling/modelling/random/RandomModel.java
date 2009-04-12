package org.integratedmodelling.modelling.random;

import org.integratedmodelling.modelling.DefaultStatefulAbstractModel;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

public class RandomModel extends DefaultStatefulAbstractModel {

	final static int DISCRETE = 0;
	final static int CONTINUOUS = 1;
	final static int NOISYMAX = 2;
	
	double[] cptDesc = null;
	int type = DISCRETE;
	
	public RandomModel(int type) {
		this.type = type;
	}
	
	@Override
	protected void validateMediatedModel(IModel model)
			throws ThinklabValidationException {
	}

	@Override
	protected Object validateState(Object state)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IInstance buildObservation(IKBox kbox, ISession session)
			throws ThinklabException {
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
		
		RandomModel ret = new RandomModel(type);
		ret.copy(this);
		ret.cptDesc = cptDesc;
		return ret;
	}

}
