/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.modelling.training;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.modelling.ModelMap;
import org.integratedmodelling.modelling.ModelMap.NamespaceEntry;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.context.Context;
import org.integratedmodelling.modelling.corescience.CategorizationModel;
import org.integratedmodelling.modelling.corescience.ClassificationModel;
import org.integratedmodelling.modelling.corescience.MeasurementModel;
import org.integratedmodelling.modelling.corescience.RankingModel;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.ITrainableModel;
import org.integratedmodelling.modelling.model.DefaultAbstractModel;
import org.integratedmodelling.modelling.model.Model;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Pair;

public class TrainingManager {

	private static final String TRAINING_TIME_PROPERTY = "training.time";
	private static final String TRAINING_DATE_PROPERTY = "training.date";
	private static final String TRAINING_MODEL_PROPERTY = "training.model";
	private static final String TRAINING_ID_PROPERTY = "training.id";
	private static final String TRAINING_NAMESPACE_PROPERTY = "training.namespace";
	private static final String TRAINING_CONTEXT_PROPERTY = "training.context";
	private static final String TRAINING_EXTENTS_PROPERTY = "training.extents";

	public static TrainingManager _this = null;

	public File getMainDirectory() {
		File ret = null;
		String fenv = System.getenv("THINKLAB_ARCHIVE_DIR");
		if (fenv != null) {
			ret = new File(fenv + File.separator + "training");
		}

		if (ret == null) {
			try {
				ret = new File(ModellingPlugin.get().getScratchPath()
						+ File.separator + "training");
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		return ret;
	}
	
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
		} else if (model instanceof ITrainableModel) {
			ret.add(model);
		} else {
			for (IModel m : model.getDependencies()) {
				findTrainableInternal(m, ret);
			}
		}
	}
	
	public void deleteTrainedInstance(String id) {
		
	}
	
	/**
	 * List trained instances in session; if model != null list all that apply, otherwise list them all.
	 * 
	 * @param model
	 * @param session
	 */
	public void listTrainedInstances(IModel model, ISession session) {
		
	}
	
	public File getTrainingDir(String id, IModel model) throws ThinklabException {
		return new File(getMainDirectory() + File.separator + id + File.separator +  model.getId().replaceAll(":", "_"));
	}
	
	public IModel applyTraining(IModel model, String id, ISession session) throws ThinklabException {
		
		Properties properties = new Properties();
		
		File tprop = 
				new File(getMainDirectory() + File.separator + id + File.separator + "training.properties");
		if (!tprop.exists())
			throw new ThinklabValidationException("training instance " + id + " does not exist. Exiting.");
		
		try {
			properties.load(new FileInputStream(tprop));
		} catch (Exception e) {
			throw new ThinklabException(e);
		}
		
		/*
		 * check that this was the trained model and warn if the model namespace was
		 * modified after training.
		 */
		String modname = properties.getProperty(TRAINING_MODEL_PROPERTY);
		String namespa = properties.getProperty(TRAINING_NAMESPACE_PROPERTY);
		long traindate = Long.parseLong(properties.getProperty(TRAINING_TIME_PROPERTY));
		Date origdate  = new Date(traindate);
		
//		if (!modname.equals(model.getName())) {
//			throw new ThinklabValidationException("training instance " + id + " is for model " + modname);
//		}
		
		NamespaceEntry ns = (NamespaceEntry) ModelMap.getNamespace(namespa);
		if (ns.getLastModificationTime() > traindate) {
			session.print("*** WARNING: namespace was modified on " + new Date(ns.getLastModificationTime()) + " after training done on " + origdate);
		}
		
		/*
		 * clone the model recursively; if a model is trainable and is in the training instance,
		 * use its trained clone instead.
		 */
		return ((DefaultAbstractModel)model).createTrainedClone(id, session);
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
			IKBox kbox, ISession session, String id, String algorithm,
			int minInputs, int minOutputs) throws ThinklabException {

		Collection<IModel> trainable = findTrainableModels(model);

		if (trainable.size() > 0) {
			
			if (id == null)
				id = NameGenerator.newID();
			
			/*
			 * setup dirs for serialization
			 */
			File tdir = 
				new File(getMainDirectory() + File.separator + id);
			tdir.mkdirs();
			
			/*
			 * TODO write property file with all info - date, model name, model last mod/version etc.
			 */
			File tprop = 
					new File(getMainDirectory() + File.separator + id + File.separator + "training.properties");
			
			Properties properties = new Properties();
			
			if (tprop.exists()) {
				try {
					properties.load(new FileInputStream(tprop));
				} catch (Exception e) {
					throw new ThinklabIOException(e);
				}
			}
			
			properties.setProperty(TRAINING_TIME_PROPERTY, new Date().getTime()+"");
			properties.setProperty(TRAINING_DATE_PROPERTY, new Date().toString());
			properties.setProperty(TRAINING_MODEL_PROPERTY, model.getName());
			properties.setProperty(TRAINING_NAMESPACE_PROPERTY, model.getNamespace());
			properties.setProperty(TRAINING_ID_PROPERTY, model.getId());
			properties.setProperty(TRAINING_CONTEXT_PROPERTY, ((Context)context).getName());
			properties.setProperty(TRAINING_EXTENTS_PROPERTY, ((Context)context).list());

			/*
			 * TODO serialize extents and store in properties
			 */
			
			/*
			 * proceed
			 */
			for (IModel m : trainable) {
				
				File mdir = new File(tdir + File.separator + m.getId().replaceAll(":", "_"));
				mdir.mkdir();
								
				IModel trained = ((ITrainableModel)m).train(kbox, session, context, mdir, new Pair<Integer,Integer>(minInputs, minOutputs));
				if (trained != null) {
					
					/*
					 * write out the model, but that should have been done already by train().
					 */
					
					/*
					 * TODO report number of trained models
					 */
					
				} 
			}
			
			try {
				properties.store(new FileOutputStream(tprop), "Generated by Thinklab training subsystem. Do not modify.");
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}
		}
		
		return id;
		
	}

	public boolean isEvidenceModel(IModel model) {
		
		/*
		 * if Model, all contingencies must be evidence models;
		 * otherwise, it must be a measurement, classification, categorization or ranking 
		 * with no dependencies that mediates nothing or another evidence model.
		 */
		if (model instanceof Model) {
			for (IModel m : ((Model)model).getObservers()) {
				if (!isEvidenceModel(m))
					return false;
			}
		} else {
			return 
					model.getDependencies().size() == 0 && 
					(((DefaultAbstractModel)model).getMediated() == null ||
						isEvidenceModel(((DefaultAbstractModel)model).getMediated())) &&
					(model instanceof MeasurementModel ||
					 model instanceof RankingModel ||
					 model instanceof CategorizationModel ||
					 model instanceof ClassificationModel);
		}
		return true;
	}
}
