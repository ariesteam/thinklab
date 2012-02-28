package org.integratedmodelling.thinklab.modelling;

import java.util.Collection;

import org.integratedmodelling.lang.model.Observer;
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
	public Collection<IAccessor> getAccessors(IContext context) {
		// TODO Auto-generated method stub
		return null;
	}

}
