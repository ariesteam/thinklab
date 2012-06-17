package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.annotation.SemanticObject;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IObserver;

/**
 * An observation connects an observable with a context (all observations in a context are
 * contingent to each other). Observation is a base class that is not expected to have a 
 * non-null observer; derived IState also connects "data" - a state holder - and should have
 * an observer to define its observation semantics.
 * 
 * @author Ferd
 *
 */
@Concept(NS.OBSERVATION)
public class Observation extends ModelObject<Observation> implements IObservation {

	IContext    _context;
	IObserver   _observer;
	ISemanticObject<?> _observable;

	IList       _observableDefinition;
	
	public Observation() {}
	
	public Observation(SemanticObject<?> obs, IContext context) {
		_observable = obs;
		_context = context;
	}

	public void initialize() {
	}

	@Override
	public Observation demote() {
		return this;
	}

	@Override
	public IObserver getObserver() {
		return _observer;
	}

	@Override
	public ISemanticObject<?> getObservable() {
		return _observable;
	}

	@Override
	public IContext getContext() {
		return _context;
	}
	
	/*
	 * ---------------------------------------------------------------------------------------------------
	 * for derived and internal purposes
	 * ---------------------------------------------------------------------------------------------------
	 */

	protected void setObservable(ISemanticObject<?> o) {
		_observable = o;
	}
	
	protected void setContext(IContext c) {
		_context = c;
	}
	
	protected void setObserver(IObserver c) {
		_observer = c;
	}

}
