package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.modelling.Model.Contingency;
import org.integratedmodelling.modelling.Model.ContingencyIterator;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.IntelligentMap;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.query.IConformance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.CamelCase;
import org.integratedmodelling.utils.LogicalConnector;
import org.integratedmodelling.utils.Polylist;

public abstract class DefaultAbstractModel implements IModel {

	protected IModel mediated = null;
	private ArrayList<IModel> dependents = new ArrayList<IModel>();

	protected IConcept observable = null;
	protected Polylist observableSpecs = null;
	protected Object state = null;
	protected String id = null;
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
	 * Generate a query that will select the requested observation type and restrict the
	 * observable to the specifications we got for this model. Use passed conformance table
	 * to define the observable constraint. Optionally add in an extent restriction.
	 * 
	 * @param extentRestriction
	 * @param conformancePolicies
	 * @param session
	 * @return
	 * @throws ThinklabException
	 */
	public Constraint generateObservableQuery(
			Restriction extentRestriction, 
			IntelligentMap<IConformance> conformancePolicies,
			ISession session) throws ThinklabException {

		Constraint c = new Constraint(this.getCompatibleObservationType(session));
		
		IInstance inst = session.createObject(observableSpecs);
		IConformance conf = conformancePolicies.get(inst.getDirectType());
		return c.restrict(
					LogicalConnector.INTERSECTION,
					new Restriction(CoreScience.HAS_OBSERVABLE, conf.getConstraint(inst)),
					extentRestriction);
	}

	
	/**
	 * Return one realization of the model on this kbox. Should keep returning these in a lazy
	 * fashion until there are no more. Then it should return null.
	 * 
	 * This version of realize() applies to predefined (not defmodel) models, so it will only handle
	 * mediation, dependencies and kbox search. The derived Model class will override this to also handle 
	 * contingencies and alternative model definitions.
	 * 
	 * TODO so this should return a query result instead, with one field and pointers to all results
	 * for kbox-based obs, the defs of the fixed ones etc. Contingencies complicate this one enormously,
	 * so for now we do it all inside the model.
	 * 
	 * @param kbox
	 * @param session
	 * @param conformancePolicies
	 * @param extentQuery
	 * @return
	 * @throws ThinklabException
	 */
	public IInstance realize(IKBox kbox, ISession session, Map<IConcept, IConformance> conformancePolicies, Constraint extentQuery) throws ThinklabException {
		
		/*
		 * if we had a previous query done AND it's our turn to change, we want another result from it.
		 * TODO we need a shared ticker and an index for each changing model. This should probably be
		 * a query result kind of object.
		 */
		
		/*
		 * if we have a state, we're realized
		 */
		
		return null;
	}

}
