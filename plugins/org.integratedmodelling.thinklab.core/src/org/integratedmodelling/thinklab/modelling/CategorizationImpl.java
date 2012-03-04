package org.integratedmodelling.thinklab.modelling;

import java.util.Set;

import org.integratedmodelling.lang.model.Observer;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.ICategorizingObserver;
import org.integratedmodelling.thinklab.api.modelling.IContext;

public class CategorizationImpl extends ObserverImpl implements
		ICategorizingObserver {

	public CategorizationImpl(Observer bean) {
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

	@Override
	public Set<String> getDictionary() {
		// TODO Auto-generated method stub
		return null;
	}

}
