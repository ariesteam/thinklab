package org.integratedmodelling.thinklab.riskwiz.genie;

import java.io.File;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.riskwiz.interfaces.IBayesianInference;
import org.integratedmodelling.thinklab.riskwiz.interfaces.IBayesianNetwork;

import smile.Network;
import smile.SMILEException;
import smile.learning.DataMatch;
import smile.learning.DataSet;
import smile.learning.EM;

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
	

	public GenieBayesianNetwork(Network network, IConcept observable,
			String input) {
		this.prototype = network;
		this.observable = observable;
		this.input = input;
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

	@Override
	public IBayesianNetwork train(File observations, String method) throws ThinklabException {
		
		Network network = new Network();
		try {
			network.readFile(this.input);			
		} catch (Exception e) {
			throw new ThinklabIOException(
					"GENIE import: reading " + input + ": " + e.getMessage());
		}
		
		DataSet dset = new DataSet();
		dset.readFile(observations.toString(), "*");
		dset.matchNetwork(network);

		DataMatch[] dm = new DataMatch[dset.getVariableCount()];
		
		for (int i = 0; i < dset.getVariableCount(); i++) {
			String nodeId = dset.getVariableId(i);
			int node = network.getNode(nodeId);
			
			// TODO check this bizarre slice parameter
			dm[i] = new DataMatch(i, node, 0);
		}
		
		try {
			if (method.equals("EM")) {
				EM em = new EM();
				em.learn(dset, network, dm);
			} // TODO remaining methods
		} catch (SMILEException e) {
			throw new ThinklabValidationException(e);
		}
		
		return new GenieBayesianNetwork(network, observable, input);
	}

	@Override
	public void write(File modelFile) throws ThinklabIOException {
		try {
			this.prototype.writeFile(modelFile.toString());
		} catch (SMILEException e) {
			throw new ThinklabIOException(e);
		}
	}


	@Override
	public boolean isLeaf(String nodeId) {
		// TODO Auto-generated method stub
		String[] ids = getParentIds(nodeId);
		return ids == null || ids.length == 0;
	}

}
