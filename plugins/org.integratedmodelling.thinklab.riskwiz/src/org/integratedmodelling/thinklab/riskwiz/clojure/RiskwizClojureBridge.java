package org.integratedmodelling.thinklab.riskwiz.clojure;

import java.util.Collection;

import org.integratedmodelling.riskwiz.bn.BeliefNetwork;

/**
 * Interface functions for Clojure
 * 
 * @author Ferdinando
 *
 */
public class RiskwizClojureBridge {

	class BeliefNodeDescriptor {
		
	}
	
	public static BeliefNetwork buildBeliefNetwork(Collection<BeliefNodeDescriptor> nodes) {
		
		BeliefNetwork ret = null;
		
		/*
		 * link nodes based on parent IDs
		 */
		
		/*
		 * build BN from them
		 */
		
		
		return ret;
	}
	
	public static BeliefNodeDescriptor buildBeliefNode(String ID, Collection<String> parentIDs, float[] cpt) {
		return null;
	}
	
}
