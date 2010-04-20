package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.literals.DistributionValue;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.annotation.ModelAnnotation;
import org.integratedmodelling.modelling.exceptions.ThinklabModelException;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.constraint.DefaultConformance;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;
import org.integratedmodelling.thinklab.interfaces.query.IConformance;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.utils.Polylist;

import clojure.lang.IFn;

public abstract class DefaultAbstractModel implements IModel {

	protected IModel mediated = null;
	protected ArrayList<IModel> dependents = new ArrayList<IModel>();
	protected ArrayList<IModel> observed = new ArrayList<IModel>();

	protected IConcept observable = null;
	protected String observableId = null;

	// this is the defmodel <name>, complete with namespace (slash-separated)
	protected String name = null;

	protected Polylist observableSpecs = null;
	protected Object state = null;
	protected DistributionValue distribution = null;
	protected String id = null;
	protected IFn whenClause = null;
	private LinkedList<Polylist> transformerQueue = new LinkedList<Polylist>();
	protected boolean mediatesExternal;
	private boolean _validated = false;

	/*
	 * if the model was declared entifiable, this is the agent type that will
	 * incarnate the entities that can be produced from an observation of it.
	 */
	private String entityAgent = null;

	/*
	 * Any clause not intercepted by applyClause becomes metadata, which is
	 * communicated to the observation created.
	 */
	protected Metadata metadata = new Metadata();

	protected boolean isOptional = false;

	/*
	 * if scenarios can be applied to this model, the content of editable will
	 * be non-null and they will specify how we can be edited (e.g. a range of
	 * values for a state, or simply "true" for any edit).
	 */
	protected Object editable = null;

	protected boolean isMediating() {
		return mediated != null || mediatesExternal;
	}

	public String getObservableId() {
		return observableId;
	}

	public void setMetadata(String kw, Object value) {
		metadata.put(kw.startsWith(":") ? kw.substring(1) : kw, value);
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This one is invoked oncr before any use is made of the model, and is
	 * supposed to validate all concepts used in the model's definition. In
	 * order to allow facilitated and automated annotation, no model should
	 * perform concept validation at declaration; all validation should be done
	 * within this function.
	 * 
	 * Validation of concepts should be done using annotateConcept() so that
	 * annotation mode will be enabled.
	 * 
	 * @param session
	 *            TODO
	 * 
	 * @throws ThinklabException
	 */
	protected abstract void validateSemantics(ISession session)
			throws ThinklabException;

	protected IConcept annotateConcept(String conceptId, ISession session,
			Object potentialParent) throws ThinklabValidationException {

		IConcept c = KnowledgeManager.get().retrieveConcept(conceptId);
		if (c == null) {

			ModelAnnotation an = (ModelAnnotation) session
					.getVariable(ModellingPlugin.ANNOTATION_UNDERWAY);
			if (an != null) {
				an.addConcept(conceptId, potentialParent == null ? null
						: potentialParent.toString());
			} else {
				throw new ThinklabValidationException(
						"model: concept "
								+ conceptId
								+ " is undefined: please annotate this model or load knowledge");
			}
		}
		return c;
	}

	public void setObservable(Object observableOrModel)
			throws ThinklabException {

		if (observableOrModel instanceof IModel) {
			this.mediated = (IModel) observableOrModel;
			this.observableId = ((DefaultAbstractModel) observableOrModel).observableId;
			this.observableSpecs = ((DefaultAbstractModel) observableOrModel).observableSpecs;
		} else if (observableOrModel instanceof IConcept) {
			this.observable = (IConcept) observableOrModel;
			this.observableSpecs = Polylist.list(this.observable);
			this.observableId = this.observable.toString();
		} else if (observableOrModel instanceof Polylist) {
			this.observableSpecs = (Polylist) observableOrModel;
			this.observableId = this.observableSpecs.first().toString();
		} else {
			this.observableId = observableOrModel.toString();
			this.observable = KnowledgeManager.get().requireConcept(
					this.observableId);
			this.observableSpecs = Polylist.list(this.observableId);
		}

		id = observableId.toString().replace(':', '_');
	}

	@Override
	public void applyClause(String keyword, Object argument)
			throws ThinklabException {

		// System.out.println(this + "processing clause " + keyword + " -> " +
		// argument);

		if (keyword.equals(":context")) {
			Collection<?> c = (Collection<?>) argument;
			for (Object o : c) {
				addDependentModel((IModel) o);
			}
		} else if (keyword.equals(":observed")) {
			Collection<?> c = (Collection<?>) argument;
			for (Object o : c) {
				addObservedModel((IModel) o);
			}
		} else if (keyword.equals(":as")) {
			setLocalId(argument.toString());
		} else if (keyword.equals(":when")) {
			whenClause = (IFn) argument;
		} else if (keyword.equals(":editable")) {
			editable = argument;
		} else if (keyword.equals(":optional")) {
			isOptional = (Boolean) argument;
		} else if (keyword.equals(":required")) {
			isOptional = !((Boolean) argument);
		} else if (keyword.equals(":agent")) {
			entityAgent = argument.toString();
		} else {
			metadata.put(keyword.substring(1), argument);
		}
	}

	public boolean isEntifiable() {
		return entityAgent != null;
	}

	public String getEntityType() {
		return entityAgent;
	}

	/**
	 * This is called for each model defined for us in a :context clause, after
	 * the dependent has been completely specified.
	 * 
	 * @param model
	 */
	public void addDependentModel(IModel model) {
		dependents.add(model);
	}

	public void addObservedModel(IModel model) {
		observed.add(model);
	}

	/**
	 * If the resulting observation is to be transformed by a transformer obs,
	 * add a transformer definition from defmodel (e.g. :cluster (def)) in the
	 * transformer queue.
	 * 
	 * @param definition
	 */
	public void enqueueTransformer(Polylist definition) {
		transformerQueue.addLast(definition);
	}

	/**
	 * This handles the :as clause. If we don't have one, our id is the
	 * de-camelized name of our observable class.
	 * 
	 * @param id
	 */
	public void setLocalId(String id) {
		this.id = id;
	}

	protected abstract void validateMediatedModel(IModel model)
			throws ThinklabValidationException;

	@Override
	public IConcept getObservable() {
		if (observable == null) {
			try {
				observable = KnowledgeManager.get()
						.requireConcept(observableId);
			} catch (Exception e) {
				throw new ThinklabRuntimeException(e);
			}
		}
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
		observableId = model.observableId;
		editable = model.editable;
		metadata = model.metadata;
		name = model.name;
		isOptional = model.isOptional;
		whenClause = model.whenClause;
		state = model.state;
		mediatesExternal = model.mediatesExternal;
	}

	/**
	 * Generate a query that will select the requested observation type and
	 * restrict the observable to the specifications we got for this model. Use
	 * passed conformance table to define the observable constraint. Optionally
	 * add in an extent restriction.
	 * 
	 * @param extentRestriction
	 * @param conformancePolicies
	 * @param session
	 * @param extents
	 * @return
	 * @throws ThinklabException
	 */
	public Constraint generateObservableQuery(
			IntelligentMap<IConformance> conformancePolicies, ISession session,
			ArrayList<Topology> extents) throws ThinklabException {

		Constraint c = new Constraint(this
				.getCompatibleObservationType(session));

		IInstance inst = session.createObject(observableSpecs);
		IConformance conf = conformancePolicies == null ? new DefaultConformance()
				: conformancePolicies.get(inst.getDirectType());

		c = c.restrict(new Restriction(CoreScience.HAS_OBSERVABLE, conf
				.getConstraint(inst)));

		if (extents.size() > 0) {

			ArrayList<Restriction> er = new ArrayList<Restriction>();
			for (Topology o : extents) {
				Restriction r = o.getConstraint("intersects");
				if (r != null)
					er.add(r);
			}

			if (er.size() > 0) {
				c = c.restrict(er.size() == 1 ? er.get(0) : Restriction.AND(er
						.toArray(new Restriction[er.size()])));
			}
		}

		/*
		 * if we need this, we are mediators even if there was no mediated model
		 * in the specs
		 */
		mediatesExternal = true;

		return c;
	}

	@Override
	public IQueryResult observe(IKBox kbox, ISession session, Object... params)
			throws ThinklabException {

		validateConcepts(session);

		IntelligentMap<IConformance> conformances = null;
		ArrayList<Topology> extents = new ArrayList<Topology>();

		if (params != null)
			for (Object o : params) {
				if (o instanceof IntelligentMap<?>) {
					conformances = (IntelligentMap<IConformance>) o;
				} else if (o instanceof IInstance) {
					// put away all the extents we passed
					IObservation obs = ObservationFactory
							.getObservation((IInstance) o);
					if (obs instanceof Topology) {
						extents.add((Topology) obs);
					}
				} else if (o instanceof Topology) {
					extents.add((Topology) o);
				}
			}

		return observeInternal(kbox, session, conformances, extents, false);

	}

	/**
	 * This will be called once before any observation is made, or it can be
	 * called from the outside API to ensure that all concepts are valid.
	 * 
	 * @param session
	 * @throws ThinklabException
	 */
	public void validateConcepts(ISession session) throws ThinklabException {

		if (!_validated) {

			/*
			 * resolve all concepts for the observable
			 */
			if (this.observable == null)
				this.observable = annotateConcept(observableId, session, null);

			if (this.observableSpecs == null)
				this.observableSpecs = Polylist.list(this.observable);

			/*
			 * notify annotation if we are unresolved, so we can find data in
			 * this phase.
			 */
			if (!isResolved() && (this instanceof DefaultStatefulAbstractModel)) {
				ModelAnnotation an = (ModelAnnotation) session
						.getVariable(ModellingPlugin.ANNOTATION_UNDERWAY);
				if (an != null) {
					an.addUnresolvedState(this.observableId, this
							.getCompatibleObservationType(session),
							observable == null ? null
									: generateObservableQuery(null, session,
											new ArrayList<Topology>()));
				}
			}

			validateSemantics(session);

			/*
			 * validate mediated
			 */
			if (mediated != null) {

				((DefaultAbstractModel) mediated).validateConcepts(session);

				/*
				 * TODO FIXME shouldn't we call validateMediated() at this
				 * point? Nothing seems to be calling it anymore.
				 */
				metadata.merge(((DefaultAbstractModel) mediated).metadata);
			}

			/*
			 * validate dependents and observed
			 */
			for (IModel m : dependents)
				((DefaultAbstractModel) m).validateConcepts(session);

			for (IModel m : observed)
				((DefaultAbstractModel) m).validateConcepts(session);

			_validated = true;
		}
	}

	@Override
	public Model train(IKBox kbox, ISession session, Object... params)
			throws ThinklabException {
		// TODO! Needs to observe everything (including the observed) and invoke
		// a virtual
		return null;
	}

	/*
	 * this should be protected, but...
	 */
	public ModelResult observeInternal(IKBox kbox, ISession session,
			IntelligentMap<IConformance> cp, ArrayList<Topology> extents,
			boolean acceptEmpty) throws ThinklabException {

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

			ModelResult res = ((DefaultAbstractModel) mediated)
					.observeInternal(kbox, session, cp, extents, false);

			if (res == null || res.getTotalResultCount() == 0) {

				if (isOptional)
					return null;

				throw new ThinklabModelException("model: cannot observe "
						+ ((DefaultAbstractModel) mediated).observable
						+ " in kbox " + kbox);
			}

			ret.addMediatedResult(res);
		}

		/*
		 * query dependencies
		 */
		for (IModel dep : dependents) {

			ModelResult d = ((DefaultAbstractModel) dep).observeInternal(kbox,
					session, cp, extents,
					((DefaultAbstractModel) dep).isOptional);

			// can only return null if optional is true for the dependent
			if (d != null)
				ret.addDependentResult(d);
		}

		/*
		 * if no state, dependents and no mediated, we need to find an
		 * observable to mediate
		 */
		if (mediated == null && dependents.size() == 0) {

			ObservationCache cache = ModellingPlugin.get().getCache();

			if (kbox == null && cache == null) {
				if (acceptEmpty)
					return null;
				else
					throw new ThinklabModelException("model: cannot observe "
							+ observable + ": no kbox given");
			}

			if (cache != null) {

				/*
				 * build context from extent array
				 */
				IObservationContext ctx = ObservationFactory
						.buildContext(extents);

				/*
				 * lookup in cache, if existing, return it
				 */
				Polylist res = cache.getObservation(observable, ctx,
						(String) session
								.getVariable(ModelFactory.AUX_VARIABLE_DESC));
				if (res != null)
					return new ModelResult(res);
			}

			IQueryResult rs = kbox.query(generateObservableQuery(cp, session,
					extents));

			if (rs == null || rs.getTotalResultCount() == 0)
				if (acceptEmpty)
					return null;
				else
					throw new ThinklabModelException("model: cannot observe "
							+ observable + " in kbox " + kbox);

			ret.addMediatedResult(rs);
		}

		ret.initialize();

		return ret;
	}

	@Override
	public String getId() {
		return id;
	}
	
	/**
	 * 
	 * @param scenario
	 * @return
	 * @throws ThinklabException 
	 */
	@Override
	public IModel applyScenario(Scenario scenario) throws ThinklabException {		
		return applyScenarioInternal(scenario, new Session());
	}
	
	protected IModel applyScenarioInternal(Scenario scenario, Session session) throws ThinklabException {
		
		DefaultAbstractModel ret = null; 
		
		IInstance match = session.createObject(observableSpecs);
		
		/*
		 * if I am in the scenario, clone the scenario's, not me, unless I am a Model which
		 * requires to be preserved for functionality
		 */
		if (!(this instanceof Model)) {
			for (IModel m : scenario.models) {
			
				IInstance im = session.createObject(((DefaultAbstractModel)m).observableSpecs);
				if (im.isConformant(match, null)) {
					try {
						return (IModel) ((DefaultAbstractModel)m).clone();
					} catch (CloneNotSupportedException e) {
						// no
					}
				}
			}
		}
		
		/*
		 * clone me and add the applyScenarios of the dependents as dependents
		 */
		try {
			ret = (DefaultAbstractModel) this.clone();
		} catch (CloneNotSupportedException e) {
			// yeah, right		
		}

		if (mediated != null) {
			IModel dep = ((DefaultAbstractModel)mediated).applyScenarioInternal(scenario, session);
			ret.mediated = (dep == null ? mediated : dep);
		}
		
		for (IModel d : dependents) {
			IModel dep = ((DefaultAbstractModel)d).applyScenarioInternal(scenario, session);
			ret.addDependentModel(dep == null ? d : dep);
		}
		
		/*
		 * give the sucker our ID so it will work in references
		 */
		if (ret != null)
			ret.id = id;
		
		return ret;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return getConfigurableClone();
	}

}
