package org.integratedmodelling.thinklab.modelling;
/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.Iterator;

import org.integratedmodelling.collections.ReadOnlyList;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.modelling.contextualization.Contextualizer;


public class ObservationList extends ReadOnlyList<IObservation> {

	IContext context;
	IModel   model;
	IKbox    kbox;
	ISession session;
	
	class It implements Iterator<IObservation> {

		int n = 0;

		@Override
		public boolean hasNext() {
			return n < (_contextualizer.getResultCount() - 1);
		}

		@Override
		public IObservation next() {
			return  get(n++);
		}

		@Override
		public void remove() {
			// suck it, it's lazy
		}
	}
	
//	private MultidimensionalCursor ticker;
	private Contextualizer _contextualizer;
		
//	public ObservationList(IModel model, ArrayList<Pair<IModel, List<Object>>> deps, ISession session) {
//
//		this.result = deps;
//		this.session = session;
//		this.model = model;
//
//		this.ticker = new MultidimensionalCursor();
//		int[] dims = new int[deps.size()];
//		int i = 0;
//		for (Pair<IModel,List<Object>> pm : deps) {
//			dims[i++] = pm.getSecond().size();
//		}
//		this.ticker.defineDimensions(dims);
//	}

	public ObservationList(Contextualizer contextualizer) {
		this._contextualizer = contextualizer;
	}
	
	@Override
	public Iterator<IObservation> iterator() {
		return new It();
	}

	@Override
	public int size() {
		return _contextualizer.getResultCount();
	}

	// --- non-API --------------------------------------------------
	
//	/**
//	 * If obs is one of the queried ones, set the state in the obs structure 
//	 * instead of asking the model. If it's not resolved and not in the deps,
//	 * it must be an optional one so skip it. The states passed will need to
//	 * be transformed to match the context when the observation is contextualized.
//	 * 
//	 * @param model
//	 * @return
//	 * @throws ThinklabException 
//	 */
//	private IObservation buildObservation(int index, IModel model) throws ThinklabException {
//
//		HashMap<IInstance,IState> known = new HashMap<IInstance, IState>();
//		
//		for (int i = 0; i < result.size(); i++) {
//			
//			List<Object> qr = result.get(i).getSecond();
//			IInstance obs = result.get(i).getFirst().getObservable();
////			IState state = (IState) qr.getResult(ticker.getElementIndexes(index)[i], session).asObject();			
////			
////			known.put(obs,state);
//		}
//		
//		return null; //((DefaultAbstractModel)model).createObservation(known);
//	}

	@Override
	public IObservation get(int n) {
		try {
			return _contextualizer.contextualize(n);
		} catch (ThinklabException e) {
			// TODO see what to do with these. Listeners should handle all error 
			// conditions during contextualization.
			throw new ThinklabRuntimeException(e);
		}
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

}
