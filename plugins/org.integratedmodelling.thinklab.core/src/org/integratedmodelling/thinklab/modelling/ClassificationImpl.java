package org.integratedmodelling.thinklab.modelling;

import org.integratedmodelling.lang.model.Observer;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IClassifyingObserver;
import org.integratedmodelling.thinklab.api.modelling.IContext;

public class ClassificationImpl extends ObserverImpl implements
		IClassifyingObserver {
	
	public ClassificationImpl(Observer bean) {
		super(bean);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IAccessor getAccessor(IContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getStateType() {
		// TODO Auto-generated method stub
		return null;
	}


}
