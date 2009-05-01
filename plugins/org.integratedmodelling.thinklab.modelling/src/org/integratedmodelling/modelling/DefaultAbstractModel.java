package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.kbox.RankingKBox;
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
	private LinkedList<Polylist> transformerQueue = new LinkedList<Polylist>();
	
	public void setObservable(Object observableOrModel) throws ThinklabException {
		
		System.out.println("got observable " + observableOrModel.getClass() + ": " + observableOrModel);
		
		if (observableOrModel instanceof IModel) {
			this.mediated = (IModel) observableOrModel;
			this.observable = ((IModel)observableOrModel).getObservable();
		} else if (observableOrModel instanceof IConcept) {
			this.observable = (IConcept) observableOrModel;
			this.observableSpecs = Polylist.list(this.observable);
		} else if (observableOrModel instanceof Polylist) {
			this.observableSpecs = (Polylist)observableOrModel;
			this.observable = KnowledgeManager.get().requireConcept(this.observableSpecs.first().toString());
		} else {
			this.observable = KnowledgeManager.get().requireConcept(observableOrModel.toString());
		}
		
		id = CamelCase.toLowerCase(observable.toString(), '-');
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
	 * If the resulting observation is to be transformed by a transformer obs,
	 * add a transformer definition from defmodel (e.g. :cluster (def)) in the transformer
	 * queue.
	 * 
	 * @param definition
	 */
	public void enqueueTransformer(Polylist definition) {
		transformerQueue.addLast(definition);
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
	
	/**
	 * Utility: find a unambiguous instance in a kbox that represents the passed
	 * observable, and make sure it is an observation of the given type before
	 * returning it.
	 * 
	 * @param observable
	 * @param kbox
	 * @param requiredObsType
	 * @return
	 * @throws ThinklabException
	 */
	public static IInstance resolveObservable(IConcept observable, IKBox kbox,
			IConcept requiredObsType, ISession session)
			throws ThinklabException {

		IInstance ret = null;

		Constraint c = new Constraint(requiredObsType)
				.restrict(new Restriction(CoreScience.HAS_OBSERVABLE,
						new Constraint(observable)));

		IQueryResult res = kbox.query(c);

		if (res.getTotalResultCount() > 0) {

			if (res.getTotalResultCount() > 1 && !(kbox instanceof RankingKBox))
				throw new ThinklabValidationException(
						"ambiguous results resolving observation of "
								+ observable + " in kbox " + kbox);

			ret = res.getResult(0, session).asObjectReference().getObject();
		}

		return ret;
	}


}
