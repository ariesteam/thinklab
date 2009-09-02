package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.cmodel.ExtentConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtent;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.modelling.exceptions.ThinklabModelException;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.IntelligentMap;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.constraint.DefaultConformance;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.query.IConformance;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
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
	protected boolean mediatesExternal;
	
	protected boolean isMediating() {
		return mediated != null || mediatesExternal;
	}
	
	public void setObservable(Object observableOrModel) throws ThinklabException {
		
		if (observableOrModel instanceof IModel) {
			this.mediated = (IModel) observableOrModel;
			this.observable = ((IModel)observableOrModel).getObservable();
			this.observableSpecs = ((DefaultAbstractModel)observableOrModel).observableSpecs;
		} else if (observableOrModel instanceof IConcept) {
			this.observable = (IConcept) observableOrModel;
			this.observableSpecs = Polylist.list(this.observable);
		} else if (observableOrModel instanceof Polylist) {
			this.observableSpecs = (Polylist)observableOrModel;
			this.observable = KnowledgeManager.get().requireConcept(this.observableSpecs.first().toString());
		} else {
			this.observable = KnowledgeManager.get().requireConcept(observableOrModel.toString());
			this.observableSpecs = Polylist.list(this.observable);
		}
		
		id = observable.toString().replace(':','_');
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
	 * @param extents 
	 * @return
	 * @throws ThinklabException
	 */
	public Constraint generateObservableQuery(
			IntelligentMap<IConformance> conformancePolicies,
			ISession session, ArrayList<IObservation> extents) throws ThinklabException {

		Constraint c = new Constraint(this.getCompatibleObservationType(session));
		
		IInstance inst = session.createObject(observableSpecs);
		IConformance conf = 
			conformancePolicies == null ? 
					new DefaultConformance() :
					conformancePolicies.get(inst.getDirectType());
					
		c = c.restrict(
				new Restriction(CoreScience.HAS_OBSERVABLE, conf.getConstraint(inst)));

		if (extents.size() > 0) {
			
			ArrayList<Restriction> er = new ArrayList<Restriction>();
			for (IObservation o : extents) {
				Restriction r =
					((ExtentConceptualModel)(o.getConceptualModel())).
						getConstraint("contains");
				if (r != null)
					er.add(r);
			}
			
			if (er.size() > 0) {
				c = c.restrict(
						er.size() == 1 ? 
							er.get(0) : 
							Restriction.AND(er.toArray(new Restriction[er.size()])));
			}
		}
		
		/*
		 * if we need this, we are mediators even if there was no mediated model in the specs
		 */
		mediatesExternal = true;
		
		return c;
	}

	@Override
	public IQueryResult observe(IKBox kbox, ISession session, Object ... params) throws ThinklabException {
		
		IntelligentMap<IConformance> conformances = null;
		ArrayList<IObservation> extents = new ArrayList<IObservation>();
		
		if (params != null)
			for (Object o : params) {
				if (o instanceof IntelligentMap<?>) {
					conformances = (IntelligentMap<IConformance>) o;
				} else if (o instanceof IInstance) {
					// put away all the extents we passed
					IObservation obs = ObservationFactory.getObservation((IInstance)o);
					if (obs.getConceptualModel() instanceof ExtentConceptualModel) {
						extents.add(obs);
					}
				}
			}

		ModelResult ret = observeInternal(kbox, session, conformances, extents);
		
		/*
		 * add all extent specifications to the root observation
		 */
		for (IObservation obs : extents) {
			
			IExtent ext = ((ExtentConceptualModel)(obs.getConceptualModel())).getExtent();
			if (ext instanceof IConceptualizable) {
				ret.addExtentObservation(((IConceptualizable)ext).conceptualize());
			}
		}
		
		return ret;
	}
	
	/*
	 * this should be protected, but...
	 */
	public ModelResult observeInternal(IKBox kbox, ISession session, IntelligentMap<IConformance> cp, ArrayList<IObservation> extents) throws ThinklabException {
		
		ModelResult ret = new ModelResult(this, kbox, session);
		
		/*
		 * if we're resolved, the model result contains all we need to know
		 */
		if (state != null)
			return ret;
		
		/*
		 * if mediated, realize mediated and add it
		 */
		if (mediated != null) {

			ModelResult res = ((DefaultAbstractModel)mediated).observeInternal(kbox, session, cp, extents);

			if (res == null || res.getTotalResultCount() == 0) {
				throw new ThinklabModelException(
						"model: cannot observe " +
						((DefaultAbstractModel)mediated).observable +
						" in kbox " +
						kbox);
			}
			
			ret.addMediatedResult(res);
		}
		
		/*
		 * query dependencies
		 */
		for (IModel dep : dependents) {
			ret.addDependentResult(((DefaultAbstractModel)dep).observeInternal(kbox, session, cp, extents));
		}
		
		/*
		 * if no state, dependents and no mediated, we need to find an observable to mediate
		 */
		if (mediated == null && dependents.size() == 0) {

			if (kbox == null) {
				throw new ThinklabModelException(
						"model: cannot observe " + observable + ": no kbox given");
			}
			
			IQueryResult rs = kbox.query(generateObservableQuery(cp, session, extents));
			
			if (rs == null || rs.getTotalResultCount() == 0)
				throw new ThinklabModelException(
						"model: cannot observe " +
						observable +
						" in kbox " +
						kbox);
			
			ret.addMediatedResult(rs);
		}
		
		ret.initialize();
		
		return ret;
	}

	@Override
	public String getId() {
		return id;
	}
}
