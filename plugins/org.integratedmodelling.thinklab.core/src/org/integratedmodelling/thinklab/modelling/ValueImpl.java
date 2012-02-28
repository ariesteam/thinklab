package org.integratedmodelling.thinklab.modelling;

import java.util.Collection;

import org.integratedmodelling.lang.model.Observer;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IValuingObserver;

public class ValueImpl extends ObserverImpl implements
		IValuingObserver {

	public ValueImpl(Observer bean) {
		super(bean);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Collection<IAccessor> getAccessors(IContext context) {
		// TODO Auto-generated method stub
		return null;
	}


}
