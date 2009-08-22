package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.observations.ObservationFactory;
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

		
//	protected ModelResult realizeInternal(ModelResult root, IKBox kbox, ISession session, Map<IConcept, IConformance> conformancePolicies, Restriction extentQuery) throws ThinklabException {
//		
//		ModelResult ret = new ModelResult();
//		
//		if (root == null) {
//			ret.ticker = new Ticker();
//		} else {
//			ret.ticker = root.ticker;
//		}
//		
//		boolean solved = false;
//		
//		/*
//		 * do we have a mediated model? Add that
//		 */
//		if (mediated != null) {
//			ret.type = ModelResult.MEDIATOR;
//			ret.add(((DefaultAbstractModel) mediated).realizeInternal(ret, kbox, session, conformancePolicies, extentQuery));
//			solved = true;
//		}
//		
//		/*
//		 * are we a transformer? Just realize the dependencies
//		 */
//
//		/*
//		 * if we have a state, we don't need anything else but our specs
//		 */
//		if (state == null) {
//			solved = true;
//		}
//
//		/*
//		 * get the specs
//		 */
//		ret.specs = buildDefinition();
//		
//		/*
//		 * if we don't have a state and we are neither, we need to lookup the 
//		 */
//		if (!solved) {
//			/*
//			 * we need an external observation
//			 */
//			// FIXME pass the map appropriately
//			Constraint c = generateObservableQuery(extentQuery, (IntelligentMap<IConformance>) conformancePolicies, session);
//			if (c != null) {
//				ret.obsHits = kbox.query(c);
//			}
//			ret.type = ModelResult.EXTERNAL;
//		}
//		return null;
//	}

	@Override
	public Polylist buildObservation(IKBox kbox) throws ThinklabException {

		boolean solved = false;
		Polylist ret = null;
		
		/*
		 * do we have a mediated model? Add that
		 */
		if (mediated != null) {
		}
		
		/*
		 * are we a transformer? Just realize the dependencies
		 */
		

		/*
		 * get the specs
		 */
		Polylist specs = buildDefinition();
		
		/*
		 * if we have a state, we don't need anything else but our specs
		 */
		if (state != null) {
			solved = true;
		} else {
			for (IModel dep : dependents) {
				/*
				 * TODO screw this - we need a MAIN one, top down, to replace run() returning a list. The whole ticker thing should be
				 * external - collect queries in a tree and run all, then build ticker and call it.
				 */
				specs = ObservationFactory.addDependency(specs, dep.buildObservation(kbox));
			}
		}
		
		/*
		 * if we don't have a state and we are neither, we need to lookup the 
		 */
		if (!solved) {
			
			/*
			 * lookup the best (first) external observation TODO THIS MUST BE EXTERNAL - JUST GET THE PROPER INDEX AND MOD RESULT FROM 
			 * OUTSIDE LOGICS
			 */
//			Constraint c = generateObservableQuery(extentQuery, (IntelligentMap<IConformance>) conformancePolicies, session);
//			if (c != null) {
				// TODO this is an instance 
//			}
		}
		
		return ret;
		
	}
}
