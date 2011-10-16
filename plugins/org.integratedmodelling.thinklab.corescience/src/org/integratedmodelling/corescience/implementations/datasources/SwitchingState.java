package org.integratedmodelling.corescience.implementations.datasources;

import java.util.ArrayList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ContextMapper;
import org.integratedmodelling.corescience.context.DatasourceStateAdapter;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.storage.SwitchLayer;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.utils.Polylist;

public class SwitchingState  extends DefaultAbstractState implements IState {
	
	private IInstance observable;
	private SwitchLayer<ContextMapper> switchLayer;
	private ContextMapper[] states;
	private IConcept valueType;

	public SwitchingState(ContextMapper[] states, IInstance observable,
			SwitchLayer<ContextMapper> switchLayer, IContext context) {
		
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

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return Polylist.list(
				CoreScience.CONTEXTUALIZED_DATASOURCE,
				Polylist.list("@", new DatasourceStateAdapter(this)));
	}


	@Override
	public double getDoubleValue(int index)
			throws ThinklabValueConversionException {
		// not supposed to for now, can be done easily
		throw new ThinklabRuntimeException("unsupported getDoubleValue called on a switching datasource");
	}

	public IState getFirstState() {
		for (ContextMapper c : states)
			return c.getState();
		return null;
	}
	
	@Override
	public boolean isProbabilistic() {
		IState s = getFirstState();
		if (s != null)
			return s.isProbabilistic();
		return false;
	}

	@Override
	public boolean isContinuous() {
		IState s = getFirstState();
		if (s != null)
			return s.isContinuous();
		return false;
	}

	@Override
	public boolean isNumeric() {
		IState s = getFirstState();
		if (s != null)
			return s.isNumeric();
		return false;
	}

	@Override
	public boolean isCategorical() {
		IState s = getFirstState();
		if (s != null)
			return s.isCategorical();
		return false;
	}

	@Override
	public boolean isBoolean() {
		IState s = getFirstState();
		if (s != null)
			return s.isBoolean();
		return false;
	}

}
