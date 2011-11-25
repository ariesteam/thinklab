package org.integratedmodelling.thinklab.riskwiz.bn;

import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

import org.integratedmodelling.riskwiz.bn.BeliefNetwork;
import org.integratedmodelling.riskwiz.domain.Domain;
import org.integratedmodelling.riskwiz.domain.LabelDomain;
import org.integratedmodelling.riskwiz.io.genie.GenieReader;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.riskwiz.interfaces.IBayesianInference;
import org.integratedmodelling.thinklab.riskwiz.interfaces.IBayesianNetwork;

public class RiskwizBayesianNetwork implements IBayesianNetwork {

	BeliefNetwork bn = null;
	
	public RiskwizBayesianNetwork(File in) throws ThinklabIOException {

		/*
		 * TODO support other formats based on file extension, or create a new
		 * GenericReader.
		 */
		GenieReader r = new GenieReader();
		try {
			this.bn = r.load(new FileInputStream(in));
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}

	}
	
	@Override
	public IBayesianInference getInference() {
		return new RiskwizBayesianInference(bn);
	}

	@Override
	public int getNodeCount() {
		return bn.getNodeNumber();
	}

	@Override
	public String[] getAllNodeIds() {
		return bn.getNodeNames();
	}

	@Override
	public int getOutcomeCount(String nodeId) {
		return bn.getBeliefNode(nodeId).getCount();
	}

	@Override
	public String getOutcomeId(String nodeId, int outcomeIndex) {
		return getOutcomeIds(nodeId)[outcomeIndex];
	}

	@Override
	public String[] getParentIds(String nodeId) {
		return bn.getParents(nodeId);
	}

	@Override
	public String[] getChildIds(String nodeId) {
		return bn.getChildren(nodeId);
	}

	@Override
	public String[] getOutcomeIds(String nodeId) {

		String[] ret = null;
		Domain domain = bn.getBeliefNode(nodeId).getDomain();
		if (domain instanceof LabelDomain) {
			Vector<String> cc = ((LabelDomain)domain).getStates();
			ret = new String[cc.size()]; int i = 0;
			for (String c : cc)
				ret[i++] = c;
		}
		return ret;
	}

	@Override
	public String getName() {
		return bn.getName();
	}
	
	@Override
	public IBayesianNetwork train(File observations, String method) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(File modelFile) throws ThinklabIOException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean isLeaf(String nodeId) {
		// TODO Auto-generated method stub
		String[] ids = getChildIds(nodeId);
		return ids == null || ids.length == 0;
	}

}
