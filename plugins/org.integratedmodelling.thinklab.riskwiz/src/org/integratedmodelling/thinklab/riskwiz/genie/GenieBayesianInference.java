package org.integratedmodelling.thinklab.riskwiz.genie;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.riskwiz.interfaces.IBayesianInference;
import org.integratedmodelling.thinklab.riskwiz.interfaces.IBayesianNetwork;

import smile.Network;
import smile.learning.DataSet;

public class GenieBayesianInference implements IBayesianInference {

	Network network = null;
	
	public GenieBayesianInference(Network prototype) {
		this.network = prototype;
	}

	@Override
	public void run() {
		network.updateBeliefs();
	}

	@Override
	public void setEvidence(String node, String outcome)
			throws ThinklabException {
		this.network.setEvidence(node, outcome);
	}

	@Override
	public double getMarginal(String node, String outcome) {
		double ret = 0;
		double[] vals = network.getNodeValue(node);
		int i = 0;
		for (String s : network.getOutcomeIds(node)) {
			if (s.equals(outcome)) {
				ret = vals[i++];
				break;
			}
		}
		return ret;
	}

	@Override
	public Map<String, Double> getMarginals(String node) {
		
		HashMap<String, Double> result = 
			new HashMap<String, Double>(network.getOutcomeCount(node));
		double[] vals = network.getNodeValue(node);
		int i = 0;
		for (String s : network.getOutcomeIds(node)) {
			result.put(s, vals[i++]);
		}
		return result;
	}

	@Override
	public void clearEvidence() {
		network.clearAllEvidence();
	}

	@Override
	public double[] getMarginalValues(String node) {
		return network.getNodeValue(node);
	}

}
