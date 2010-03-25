package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.modelling.exceptions.ThinklabModelException;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;
import org.integratedmodelling.thinklab.interfaces.query.IConformance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

/**
 * The "default" model class reflects the defmodel form, and has
 * both a contingency structure and a dependency structure, which can be resolved
 * to observations using a kbox and a session. The contingency structure is 
 * used to contextualize the dependencies, and the result is a set of contextualizable
 * models wrapped in one observation.
 * 
 * The run() method will build the model, returning a new main observation that can be 
 * contextualized to produce states for ("run") all the observations computed.
 * 
 * A Model won't receive any clauses from defmodel, because the copy-on-write pattern built into 
 * defmodel will create a ProxyModel whenever clauses are specified. 
 * 
 * @author Ferdinando Villa
 * @date Jan 25th, 2008.
 */
public class Model extends DefaultAbstractModel {

	ArrayList<IModel> models = null;
	Collection<IModel> context = null;
	Collection<String> contextIds = null;
	String description = null;
	IObservation contingencyModel = null;
	
	/**
	 * Stores state and variable info related to the specific contingency for which we're building
	 * a model. Each state of the contingency model corresponds to one of these, returned by the
	 * contingency iterator. Just a stub class for now.
	 * 
	 * @author Ferdinando
	 *
	 */
	public class Contingency {
		
	}
		
	/**
	 * Iterates the states of the contingency observation - which may be simply the 
	 * identification of the model if no contingency model is seen across the hierarchy.
	 * 
	 * @author Ferdinando
	 *
	 */
	public class ContingencyIterator implements Iterator<Contingency> {

		int cState = 0;
		int nStates = 1;
		IObservation contingencyModel = null;
		
		/*
		 * pass null if no contingency model exists. This equates to 
		 * :when :observable - the contingency model is determined by
		 * the intersection of the contexts.
		 */
		public ContingencyIterator(IObservation contingencyModel) {
		}
		
		@Override
		public boolean hasNext() {
			return cState < nStates;
		}

		@Override
		public Contingency next() {
			cState ++;
			return null;
		}

		@Override
		public void remove() {
		}
	}
	
	IConcept observable = null;
	Polylist observableSpecs = null;
	Object state = null;
	private boolean contingencyModelBuilt;
	
	/*
	 * This iterates over the states of the contingency model. Each Contingency contains the 
	 * values of the context variables for each state.
	 */
	protected ContingencyIterator getContingencyIterator(ISession session, IKBox kbox) {

		if (!contingencyModelBuilt) {
			buildContingencyModel(kbox, session);
		}
		return new ContingencyIterator(contingencyModel);
	}
	
	private void buildContingencyModel(IKBox kbox, ISession session) {
		
		/*
		 * TODO build the contingency model. For now this only passes a null, resulting
		 * in one contingency state. 
		 * 
		 * The contingency model must accomodate zero or one models and each
		 * model is conditioned to it. If no :when clause exists, the implicit
		 * contingency clause is :when :observable - meaning, this is the model
		 * to use when it is possible to use it. The order of declaration counts
		 * as priority order to decide which model is used first to cover the
		 * context.
		 * 
		 */
		contingencyModelBuilt = true;
	}
	
	
	@Override
	public ModelResult observeInternal(IKBox kbox, ISession session, IntelligentMap<IConformance> cp, ArrayList<Topology> extents, boolean acceptEmpty)  throws ThinklabException {
	
		ModelResult ret = null;
		ArrayList<Polylist> cmodels = new ArrayList<Polylist>();
		
		if (models.size() == 1)
			ret = ((DefaultAbstractModel)(models.get(0))).observeInternal(kbox, session, cp, extents, acceptEmpty);
		else {

			ret = new ModelResult(this, kbox, session);
			
			for (ContingencyIterator it = getContingencyIterator(session, kbox); it.hasNext(); ) {

				Contingency cn = it.next();
				
				/*
				 * TODO this will return an array of models, which may need to be
				 * observed in sequence and combined as unconditional
				 * contingencies when >1 is observable.
				 * 
				 * It should be like saying :when :observable - the default
				 * contingency clause.
				 */
				IModel cmod = chooseModel(models, cn, kbox);
				if (cmod == null) {
					throw new ThinklabModelException(
							"cannot choose a model formulation for " +
							observable +
							" in context " +
							cn +
							": no matching submodel");
				}
				
				ModelResult contingentRes = ((DefaultAbstractModel)cmod).observeInternal(kbox, session, cp, extents, acceptEmpty);
				ret.addContingentResult(contingentRes);
			}

		}
		
		return ret;
		
	}

	
	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session)  throws ThinklabException {
	
		Polylist ret = null;
		ArrayList<Polylist> cmodels = new ArrayList<Polylist>();
		
		for (ContingencyIterator it = getContingencyIterator(session, kbox); it.hasNext(); ) {
			
			Contingency contingency = it.next();
			cmodels.add(buildDefinition(contingency, kbox, session));
		}
		
		if (cmodels.size() == 1)
			ret = cmodels.get(0);
		else {
			ret = Polylist.list(getCompatibleObservationType(session));
			ret = ObservationFactory.setObservable(ret, observableSpecs);
			
			for (Polylist cont : cmodels) 
				ret = ObservationFactory.addContingency(ret, cont);
		}
		
		return ret;
		
	}

	
	/**
	 * Build a model using our specifications, the passed context to resolve any :when clauses,
	 * and the given kbox.
	 * 
	 * @param context
	 * @return
	 */
	private Polylist buildDefinition(Contingency context, IKBox kbox, ISession session) throws ThinklabException {
		
		/*
		 * if there's only one model, that's what we return
		 */
		IModel model = chooseModel(models, context, kbox);
		if (model == null) {
			throw new ThinklabModelException(
					"cannot choose a model formulation for " +
					observable +
					" in context " +
					context +
					": no matching submodel");
		}
		return model.buildDefinition(kbox, session);
	}
	
	/*
	 * Choose the appropriate model for the context. 
	 * 
	 * TODO this should return an ARRAY of models that apply to the situation. They should be
	 * tried in sequence until one returns observations. This way we can use multiple 
	 * alternative definitions, prioritizing them in order of declaration and/or using
	 * the states of another observation. 
	 * 
	 * @param models2
	 * @param context2
	 * @param kbox
	 * @return
	 */
	private IModel chooseModel(ArrayList<IModel> models2, Contingency context2,
			IKBox kbox) {
		
		if (models.size() == 1)
			return models.get(0);
		
		/* TODO RETE stuff goes here */
		
		return null;
	}

	public void setDescription(String s) {
		description = s;
	}

	public void addContingency(IModel m, Map<?,?> metadata) {
		
		if (context == null)
			context = new ArrayList<IModel>();
		context.add(m);
	}
	
	/**
	 * Can be called once or more; models are passed after being configured with their
	 * clauses. They may have :when clauses to condition them to a particular context
	 * state, or have the implicit :when :observable clause which makes them apply
	 * as default in order of declaration, until the context is covered.
	 */
	public void defModel(IModel model, Map<?,?> metadata) {
		
		// System.out.println("setting unconditional " + model);
		if (models == null) {
			models = new ArrayList<IModel>();
		}
		
		if (metadata != null) {
			// TODO use it
			System.out.println("\nMETADATA! " + metadata + "\n");
		}
		
		models.add(model);
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		return CoreScience.Observation();
	}

	@Override
	public boolean isResolved() {
		// TODO this depends
		return true;
	}
	
	@Override
	public String toString() {
		// add the different possible incarnations of the model after the closed parenthesis
		String mdesc = "{";
		for (IModel m : models) {
			mdesc += (mdesc.length() == 1 ? "" : ",") + m ;
		}
		mdesc += "}";
		return "model(" + getObservable() +") " + mdesc;
	}


	@Override
	public void applyClause(String keyword, Object argument) throws ThinklabException {
		throw new ThinklabInternalErrorException(
				"internal error: a Model should only be configured through a proxy");
	}

	@Override
	public IModel getConfigurableClone() {
		/*
		 * Skip copying
		 */
		return new ModelProxy(this);
	}

	@Override
	protected void validateMediatedModel(IModel model)
			throws ThinklabValidationException {
		throw new ThinklabValidationException("model " + id + " cannot mediate another model");
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void validateSemantics(ISession session) throws ThinklabException {

		// validate all contingent and contingency models
		if (context != null)
			for (IModel m : context) {
				((DefaultAbstractModel)m).validateConcepts(session);
			}
		
		if (models != null)
			for (IModel m : models) {
				((DefaultAbstractModel)m).validateConcepts(session);
			}
	}

	/**
	 * Return the definition of this model, assuming it is only one model, and throw an
	 * exception if the definition has contingencies.
	 * @return
	 */
	public IModel getDefinition() {
		
		if (models.size() != 1)
			throw new ThinklabRuntimeException("model: getDefinition called on a model with contingencies");

		return models.get(0);
	}
	
	/**
	 * 
	 * @param scenario
	 * @return
	 */
	public Model applyScenario(Scenario scenario) {
		
		/*
		 * 1. determine common observables
		 */
		
		/*
		 * 2. build an identification with just the relevant
		 * observations
		 */
		
		/*
		 * 3. contextualize to model's context
		 */
		
		/*
		 * 4. build a new model with computed datasources
		 */
		
		return null;
	}

	/**
	 * Return a scenario with all the observables that were declared
	 * editable in the defmodel form.
	 * 
	 * @return 
	 */
	public Scenario getDefaultScenario() {
		
		Scenario ret = new Scenario();
		collectEditableModels(this, ret);
		return ret;
	}

	private void collectEditableModels(DefaultAbstractModel model, Scenario ret) {
		if (this.editable != null) {
			ret.addModel(this, null, this.editable);
		}
		for (IModel m : dependents)
			collectEditableModels((DefaultAbstractModel)m, ret);
	}
}
