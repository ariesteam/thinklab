package org.integratedmodelling.modelling.training;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.model.DefaultAbstractModel;
import org.integratedmodelling.modelling.model.Model;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.NameGenerator;

public class TrainingManager {

	public static TrainingManager _this = null;
	
	public static TrainingManager get() {
		if (_this == null)
			_this = new TrainingManager();
		return _this;
	}
	
	public Collection<IModel> findTrainableModels(IModel model) {
		
		ArrayList<IModel> ret  = new ArrayList<IModel>();
		findTrainableInternal(model, ret);
		return ret;
	}
	
	private void findTrainableInternal(IModel model, ArrayList<IModel> ret) {

		if (model instanceof Model) {
			for (IModel m : ((Model)model).getObservers()) {
				findTrainableInternal(m, ret);
			}
		} else if (((DefaultAbstractModel)model).isTrainable()) {
			ret.add(model);
		} else {
			for (IModel m : model.getDependencies()) {
				findTrainableInternal(m, ret);
			}
		}
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

	/**
	 * Train all the trainable models found in the definition of the passed one. If no trainable
	 * model exists, return null. Otherwise return a unique ID that can be later applied to the
	 * same model to turn it into the trained instance.
	 * 
	 * @param model
	 * @param context
	 * @param kbox
	 * @param session
	 * @return
	 * @throws ThinklabException 
	 */
	public String doTraining(Model model, IContext context,
			IKBox kbox, ISession session) throws ThinklabException {

		String id = null;
		
		Collection<IModel> trainable = findTrainableModels(model);

		if (trainable.size() > 0) {
			
			id = NameGenerator.newName(model.getId());
			
			/*
			 * setup dirs for serialization
			 */
			File tdir = 
				new File(ModellingPlugin.get().getScratchPath() + File.separator + 
					"training" + File.separator + id);
			tdir.mkdirs();
			
			/*
			 * proceed
			 */
			for (IModel m : trainable) {
				File mdir = new File(tdir + File.separator + m.getId());
				mdir.mkdir();
				
				session.print("training " + m.getName());
				
				IModel trained = m.train(kbox, session, context, mdir);
				if (trained != null) {
					
				} else {
					session.print("not enough evidence in kbox to train " + m.getName());
				}
			}
		}
		
		return id;
		
	}
}
