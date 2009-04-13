package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.CamelCase;
import org.integratedmodelling.utils.Polylist;

public abstract class DefaultAbstractModel implements IModel {

	protected IModel mediated = null;
	protected IConcept observable = null;
	protected Polylist observableSpecs = null;
	protected Object state = null;
	protected String id = null;
	private ArrayList<IModel> dependents = new ArrayList<IModel>();
	private Polylist whenClause = null;
	
	public void setObservable(Object observableOrModel) throws ThinklabException {
		
		System.out.println("got observable " + observableOrModel.getClass() + ": " + observableOrModel);
		
		if (observableOrModel instanceof IModel) {
			this.mediated = (IModel) observableOrModel;
			this.observable = ((IModel)observableOrModel).getObservable();
		} else if (observableOrModel instanceof IConcept) {
			this.observable = (IConcept) observableOrModel;
		} else if (observableOrModel instanceof Polylist) {
			this.observableSpecs = (Polylist)observableOrModel;
			this.observable = KnowledgeManager.get().requireConcept(this.observableSpecs.first().toString());
		} else {
			this.observable = KnowledgeManager.get().requireConcept(observableOrModel.toString());
		}
		
		id = CamelCase.toLowerCase(observable.toString(), '-');
	}
	
	/*
	 * collect all the observable and the models for them. If any of the
	 * concepts has more than one models, we must have a contingency model
	 * to choose it in each contingency. If any of the concepts has a null
	 * or empty array of models, or the model linked is unresolved,
	 * we need to resolve the observable from external resources.
	 */
	protected HashMap<IConcept, ArrayList<IModel>> collectModels() {
		return null;
	}
	
	@Override
	public void applyClause(String keyword, Object argument) throws ThinklabException {
		
		System.out.println(this + "processing clause " + keyword + " -> " + argument);
		
		if (keyword.equals(":context")) {
			
			Collection<?> c = (Collection<?>) argument;
			for (Object o : c) {
				addDependentModel((IModel) o);
			}
			
		} else if (keyword.equals(":as")) {
			
			setLocalId(argument.toString());
			
		} else if (keyword.equals(":when")) {
			
			whenClause = (Polylist) argument;
		}
	}
	
	/**
	 * This is called for each model defined for us in a :context clause, after the dependent has been
	 * completely specified.
	 * 
	 * @param model
	 */
	public void addDependentModel(IModel model) {
		dependents.add(model);
	}

	/**
	 * This handles the :when condition if any is given for us in defmodel.
	 * 
	 * @param condition
	 */
	public void addConditionalClause(Polylist condition) {
		
	}
	
	/**
	 * This handles the :as clause. If we don't have one, our id is the de-camelized name of
	 * our observable class.
	 * 
	 * @param id
	 */
	public void setLocalId(String id) {
		this.id = id;
	}
	
	protected abstract void validateMediatedModel(IModel model) throws ThinklabValidationException;
	

	@Override
	public IConcept getObservable() {
		return observable;
	}
	
	@Override
	public boolean isResolved() {
		return state != null || mediated != null;
	}
	
	/*
	 * Copy the relevant fields when a clone is created before configuration
	 */
	protected void copy(DefaultAbstractModel model) {
		id = model.id;
		mediated = model.mediated;
		observable = model.observable;
		observableSpecs = model.observableSpecs;
	}

}
