package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IDataSource;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IObserver;

/**
 * An observation pairs a datasource with an observer. The datasource may provide
 * additional context for it even if a context hasn't been specified.
 * 
 * @author Ferd
 *
 */
@Concept(NS.OBSERVATION)
public class Observation extends ModelObject<Observation> implements IObservation {

	IDataSource _datasource;
	IContext    _context;
	IObserver   _observer;
	ISemanticObject<?> _observable;

	IList       _observableDefinition;
	
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

}
