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
