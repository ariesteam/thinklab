package org.integratedmodelling.thinklab.modelling.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.multidimensional.MultidimensionalCursor;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.observation.IContext;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservation;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservationList;
import org.integratedmodelling.thinklab.api.modelling.observation.IState;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.modelling.model.implementation.DefaultAbstractModel;

public class ObservationList implements IObservationList {

	IContext context;
	IModel   model;
	IKBox    kbox;
	ISession session;
	
	class It implements Iterator<IObservation> {

		int n = 0;

		@Override
		public boolean hasNext() {
			return n < (ticker.getMultiplicity() - 1);
		}

		@Override
		public IObservation next() {
			try {
				return get(n++);
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		}

		@Override
		public void remove() {
			// suck it
		}
	}
	
	private ArrayList<Pair<IModel, IQueryResult>> result;
	private MultidimensionalCursor ticker;
		
	public ObservationList(IModel model, ArrayList<Pair<IModel, IQueryResult>> deps, ISession session) {

		this.result = deps;
		this.session = session;
		this.model = model;

		this.ticker = new MultidimensionalCursor();
		int[] dims = new int[deps.size()];
		int i = 0;
		for (Pair<IModel,IQueryResult> pm : deps) {
			dims[i++] = pm.getSecond().getResultCount();
		}
		this.ticker.defineDimensions(dims);
	}

	@Override
	public Iterator<IObservation> iterator() {
		return new It();
	}

	@Override
	public int size() {
		return ticker.getMultiplicity();
	}

	@Override
	public IObservation get(int index) throws ThinklabException {
		return buildObservation(index, model);
	}
	
	
	
	// --- non-API --------------------------------------------------
	
	/**
	 * If obs is one of the queried ones, set the state in the obs structure 
	 * instead of asking the model. If it's not resolved and not in the deps,
	 * it must be an optional one so skip it. The states passed will need to
	 * be transformed to match the context when the observation is contextualized.
	 * 
	 * @param model
	 * @return
	 * @throws ThinklabException 
	 */
	private IObservation buildObservation(int index, IModel model) throws ThinklabException {

		HashMap<IInstance,IState> known = new HashMap<IInstance, IState>();
		
		for (int i = 0; i < result.size(); i++) {
			
			IQueryResult qr = result.get(i).getSecond();
			IInstance obs = result.get(i).getFirst().getObservable();
			IState state = (IState) qr.getResult(ticker.getElementIndexes(index)[i], session).asObject();			
			
			known.put(obs,state);
		}
		
		return ((DefaultAbstractModel)model).createObservation(known);
	}

}
