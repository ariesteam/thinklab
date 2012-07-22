package org.integratedmodelling.thinklab.visualization;

import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IClassification;
import org.integratedmodelling.thinklab.api.modelling.IClassifyingObserver;
import org.integratedmodelling.thinklab.api.modelling.IState;

public class ClassificationDisplayMetadata extends DisplayMetadata {

	IClassification _classification;
	
	public ClassificationDisplayMetadata(IState state) {
		
		super(state);
		
		if (state.getObserver() instanceof IClassifyingObserver)
			throw new ThinklabRuntimeException("internal error: classification display metadata used on a non-classification");
		
		_classification = ((IClassifyingObserver)(state.getObserver())).getClassification();

		double[] bp = _classification.getDistributionBreakpoints();
		if (bp != null) {
			// prepare for interpolation
		}

	}

	@Override
	public Number getDisplayData(Object object) {
		
		/*
		 * TODO handle the rest 
		 */
		
		return _classification.getRank((IConcept)object);
	}

}
