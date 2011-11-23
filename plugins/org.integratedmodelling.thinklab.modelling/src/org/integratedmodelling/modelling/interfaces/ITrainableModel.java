package org.integratedmodelling.modelling.interfaces;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

public interface ITrainableModel {

	/**
	 * Train the model to match any specified output observation (in the :observed
	 * clause, if any). Returns a new trained model
	 * that has learned to reproduce the models observed on the passed kbox.
	 * 
	 * @param kbox
	 * @param session
	 * @param params
	 * @return
	 * @throws ThinklabException
	 */
	IModel train(IKBox kbox, ISession session, Object ... params) throws ThinklabException;
	
	/**
	 * Switch to the trained instance identified by passed ID. Must work with the TrainingManager to
	 * convert the ID into usable information.
	 * 
	 * @param trainedInstanceID
	 */
	void applyTraining(String trainedInstanceID);
}
