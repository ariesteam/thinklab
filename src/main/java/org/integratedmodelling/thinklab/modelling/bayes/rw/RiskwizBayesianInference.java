/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.modelling.bayes.rw;

import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.riskwiz.bn.BeliefNetwork;
import org.integratedmodelling.riskwiz.inference.ls.JoinTree;
import org.integratedmodelling.riskwiz.inference.ls.JoinTreeCompiler;
import org.integratedmodelling.riskwiz.jtree.JTInference;
import org.integratedmodelling.thinklab.interfaces.bayes.IBayesianInference;

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
