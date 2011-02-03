package org.integratedmodelling.corescience.context;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.utils.Polylist;

/**
 * Use when a IState must be seen as a Datasource and be inserted in an observation. It can
 * also be used as a IState. Eventually we will want to enable all the transformation methods, 
 * which at the moment would be quite a big job, to support storing states as rescalable observations.
 * 
 * @author Ferdinando
 *
 */
public class DatasourceStateAdapter implements IDataSource<Object>, IState, IInstanceImplementation {

	private IState _state;

	public DatasourceStateAdapter(IState state) {
		this._state = state;
	}
	
	public IState getOriginalState() {
		return _state;
	}

	@Override
	public IConcept getValueType() {
		return _state.getValueType();
	}

	@Override
	public Object getInitialValue() {
		return null;
	}

	@Override
	public Object getValue(int index, Object[] parameters) {
		return _state.getValue(index);
	}

	@Override
	public void preProcess(IObservationContext context)
			throws ThinklabException {
	}

	@Override
	public void postProcess(IObservationContext context)
			throws ThinklabException {
	}

	@Override
	public IDataSource<?> transform(IDatasourceTransformation transformation)
			throws ThinklabException {
		return null;
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {
	}

	@Override
	public void validate(IInstance i) throws ThinklabException {
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		return _state.conceptualize();
	}

	@Override
	public void setValue(int index, Object o) {
		_state.setValue(index, o);
	}

	@Override
	public Object getValue(int offset) {
		return _state.getValue(offset);
	}

	@Override
	public Object getRawData() {
		return _state.getRawData();
	}

	@Override
	public double[] getDataAsDoubles() throws ThinklabValueConversionException {
		return _state.getDataAsDoubles();
	}

	@Override
	public double getDoubleValue(int index)
			throws ThinklabValueConversionException {
		return _state.getDoubleValue(index);
	}

	@Override
	public Metadata getMetadata() {
		return _state.getMetadata();
	}

	@Override
	public int getValueCount() {
		return _state.getValueCount();
	}

	@Override
	public IConcept getObservableClass() {
		return _state.getObservableClass();
	}

	@Override
	public IContext getObservationContext() {
		return _state.getObservationContext();
	}
	
	@Override
	public String toString() {
		return _state.toString();
	}

	@Override
	public IState aggregate(IConcept concept) throws ThinklabException {
		return _state.aggregate(concept);
	}

	@Override
	public boolean isSpatiallyDistributed() {
		return _state.isSpatiallyDistributed();
	}

	@Override
	public boolean isTemporallyDistributed() {
		return _state.isTemporallyDistributed();
	}

}
