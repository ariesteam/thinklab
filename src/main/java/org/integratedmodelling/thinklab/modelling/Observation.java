package org.integratedmodelling.thinklab.modelling;

import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.lang.parsing.IDataSourceDefinition;
import org.integratedmodelling.thinklab.api.lang.parsing.IObservationDefinition;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IDataSource;
import org.integratedmodelling.thinklab.api.modelling.IObserver;

/**
 * An observation pairs a datasource with an observer. The datasource may provide
 * additional context for it even if a context hasn't been specified.
 * 
 * @author Ferd
 *
 */
public class Observation extends ModelObject implements IObservationDefinition {

	IList       _observable;
	IDataSource _datasource;
	IContext    _context;
	IObserver   _observer;
	Object      _inlineState;
	
	@Override
	public void setObservable(IList semantics) {
		_observable = semantics;
	}

	@Override
	public void setDataSource(IDataSourceDefinition datasource) {
		_datasource = (IDataSource)datasource;
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

}
