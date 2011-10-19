package org.integratedmodelling.modelling.training;

import org.integratedmodelling.modelling.interfaces.IModel;

public class TrainingManager {

	public static TrainingManager _this = null;
	
	public static TrainingManager get() {
		if (_this == null)
			_this = new TrainingManager();
		return _this;
	}
	
	public String createTrainedInstance(IModel model) {
		return null;
	}
	
	public void deleteTrainedInstance(String id) {
		
	}
	
	public void listTrainedInstances(IModel model) {
		
	}
	
	public IModel applyTraining(IModel model, String id) {
		return null;
	}
	
	/**
	 * Delete all obsolete trained instances for the given
	 * namespace. 
	 * 
	 * @param namespace
	 */
	public void deleteObsoletedInstances(String namespace) {
		
	}
}
