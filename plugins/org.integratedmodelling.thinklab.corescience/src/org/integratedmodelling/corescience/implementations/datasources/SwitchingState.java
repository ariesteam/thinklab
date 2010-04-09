package org.integratedmodelling.corescience.implementations.datasources;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.corescience.storage.SwitchLayer;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Polylist;

public class SwitchingState implements IState {
	
	private ObservationContext context;

	public SwitchingState(SwitchLayer<IState> switchLayer, ObservationContext context) {
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addValue(int idx, Object o) {
		// TODO Auto-generated method stub

	}

	@Override
	public double[] getDataAsDoubles() throws ThinklabValueConversionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getMetadata(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getObservableClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getRawData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTotalSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMetadata(String id, Object o) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getInitialValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(int index, Object[] parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getValueType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void postProcess(IObservationContext context)
			throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public void preProcess(IObservationContext context)
			throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public IDataSource<?> transform(IDatasourceTransformation transformation)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObservationContext getObservationContext() {
		return this.context;
	}

}
