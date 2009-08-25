package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.modelling.exceptions.ThinklabModelException;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.IntelligentMap;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
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
		 */
		contingencyModelBuilt = true;
	}
	
	
	@Override
	public ModelResult observeInternal(IKBox kbox, ISession session, IntelligentMap<IConformance> cp, ArrayList<IObservation> extents)  throws ThinklabException {
	
		ModelResult ret = null;
		ArrayList<Polylist> cmodels = new ArrayList<Polylist>();
		
		if (models.size() == 1)
			ret = ((DefaultAbstractModel)(models.get(0))).observeInternal(kbox, session, cp, extents);
		else {

			ret = new ModelResult(this, kbox, session);
			
			for (ContingencyIterator it = getContingencyIterator(session, kbox); it.hasNext(); ) {

				Contingency cn = it.next();
				IModel cmod = chooseModel(models, cn, kbox);
				if (cmod == null) {
					throw new ThinklabModelException(
							"cannot choose a model formulation for " +
							observable +
							" in context " +
							cn +
							": no matching submodel");
				}
				
				ModelResult contingentRes = ((DefaultAbstractModel)cmod).observeInternal(kbox, session, cp, extents);
				
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
	
	@Override
	public String toString() {
		return "[" + getObservable() + "->" + getCompatibleObservationType(null)+"]";
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

}
