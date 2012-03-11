package org.integratedmodelling.thinklab.modelling;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.lang.model.Observer;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IRankingObserver;

public class RankingImpl extends ObserverImpl implements
		IRankingObserver {

	public RankingImpl(Observer bean) {
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
	public Pair<Number, Number> getRange() {
		// TODO Auto-generated method stub
		return null;
	}

}
