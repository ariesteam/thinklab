package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.Obs;
import org.integratedmodelling.corescience.contextualization.Compiler;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
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
import org.integratedmodelling.utils.Polylist;

/**
 * The "default" model class is the one that reflects the defmodel form. It has
 * both a contingency structure and a dependency structure, which are both resolved
 * to observations using a kbox and a session; then the contingency structure is 
 * used to contextualize the dependencies, and the result is a set of contextualizable
 * models wrapped in one observation.
 * 
 * The run() method will build and contextualize the model, returning a new 
 * observation of the models' contextualized states.
 * 
 * @author Ferdinando Villa
 * @date Jan 25th, 2008.
 * 
 */
public class Model implements IModel {

	ArrayList<IModel> models = null;
	Collection<IModel> context = null;
	Collection<String> contextIds = null;
	IKBox contKbox = null;
	String description = null;
	
	public class Dependency {
		IModel model;
		Polylist clause = null;
		String id = null;
		Object parameterValue = null;
	}
	
	IConcept observable = null;
	Polylist observableSpecs = null;
	Object state = null;
	
	public void setObservable(Object observableOrModel) throws ThinklabException {
		
		System.out.println("model: got observable " + observableOrModel.getClass() + ": " + observableOrModel);
		
		if (observableOrModel instanceof IConcept) {
			this.observable = (IConcept) observableOrModel;
		} else if (observableOrModel instanceof Polylist) {
			this.observableSpecs = (Polylist)observableOrModel;
			this.observable = KnowledgeManager.get().requireConcept(this.observableSpecs.first().toString());
		} else {
			this.observable = KnowledgeManager.get().requireConcept(observableOrModel.toString());
		}
	}
	
	
	/**
	 * Run the model in the given session, using the passed kboxes and topology if
	 * any.
	 * 
	 * @param session where we create the whole thing
	 * @param params may contain one kbox (used for both context and deps), two
	 * 	kboxes (used for context and deps respectively) and/or a topology (observation
	 *  context) used to define the overall topology for the context.
	 *   
	 * @return
	 * @throws ThinklabException
	 */
	public IInstance run(ISession session, Collection<Object> params) throws ThinklabException {
		
		IKBox contKbox = null;
		IKBox depsKbox = null;
		IObservationContext topology = null;
		
		if (params != null)
			for (Object o : params) {
				if (o instanceof IKBox) {
					if (contKbox == null)
						contKbox = (IKBox) o;
					else 
						depsKbox = (IKBox) o;
				} else if (o instanceof IObservationContext) {
					topology = (IObservationContext) o;
				}
			}
		
		IInstance ret = null;
		// FIXME must use both kboxes and the topology
		IInstance model = buildObservation(contKbox, session);
		if (model != null) {
			ret = Compiler.contextualize(Obs.getObservation(model), session);
		}
		return ret;
	}
	
	public void setDescription(String s) {
		description = s;
	}

	public void addContingency(IModel m, Collection<Object> auxInfo) {
		
		if (context == null)
			context = new ArrayList<IModel>();
		context.add(m);
	}
	
	/*
	 * Build a main observation using all the observables that we depend on and
	 * can be found in the passed kbox, contextualize it, and add any further
	 * dependencies that come from the types referred to. Each context state of
	 * this observation will determine the model to be built.
	 */
	private IInstance buildContingencyStructure(IKBox kbox, ISession session) throws ThinklabException {
		return null;
	}

	/**
	 * Can be called once or more; models passed may have a list of aux info
	 * attached, which contains keywords and their values. If their values
	 * are lists, they will be polylists. KW can be :as, :if, :otherwise, :parameter
	 * etc.
	 */
	public void defModel(IModel model, Collection<?> aux) {
		System.out.println("setting unconditional " + model);
		if (models == null) {
			models = new ArrayList<IModel>();
		}
		models.add(model);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.integratedmodelling.modelling.IModel#buildObservation(org.
	 * integratedmodelling.thinklab.interfaces.storage.IKBox,
	 * org.integratedmodelling.thinklab.interfaces.applications.ISession)
	 */
	public IInstance buildObservation(IKBox kbox, ISession session)
			throws ThinklabException {

		IInstance ret = null;
		
		if (models.size() == 1) {
			ret = models.get(0).buildObservation(kbox, session);
		}
		
		return ret;
	}

	@Override
	public IConcept getObservable() {
		return observable;
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		return CoreScience.Observation();
	}

	@Override
	public boolean isResolved() {
		return true;
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

	@Override
	public void setState(Object object) throws ThinklabValidationException {
		// TODO Auto-generated method stub
		
	}

}
