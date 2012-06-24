package org.integratedmodelling.thinklab.modelling.visualization;

import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.IClassification;

public class ClassificationDisplayMetadata extends DisplayMetadata {

	IClassification _classification;
	
	public ClassificationDisplayMetadata(IClassification classification) {
		
		_classification = classification;
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
