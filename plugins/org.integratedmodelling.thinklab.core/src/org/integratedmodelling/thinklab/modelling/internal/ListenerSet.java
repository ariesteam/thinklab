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
package org.integratedmodelling.thinklab.modelling.internal;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.thinklab.api.listeners.IListener;
import org.integratedmodelling.thinklab.modelling.model.implementation.AbstractStateModel;

/*
 * Utility class to dispatch messages to all different types of listeners without
 * writing a ridiculous amount of ugly code. Context (privately) returns one of 
 * these.
 */
public class ListenerSet {

	Collection<IListener> _listeners;
	
	public ListenerSet(ArrayList<IListener> _listeners) {
		// TODO Auto-generated constructor stub
	}

	public void notifyDependencyFound(AbstractStateModel model) {
		// TODO Auto-generated method stub
		
	}

	public void notifyOptionalDependencyNotFound(AbstractStateModel model) {
		// TODO Auto-generated method stub
		
	}

	public void notifyAlreadyObservedState(AbstractStateModel model) {
		// TODO Auto-generated method stub
		
	}

}
