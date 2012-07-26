package org.integratedmodelling.thinklab.modelling.bayes;

import java.io.File;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IComputingAccessor;
import org.integratedmodelling.thinklab.api.modelling.ISerialAccessor;
import org.integratedmodelling.thinklab.interfaces.bayes.IBayesianNetwork;

public class BayesianAccessor implements ISerialAccessor, IComputingAccessor {

	IBayesianNetwork _network;
	String importFile;
	File workspace;
	boolean _initialized = false;
	
	public BayesianAccessor(String importFile, File workspace) {
		this.importFile = importFile;
		this.workspace = workspace;
	}

	public void initialize() throws ThinklabException {
		if (!_initialized) {
			_network = 
				BayesianFactory.get().
					createBayesianNetwork(importFile + File.separator + workspace);
			
			/*
			 * TODO compile all support information
			 */
			
			_initialized = true;
		}
	}
	
	@Override
	public IConcept getStateType() {		
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

		initialize();
		
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
