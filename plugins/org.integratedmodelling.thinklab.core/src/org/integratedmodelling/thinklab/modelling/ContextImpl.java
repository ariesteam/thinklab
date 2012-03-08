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
package org.integratedmodelling.thinklab.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.lang.model.Context;
import org.integratedmodelling.lang.model.LanguageElement;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.listeners.IListener;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IContextMapper;
import org.integratedmodelling.thinklab.api.modelling.IExtent;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.metadata.IMetadata;
import org.integratedmodelling.thinklab.metadata.Metadata;
import org.integratedmodelling.thinklab.modelling.internal.ListenerSet;
import org.integratedmodelling.thinklab.modelling.internal.NamespaceQualified;

public class ContextImpl extends NamespaceQualified implements IContext {

	/*
	 * this one will be null unless the context comes from a language 
	 * statement.
	 */
	Context _bean;
	
	private ArrayList<IListener> _listeners = new ArrayList<IListener>();
	private Metadata _metadata = new Metadata();	
	private HashMap<ISemanticObject, IState> _states = new HashMap<ISemanticObject, IState>();
	
	public ContextImpl(IContext context) {
		// TODO copy every state and extent from the passed one, then initialize
	}

	public ContextImpl() {
		// TODO Auto-generated constructor stub
	}

	public ContextImpl(Context bean) {
		this._bean = bean;
		/*
		 * TODO define from bean content, which may be not entirely validated.
		 */
	}
	
	@Override
	public Set<ISemanticObject> getObservables() {
		return _states.keySet();
	}

	@Override
	public IMetadata getMetadata() {
		return _metadata;
	}

	@Override
	public Collection<IExtent> getExtents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMultiplicity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMultiplicity(IConcept concept) throws ThinklabException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IExtent getExtent(IConcept observable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCovered(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IState getState(IConcept observable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean intersects(IContext context) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IExtent getTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExtent getSpace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IState> getStates() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IContext collapse(IConcept dimension) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextMapper mapContext(IObservation observation) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getDimensionSizes() {
		// TODO Auto-generated method stub
		return null;
	}

	public IConcept getDimension(IConcept c) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<IConcept> getDimensions() {
		return null;
	}

	public IConcept getDimension(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addState(IState s) {
		// TODO Auto-generated method stub
		
	}

	public ListenerSet getListenerSet() {
		return new ListenerSet(_listeners);
	}

	@Override
	public void listen(IListener... listeners) {
		for (IListener l : listeners)
			_listeners.add(l);
	}

	public boolean containsState(ISemanticObject observable) {
		return _states.containsKey(observable);
	}

	@Override
	public IContext intersection(IContext other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContext union(IContext other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(IContext o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean overlaps(IContext o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void merge(IObservation observation) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void merge(IContext context) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LanguageElement getLanguageElement() {
		// TODO Auto-generated method stub
		return null;
	}

}
