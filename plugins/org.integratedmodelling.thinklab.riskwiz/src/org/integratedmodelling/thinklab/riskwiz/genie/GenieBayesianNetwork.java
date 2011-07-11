package org.integratedmodelling.thinklab.riskwiz.genie;

import java.io.File;

import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.riskwiz.interfaces.IBayesianInference;
import org.integratedmodelling.thinklab.riskwiz.interfaces.IBayesianNetwork;

import smile.Network;

public class GenieBayesianNetwork implements IBayesianNetwork {

	/*
	 * we keep one network as "prototype" and we use it for the first
	 * inference object.
	 */
	Network prototype;
	IConcept observable;
	boolean used = false;
	String input = null;
	
	public GenieBayesianNetwork(File in) throws ThinklabIOException {

		prototype =  new Network();
		
		try {
			this.prototype.readFile(this.input = in.toString());			
		} catch (Exception e) {
			throw new ThinklabIOException(
					"GENIE import: reading " + in + ": " + e.getMessage());
		}
	}
	
	@Override
	public IBayesianInference getInference() {
		// TODO Auto-generated method stub
		if (!used) {
			return new GenieBayesianInference(prototype);
		}
		
		Network net = new Network();
		
		/*
		 * one has been succesfully read already, don't capture exceptions.
		 */
		net.readFile(this.input);
		return new GenieBayesianInference(net);
		
	}

	@Override
	public int getNodeCount() {
		return prototype.getNodeCount();
	}

	@Override
	public String[] getAllNodeIds() {
		return prototype.getAllNodeIds();
	}

	@Override
	public int getOutcomeCount(String nodeId) {
		return prototype.getOutcomeCount(nodeId);
	}

	@Override
	public String getOutcomeId(String nodeId, int outcomeIndex) {
		return prototype.getOutcomeId(nodeId, outcomeIndex);
	}

	@Override
	public String[] getParentIds(String nodeId) {
		return prototype.getParentIds(nodeId);
	}

	@Override
	public String[] getChildIds(String nodeId) {
		return prototype.getChildIds(nodeId);
	}

	@Override
	public String[] getOutcomeIds(String nodeId) {
		return prototype.getOutcomeIds(nodeId);
	}

	@Override
	public String getName() {
		return prototype.getName();
	}

}
