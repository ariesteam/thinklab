package org.integratedmodelling.thinklab.riskwiz.interfaces;

import java.io.File;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;

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
	
	public IBayesianNetwork train(File observations, String method) throws ThinklabException;

	public void write(File modelFile) throws ThinklabIOException;

}