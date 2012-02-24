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
package org.integratedmodelling.thinklab.modelling.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.multidimensional.MultidimensionalCursor;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.modelling.model.implementation.DefaultAbstractModel;

public class ObservationList implements List<IObservation> {

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
			return  get(n++);
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

	@Override
	public boolean add(IObservation arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends IObservation> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends IObservation> arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IObservation get(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexOf(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<IObservation> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<IObservation> listIterator(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IObservation remove(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IObservation set(int arg0, IObservation arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IObservation> subList(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
