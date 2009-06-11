package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
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
public class Model extends DefaultAbstractModel implements IConceptualizable {

	ArrayList<IModel> models = null;
	Collection<IModel> context = null;
	Collection<String> contextIds = null;
	IKBox contKbox = null;
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
	
	public class LinkedModel {
		
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
		 * pass null if no contingency model exists
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
	
	protected ContingencyIterator getContingencyIterator(ISession session, IKBox contingencyKbox) {

		if (!contingencyModelBuilt) {
			buildContingencyModel(session);
		}
		return new ContingencyIterator(contingencyModel);
	}
	
	private void buildContingencyModel(ISession session) {
		
		/*
		 * TODO build the contingency model. For now this only passes a null, resulting
		 * in one contingency state.
		 */
		contingencyModelBuilt = true;
	}

	/**
	 * Run the model in the given session, using the passed kboxes and topology if
	 * any. It just builds the instance of an observation for the given concept, but
	 * it is left to the user to contextualize it to obtain states.
	 * 
	 * @param session where we create the whole thing
	 * @param params may contain one kbox (used for both context and deps), two
	 * 	kboxes (used for context and deps respectively) and/or a topology (observation
	 *  context) used to define the overall topology for the context.
	 *   
	 * @return the uncontextualized observation representing the model.
	 * 
	 * @throws ThinklabException
	 */
	public IInstance run(ISession session, Collection<Object> params) throws ThinklabException {
		
		IKBox contKbox = null;
		IKBox depsKbox = null;
		Constraint contextQuery = null;
		
		if (params != null)
			for (Object o : params) {
				if (o instanceof IKBox) {
					if (contKbox == null)
						contKbox = (IKBox) o;
					else 
						depsKbox = (IKBox) o;
				} else if (o instanceof IInstance) {
					contextQuery = null; // TODO turn the ctx of the instance into a query
				} else if (o instanceof Constraint) {
					contextQuery = (Constraint) o;
				}
			}

		if (contextQuery != null) {
			// TODO filter kboxes or pass query downstream
		}
		
		return session.createObject(buildObservation(session, contKbox, depsKbox, contextQuery));

	}
	
	private Polylist buildObservation(ISession session, IKBox contKbox, IKBox depsKbox, Constraint contextQuery) 
		throws ThinklabException {
	
		
		Polylist ret = null;
		ArrayList<Polylist> cmodels = new ArrayList<Polylist>();
		
		for (ContingencyIterator it = getContingencyIterator(session, contKbox); it.hasNext(); ) {
			
			Contingency contingency = it.next();
			LinkedModel context = linkDependencies(contingency, depsKbox);
			
			// TODO this should take no argument or a substitution observation if the obs is unresoslved.
			cmodels.add(buildObservation(context));
		}
		
		if (cmodels.size() == 1)
			ret = cmodels.get(0);
		else {

			/*
			 * TODO create a spec for a main obs with all contingencies linked
			 */
		}
		
		return ret;
		
	}

	private Polylist buildObservation(LinkedModel context) {
		
		/*
		 * 1. 
		 * 
		 * 2. build specs for the chosen obs for our observable
		 * 
		 * 3. for all dependent observables, add the chosen obs;
		 * 
		 * 4. add the mediated obs if any;
		 * 
		 * 5. for each transformer, wrap current result as its dependency (ret = transform(ret)).
		 */
		
		return null;
	}
	
	/**
	 * Produce a map of observable -> model/observation handling linkage of models to observables
	 * using the passed kbox. If unlinked observables exist or linkage is ambiguous, throw an exception.
	 * 
	 * This should configure and run the RETE engine if any when clauses exist. As such, it's not
	 * necessarily a simple function.
	 * 
	 * @param contingency
	 * @param depsKbox
	 * @return
	 */
	private LinkedModel linkDependencies(Contingency contingency, IKBox depsKbox) throws ThinklabException {

		/*
		 * scan context; for all unresolved ones, lookup an appropriate obs - meaning
		 * we need to pass the context here, too, as a query built from another obs most
		 * likely. If any remain unresolved (and have no :ask clause which we will 
		 * implement later) throw an exception. If the one being built is a Model, we
		 * must choose among its definitions according to the context, not use the model
		 * itself.
		 */
		
		LinkedModel ret = new LinkedModel();
		
		/*
		 * TODO
		 */
		
		return ret;
	}

	public void setDescription(String s) {
		description = s;
	}

	public void addContingency(IModel m) {
		
		if (context == null)
			context = new ArrayList<IModel>();
		context.add(m);
	}
	
	/**
	 * Can be called once or more; models are passed after being configured with their
	 * clauses. If more than one model get here, they must be "disjoint" i.e. have mutually
	 * exclusive :when clauses with only one possible "default" one without.
	 */
	public void defModel(IModel model) {
		
		System.out.println("setting unconditional " + model);
		if (models == null) {
			models = new ArrayList<IModel>();
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
	
	public String toString() {
		return "[" + getObservable() + "->" + getCompatibleObservationType(null)+"]";
	}


	@Override
	public void applyClause(String keyword, Object argument) throws ThinklabException {
		throw new ThinklabInternalErrorException("internal error: a Model should only be configured through a proxy");
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
	public Polylist buildDefinition() throws ThinklabException {
		
		/*
		 * we should build the definition of the chosen def'd models, not on the defmodel
		 * result itself.
		 */
		throw new ThinklabInternalErrorException("internal error: buildDefinition should not be called on a Model");
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

}
