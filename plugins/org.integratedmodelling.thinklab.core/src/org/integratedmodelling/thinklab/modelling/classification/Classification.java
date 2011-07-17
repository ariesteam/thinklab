package org.integratedmodelling.thinklab.modelling.classification;

import java.util.ArrayList;
import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.modelling.classification.IClassification;
import org.integratedmodelling.thinklab.api.modelling.classification.IClassifier;

public class Classification implements IClassification {

	ArrayList<IClassifier> _classifiers = new ArrayList<IClassifier>();
	
	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(IConcept type, Type typeHint)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub

	}

	@Override
	public IConcept classify(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRank(IConcept concept) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double[] getNumericRange(IConcept concept) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getDistributionBreakpoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IClassifier> getClassifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IConcept> getConceptOrder() {
		// TODO Auto-generated method stub
		return null;
	}

}
