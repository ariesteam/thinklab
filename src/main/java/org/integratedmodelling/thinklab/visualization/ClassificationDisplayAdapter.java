package org.integratedmodelling.thinklab.visualization;

import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IClassification;
import org.integratedmodelling.thinklab.api.modelling.IClassifyingObserver;
import org.integratedmodelling.thinklab.api.modelling.IState;

public class ClassificationDisplayAdapter extends DisplayAdapter {

	IClassification _classification;
	
	public ClassificationDisplayAdapter(IState state) {
		
		super(state);
		
		if (!(state.getObserver() instanceof IClassifyingObserver)) 
			throw new ThinklabRuntimeException("internal error: classification display metadata used on a non-classification: " + state.getObserver());
		
		_classification = ((IClassifyingObserver)(state.getObserver())).getClassification();

		double[] bp = _classification.getDistributionBreakpoints();
		if (bp != null) {
			// prepare for interpolation
		}

	}

	@Override
	protected Number getDisplayData(Object object) {
		
		/*
		 * TODO handle the rest 
		 */		
		return object == null ? null : _classification.getRank((IConcept)object);
	}

}
