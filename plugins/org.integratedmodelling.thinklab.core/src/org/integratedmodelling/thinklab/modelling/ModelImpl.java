package org.integratedmodelling.thinklab.modelling;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.lang.model.Model;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.modelling.internal.MN;

public class ModelImpl extends ObserverImpl implements IModel, IConceptualizable {

	public ModelImpl(Model bean) {
		super(bean);
		// TODO Auto-generated constructor stub
	}

	IInstance _observable;
	
	@Override
	public Collection<IAccessor> getAccessors(IContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IInstance getObservable() {
		return _observable;
	}

	@Override
	public IList conceptualize() throws ThinklabException {

		ArrayList<Object> ret = new ArrayList<Object>();
		
		ret.add(MN.MODEL);
		ret.add(PolyList.list(MN.HAS_OBSERVABLE, _observable.conceptualize()));
		
		return PolyList.fromCollection(ret);
	}

	@Override
	public void define(IList conceptualization) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

}
