package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IDataSource;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.parsing.IFunctionDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IObservationDefinition;

/**
 * An observation pairs a datasource with an observer. The datasource may provide
 * additional context for it even if a context hasn't been specified.
 * 
 * @author Ferd
 *
 */
@Concept(NS.OBSERVATION)
public class Observation extends ModelObject<Observation> implements IObservationDefinition {

	IList       _observable;
	IDataSource _datasource;
	IContext    _context;
	IObserver   _observer;
	Object      _inlineState;

	IFunctionDefinition _datasourceDefinition;
	
	public void initialize() {
		
		/*
		 * TODO turn datasource definition or inline value into 
		 * an actual datasource.
		 */
		
		/*
		 * TODO let the datasource fill in the observation's 
		 * context if necessary.
		 */
	}
	
	@Override
	public void setObservable(IList semantics) {
		_observable = semantics;
	}

	@Override
	public void setDatasourceGeneratorFunction(IFunctionDefinition datasource) {
		_datasourceDefinition = datasource;
	}

	@Override
	public IList getObservable() {
		return _observable;
	}

	@Override
	public IDataSource getDataSource() {
		return _datasource;
	}

	@Override
	public IContext getContext() {
		return _context;
	}

	@Override
	public void setObserver(IObserver observer) {
		_observer = observer;
	}

	@Override
	public void setInlineState(Object state) {
		_inlineState = state;
	}

	@Override
	public Observation demote() {
		return this;
	}

	@Override
	public void setDataSource(IDataSource datasource) {
		_datasource = datasource;
	}

}
