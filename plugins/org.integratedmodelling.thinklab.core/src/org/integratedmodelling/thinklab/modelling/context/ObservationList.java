package org.integratedmodelling.thinklab.modelling.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.observation.IContext;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservation;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservationList;
import org.integratedmodelling.thinklab.api.modelling.observation.IState;

public class ObservationList implements IObservationList {

	IContext context;
	IModel   model;
	IKBox    kbox;
		
	public ObservationList(ArrayList<Pair<IModel, IQueryResult>> deps) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IContext resolve(int index) throws ThinklabException {
		
		Context ret = new Context(context);
		
		IObservation obs = get(index);
		for (IState s : resolveDependencies())
			ret.addState(s);
				
		/*
		 * contextualize with the resolved states
		 */
		Contextualizer ctx = new Contextualizer(obs, context, kbox);

		/*
		 * and any listeners		
		 */
		
		return ctx.run();
	}

	@Override
	public Iterator<IObservation> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IObservation get(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	// --- non-API --------------------------------------------------
	
	/**
	 * extract all the unresolved dependencies, remembering whether they are optional. Complain
	 * and leave if any non-optional dependencies are not available in kbox.
	 */
	public Collection<IState> resolveDependencies() throws ThinklabException {
		
		Collection<IState> ret = new ArrayList<IState>();
		
		
		return ret;
	}

}
