package org.integratedmodelling.thinklab.modelling.bayes;

import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IComputingAccessor;
import org.integratedmodelling.thinklab.api.modelling.ISerialAccessor;
import org.integratedmodelling.thinklab.interfaces.bayes.IBayesianNetwork;

public class BayesianAccessor implements ISerialAccessor, IComputingAccessor {

	IBayesianNetwork _network;
	
	public BayesianAccessor(Map<String, Object> parameters) {
		
	}
	
	@Override
	public IConcept getStateType() {
		/*
		 * TODO this is actually a DISCRETE_PROBABILITY_DISTRIBUTION which I don't have yet
		 */
		return Thinklab.NUMBER;
	}

	@Override
	public void notifyDependency(ISemanticObject<?> observable, String key) {

		/*
		 * 
		 */
		
	}

	@Override
	public void notifyExpectedOutput(ISemanticObject<?> observable, String key) {

		/*
		 * find node matching observable and the observer that interprets it.
		 * if observable isa uncertainty, find the matching node by inspecting its
		 * properties.
		 */
		
		/*
		 * record key for evidence matching
		 */
	}

	@Override
	public void process(int stateIndex) throws ThinklabException {

		/*
		 * submit evidence
		 */
		
		/*
		 * run inference
		 */
		
		/*
		 * extract values
		 */
		
		/*
		 * store last context index
		 */
	}

	@Override
	public void setValue(String inputKey, Object value) {
		
		/*
		 * store evidence for later inference
		 */
		
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
