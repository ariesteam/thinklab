package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.storage.SwitchLayer;
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

	Object state = null;
	private boolean contingencyModelBuilt;

	
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
	
		ModelResult ret = new ModelResult(this, kbox, session);
		SwitchLayer<IModel> switchLayer = null;

		ObservationContext exts = 
			(extents == null || extents.size() == 0) ?
				null :
				new ObservationContext(extents.toArray(new Topology[extents.size()]));
		
		if (exts != null)
			switchLayer = new SwitchLayer<IModel>(exts);
			
		/*
		 * TODO if there is a context model, observe it now in the same context - even if we have only one model
		 */
		int ctidx = 0;
		for (IModel m : models) {
			
			/*
			 * TODO the switch layer stuff can only known when extents are known for all subobservations, so it must be moved into
			 * ModelResult.
			 * if no more models are needed to fully cover extent, proceed.
			 */
			if (switchLayer != null && switchLayer.isCovered())
				break;
			
			/*
			 * TODO
			 * if model has a where clause and we have a context model, we must compute the where clause across its context
			 * and activate appropriately; if no state is activated, don't observe it.
			 *
			 * if model doesn't have a where clause, activate its full coverage into the switch layer, which can vary according
			 * to its 
			 */

			ModelResult mr = ((DefaultAbstractModel)m).observeInternal(kbox, session, cp, extents, acceptEmpty);

			if (mr != null) {
				ret.addContingentResult(mr);
				ctidx ++;
			}

		}
		
		/*
		 * TODO if we have a switch layer, add it to model result
		 */
		if (switchLayer != null)
			ret.setSwitchLayer(switchLayer);
		
		
		return ret;
		
	}
	public void setDescription(String s) {
		this.description = s;
	}

	public void addContingency(IModel m, Map<?,?> metadata) {
		
		if (this.context == null)
			this.context = new ArrayList<IModel>();
		this.context.add(m);
	}
	
	/**
	 * Can be called once or more; models are passed after being configured with their
	 * clauses. They may have :when clauses to condition them to a particular context
	 * state, or have the implicit :when :observable clause which makes them apply
	 * as default in order of declaration, until the context is covered.
	 */
	public void defModel(IModel model, Map<?,?> metadata) {
		
		if (this.models == null) {
			this.models = new ArrayList<IModel>();
		}
		
		this.models.add(model);
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


	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session)
			throws ThinklabException {
		// WON'T GET CALLED UNLESS I SCREWED UP
		throw new ThinklabInternalErrorException("SHIT! BUILDDEFINITION CALLED ON MODEL!");
	}
}
