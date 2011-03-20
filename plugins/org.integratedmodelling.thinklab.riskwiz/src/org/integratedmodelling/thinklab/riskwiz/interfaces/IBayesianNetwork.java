package org.integratedmodelling.thinklab.riskwiz.interfaces;

public interface IBayesianNetwork {

	public int getNodeCount();
		
	public String[] getAllNodeIds();
	
	public int getOutcomeCount(String nodeId);
	
	public String getOutcomeId(String nodeId, int outcomeIndex);

	public String[] getParentIds(String nodeId);

	public String[] getChildIds(String nodeId);

	public String[] getOutcomeIds(String field);

	public IBayesianInference getInference();

	public String getName();

}