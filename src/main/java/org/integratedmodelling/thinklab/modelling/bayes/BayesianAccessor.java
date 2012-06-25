package org.integratedmodelling.thinklab.modelling.bayes;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IComputingAccessor;
import org.integratedmodelling.thinklab.api.modelling.ISerialAccessor;

public class BayesianAccessor implements ISerialAccessor, IComputingAccessor {

	@Override
	public IConcept getStateType() {
		/*
		 * TODO this is actually a DISCRETE_PROBABILITY_DISTRIBUTION which I don't have yet
		 */
		return Thinklab.NUMBER;
	}

	@Override
	public void notifyDependency(ISemanticObject<?> observable, String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyExpectedOutput(ISemanticObject<?> observable, String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void process(int stateIndex) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(String inputKey, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getValue(String outputKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(int contextIndex) {
		// TODO Auto-generated method stub
		return null;
	}

}
