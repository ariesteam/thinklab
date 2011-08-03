package org.integratedmodelling.thinklab.modelling.model.implementation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.modelling.factories.IModelFactory;
import org.integratedmodelling.thinklab.api.modelling.observation.IObservation;
import org.integratedmodelling.thinklab.api.modelling.observation.IState;

/**
 * The reference implementation of the model object created by defmodel. It holds one
 * or more concrete models (e.g. measurements, rankings etc) that can represent the
 * observable, and is capable of selecting the applicable one(s) appropriately to 
 * observe the observable in a given context, generating the Observation structure
 * that can be resolved to States through contextualization.
 *  
 * Compared to any other model, it supports:
 * 
 * <ol>
 * <li>a context model that is computed before the others and drives the choice of
 *     contingent models;</li>
 * <li>multiple models (contingencies) to be selected on the basis of the context
 * 	   model if present, or on previous models not being defined in each specific
 * 	   context state;</li>
 * </ol>
 * 	
 * @author Ferd
 *
 */
public class Model extends DefaultAbstractModel {

	IModel _contextModel = null;
		
	public Model(INamespace namespace) {
		super(namespace);
	}
	
	
	// --- following API should be private to ModelFactory; could use interface segregation in the API
	// --- at some point.
	
	
	@SuppressWarnings("unchecked")
	@Override
	public IModel define(Map<String, Object> def) throws ThinklabException {
		
		super.define(def);
		
		IModel cm = (IModel) def.get(IModelFactory.K_CONTEXTMODEL);
		if (cm != null)
			this._contextModel = cm;

		List<IModel> mods = (List<IModel>) def.get(IModelFactory.K_DEFINITION);
		if (mods != null) {
			
			List<IExpression> as = (List<IExpression>) def.get(IModelFactory.K_CONDITIONALS);
			
			
		}
		
		return this;
	}


	@Override
	public IObservation createObservation(HashMap<IInstance, IState> known) {
		// TODO Auto-generated method stub
		return null;
	}


}
