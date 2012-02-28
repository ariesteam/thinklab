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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.modelling.contextualization.Contextualizer;


public class ObservationList implements List<IObservation> {

	IContext context;
	IModel   model;
	IKBox    kbox;
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
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean add(IObservation arg0) {
		throw new UnsupportedOperationException("unsupported on contextualization results");
	}

	@Override
	public boolean addAll(Collection<? extends IObservation> arg0) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("unsupported on contextualization results");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("unsupported on contextualization results");
	}

	@Override
	public boolean contains(Object arg0) {
		throw new UnsupportedOperationException("unsupported on contextualization results");
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		throw new UnsupportedOperationException("unsupported on contextualization results");
	}

	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException("unsupported on contextualization results");
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException("unsupported on contextualization results");
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException("unsupported on contextualization results");
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(int arg0, IObservation arg1) {
		throw new UnsupportedOperationException("unsupported on contextualization results");
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends IObservation> arg1) {
		throw new UnsupportedOperationException("unsupported on contextualization results");
	}

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
	public int indexOf(Object arg0) {
		throw new UnsupportedOperationException("unsupported on contextualization results");
	}

	@Override
	public int lastIndexOf(Object arg0) {
		throw new UnsupportedOperationException("unsupported on contextualization results");
	}

	@Override
	public ListIterator<IObservation> listIterator() {
		throw new UnsupportedOperationException("unsupported on contextualization results");
	}

	@Override
	public ListIterator<IObservation> listIterator(int arg0) {
		throw new UnsupportedOperationException("unsupported on contextualization results");
	}

	@Override
	public IObservation remove(int arg0) {
		throw new UnsupportedOperationException("unsupported on contextualization results");
	}

	@Override
	public IObservation set(int arg0, IObservation arg1) {
		throw new UnsupportedOperationException("unsupported on contextualization results");
	}

	@Override
	public List<IObservation> subList(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
