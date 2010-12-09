package org.integratedmodelling.modelling;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.modelling.interfaces.IContext;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Polylist;

public class Context implements IContext {

	ObservationContext _ctx;
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public IObservationContext getContext() {
		return _ctx;
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IState getSpace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IState getTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IState getState(IConcept concept) {
		// TODO Auto-generated method stub
		return null;
	}

}
