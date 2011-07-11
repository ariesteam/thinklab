package org.integratedmodelling.thinklab.riskwiz.bn;

import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.riskwiz.bn.BeliefNetwork;
import org.integratedmodelling.riskwiz.inference.ls.JoinTree;
import org.integratedmodelling.riskwiz.inference.ls.JoinTreeCompiler;
import org.integratedmodelling.riskwiz.jtree.JTInference;
import org.integratedmodelling.thinklab.riskwiz.interfaces.IBayesianInference;

public class RiskwizBayesianInference implements IBayesianInference {

	JTInference<JoinTree> inference = null;
	
	RiskwizBayesianInference(BeliefNetwork bn) {
		this.inference = new JTInference<JoinTree>();
		try {
			this.inference.initialize(bn, new JoinTreeCompiler());
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	@Override
	public void run() {
		this.inference.run();
	}

	@Override
	public void setEvidence(String node, String outcome)
			throws ThinklabException {
		this.inference.setObservation(node, outcome);
	}

	@Override
	public double getMarginal(String node, String outcome) {
		return inference.getMarginal(node).getDomainValuePairs().get(outcome);
	}

	@Override
	public Map<String, Double> getMarginals(String node) {
		return inference.getMarginal(node).getDomainValuePairs();
	}

	@Override
	public void clearEvidence() {
		inference.globalRetraction();
	}

	@Override
	public double[] getMarginalValues(String node) {
		return inference.getMarginal(node).getValues();
	}
}
