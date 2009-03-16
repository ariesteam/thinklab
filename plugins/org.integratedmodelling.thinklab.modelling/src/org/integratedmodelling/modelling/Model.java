package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.Obs;
import org.integratedmodelling.corescience.contextualization.Compiler;
import org.integratedmodelling.modelling.interfaces.IModel;
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
import org.integratedmodelling.utils.Pair;
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

	IConcept subject = null;
	IModel unconditional = null;
	ArrayList<Pair<Polylist,IModel>> conditional = null;
	Collection<IModel> context = null;
	Collection<String> contextIds = null;
	IKBox contKbox = null;

	public IInstance run(IKBox kbox, ISession session) throws ThinklabException {
		
		IInstance ret = null;
		IInstance model = buildObservation(kbox, session);
		if (model != null) {
			ret = Compiler.contextualize(Obs.getObservation(model), session);
		}
		return ret;
	}
	
	public void setObservable(IConcept c) {
		subject = c;
		System.out.println("observable set to " + c);
	}
	
	public void addContingency(String s, IModel m) {
		
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
	 * Define rules to choose specific types to represent basic type in model
	 * 
	 * @param session
	 * @param basetype
	 * @param cond
	 */
	public void defrule(Polylist rule, IModel model) {
		
		if (conditional == null)
			conditional = new ArrayList<Pair<Polylist,IModel>>();
		
		conditional.add(new Pair<Polylist, IModel>(rule, model));
		
		System.out.println(subject + " seen as " + model + " iif " + rule);
	}

	
	public void setModel(IModel model) {
		System.out.println("setting unconditional " + model);
		unconditional = model;
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
		
		if (conditional != null) {
			
			/*
			 * build and contextualize the contingency structure
			 */
			
			/*
			 * infer an observation structure per contingent state
			 */


		} else if (unconditional != null) {
			ret = unconditional.buildObservation(kbox, session);
		}
		
		return ret;
	}

	@Override
	public IConcept getObservable() {
		return subject;
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		return CoreScience.Observation();
	}

	@Override
	public boolean isResolved() {
		// TODO Auto-generated method stub
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

}
