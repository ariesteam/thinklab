package org.integratedmodelling.corescience.implementations.datasources;

import java.util.ArrayList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ContextMapper;
import org.integratedmodelling.corescience.context.DatasourceStateAdapter;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.corescience.storage.SwitchLayer;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.utils.Polylist;

public class SwitchingState implements IState {
	
	private ObservationContext context;
	private Metadata metadata = new Metadata();
	private IInstance observable;
	private SwitchLayer<ContextMapper> switchLayer;
	private ContextMapper[] states;
	private IConcept valueType;

	public SwitchingState(ContextMapper[] states, IInstance observable,
			SwitchLayer<ContextMapper> switchLayer, IObservationContext context) {
		
		this.context = (ObservationContext) context;
		this.observable = observable;
		this.switchLayer = switchLayer;
		this.states = states;
		
		/*
		 * determine common value type
		 */
		ArrayList<IConcept> vts = new ArrayList<IConcept>();
		for (ContextMapper s : states) {
			if (s != null)
				vts.add(s.getState().getValueType());
		}
		this.valueType = KnowledgeManager.get().getLeastGeneralCommonConcept(vts);
	}

	@Override
	public void setValue(int idx, Object o) {
		// not supposed to
		throw new ThinklabRuntimeException("illegal addValue called on a switching datasource");
	}

	@Override
	public double[] getDataAsDoubles() throws ThinklabValueConversionException {
		
		int size = context.getMultiplicity();
		double[] ret = new double[size];
		for (int i = 0; i < size; i++) {
			
			double val = Double.NaN;
			if (switchLayer != null) {
				val = switchLayer.get(i, states) == null ? 
						Double.NaN : 
						switchLayer.get(i, states).getDoubleValue(i);
			} else {
				// return the first non-null state
				for (ContextMapper s : states) {
					if (s != null) {
						double v = s.getDoubleValue(i);
						if (!Double.isNaN(v)) {
							val = v;
							break;
						}
					}
				}
			}
			ret[i] = val;
		}
		return ret;
	}

	@Override
	public IConcept getObservableClass() {
		return observable.getDirectType();
	}

	@Override
	public Object getRawData() {
		// not supposed to
		throw new ThinklabRuntimeException("illegal getRawData called on a switching datasource");
	}

	@Override
	public int getValueCount() {
		return context.getMultiplicity();
	}
	
//	@Override
//	public Object getInitialValue() {
//		return null;
//	}
//
//	@Override
	private Object switchValue(int index) {
		
		if (switchLayer != null) {
			return switchLayer.get(index, states) == null ? 
					null : 
					switchLayer.get(index, states).getValue(index);
		} else {
			// return the first non-null state
			for (ContextMapper s : states) {
				if (s != null) {
					Object o = s.getValue(index);
					if (o != null && !(o instanceof Double && Double.isNaN((Double)o)))
						return o;
				}
			}
		}
		return null;
	}
	
	@Override
	public Object getValue(int offset) {
		return (offset >= 0 && offset < getValueCount()) ? switchValue(offset) : null;
	}

	@Override
	public IConcept getValueType() {
		return this.valueType;
	}

//	@Override
//	public void postProcess(IObservationContext context)
//			throws ThinklabException {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void preProcess(IObservationContext context)
//			throws ThinklabException {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public IDataSource<?> transform(IDatasourceTransformation transformation)
//			throws ThinklabException {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return Polylist.list(
				CoreScience.CONTEXTUALIZED_DATASOURCE,
				Polylist.list("@", new DatasourceStateAdapter(this)));
	}

	@Override
	public ObservationContext getObservationContext() {
		return this.context;
	}

	@Override
	public Metadata getMetadata() {
		return this.metadata ;
	}

	@Override
	public double getDoubleValue(int index)
			throws ThinklabValueConversionException {
		// not supposed to for now, can be done easily
		throw new ThinklabRuntimeException("unsupported getDoubleValue called on a switching datasource");
	}

}
