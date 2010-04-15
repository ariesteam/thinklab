package org.integratedmodelling.modelling.interfaces;

import org.integratedmodelling.modelling.Scenario;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

/**
 * The most high-level notion in Thinklab. Essentially a query that returns 
 * observations of a concept given a kbox and a session. It is nothing but a set
 * of axioms, and it should be serializable to an appropriately restricted
 * observation class; it is here represented as a Java class for practical
 * reasons (of efficiency, storage and cleanup of unneeded axioms); make it a
 * IConceptualizable to implement the behavior if needed, it's likely to be
 * unneeded overhead for now.
 * 
 * The Java side is usable as is but the whole model definition machinery is
 * meant to be used from Clojure, which provides an elegant and compact syntax for
 * model specification. See the examples/ folder in the plugin directory.
 * 
 * More docs will come or I'm not a real academic...
 * 
 * @author Ferdinando Villa
 * @date Jan 25th, 2008.
 * 
 */
public interface IModel extends IConceptualizable {

	/**
	 * Return the base observable concept
	 * 
	 * @return
	 */
	public abstract IConcept getObservable();
	
	/**
	 * Return the type of observation that we can deal with if we need to
	 * be paired to data from a kbox. If we're compatible with more than
	 * one type, we pass the session so we can build a union of types.
	 * 
	 * @return
	 */
	public abstract IConcept getCompatibleObservationType(ISession session);
	
	/**
	 * This one should return true if the model contains enough information
	 * to become a contextualizable observation without accessing an external kbox. 
	 * If this returns false, buildObservation will consult the kbox for matching
	 * observations, and use them as appropriate to resolve dependencies.
	 * 
	 * @return
	 */
	public abstract boolean isResolved();

	/**
	 * Called by defmodel with any keyword parameters added after the model. Will set
	 * properties such as :as, :when etc.
	 * 
	 * @param keyword
	 * @param argument
	 * @throws ThinklabException 
	 */
	public abstract void applyClause(String keyword, Object argument) throws ThinklabException;
	
	/**
	 * When models are postfixed with modifier clauses, we want to 
	 * @return
	 */
	public abstract IModel getConfigurableClone();

	/**
	 * Create the base list definition for the resulting observation
	 * 
	 * @return
	 * @throws ThinklabException
	 */
	public Polylist buildDefinition(IKBox kbox, ISession session) throws ThinklabException;

	/**
	 * Models must have an ID
	 * @return
	 */
	public abstract String getId();

	/**
	 * Observing a model over a kbox is a query that produces zero or more observations of 
	 * that model. Contextualizing each observation is equivalent to running the model.
	 * 
	 * @param kbox a kbox to lookup unresolved observations. May be null, but make sure
	 *        that the model is fully resolved or exceptions will be generated.
	 * @param session 
	 * @param cp if not null, contains the definition of conformity that will be used to 
	 * 	      lookup each observable for the observations in the kbox. If null, the default
	 * 		  conformance will be used, matching all object and classification properties but
	 *        no literals.
	 * @param extents TODO
	 * @return A model result object, which works like any query result and will return 
	 *         a "lazy" sequence of observation objects (generated only on demand). The 
	 *         observations will need to be contextualized by the user.
	 *         
	 * @throws ThinklabException
	 */
	public IQueryResult observe(IKBox kbox, ISession session, Object ... arguments) throws ThinklabException;

	/**
	 * Train the model to match any specified output observation (in the :observed
	 * clause, if any). Not all models may be trainable. Returns a new trained model
	 * that has learned to reproduce the models observed on the passed kbox.
	 * 
	 * @param kbox
	 * @param session
	 * @param params
	 * @return
	 * @throws ThinklabException
	 */
	IModel train(IKBox kbox, ISession session, Object ... params) throws ThinklabException;

	/**
	 * A scenario is a model modifier, containing alternative models for given observables.
	 * Applying the scenario simply substitutes any models of the same observables with those
	 * in the scenario, going as deep as needed in the dependency chain.
	 * 
	 * @param scenario
	 * @return
	 * @throws ThinklabException
	 */
	public IModel applyScenario(Scenario scenario) throws ThinklabException;
}