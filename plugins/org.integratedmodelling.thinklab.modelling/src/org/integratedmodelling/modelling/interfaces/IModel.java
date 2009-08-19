package org.integratedmodelling.modelling.interfaces;

import java.util.Map;

import org.integratedmodelling.modelling.ModelResult;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.query.IConformance;
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
	 * Return one realization of the model on the passed kbox. It should support calling many times, each
	 * time returning a different observation structure in a lazy fashion until there are no more to be
	 * found. At that point it should return null.
	 * 
	 * @param kbox a kbox to use to resolve any unresolved observables in the model.
	 * @param session to hold any instances created.
	 * @param conformancePolicies if not null, an IntelligentTable of conformance policies to be used to
	 * 	match observables to those stored in the kbox. If null is passed, it will use the default conformance
	 *  policy, which matches classes and classification properties exactly and ignores literals.
	 *  
	 * @param extentQuery an optional query to specify further extents to restrict the kbox with. Used to
	 * 		  avoid explosion when matching on large kboxes. Of course a pre-constrained kbox can also be
	 *        used.
	 * @return one realization of the model over the kbox, or null if there aren't any.
	 * @throws ThinklabException
	 */
	public abstract ModelResult realize(IKBox kbox, ISession session, Map<IConcept, IConformance> conformancePolicies, Restriction extentQuery) 
		throws ThinklabException;

	/**
	 * Create the base list definition for the resulting observation, to which 
	 * the main buildObservation() in Model will add mediated models, dependencies
	 * and transformers.
	 * 
	 * @return
	 * @throws ThinklabException
	 */
	public Polylist buildDefinition() throws ThinklabException;

	/**
	 * Merge the definition returned by buildObservation with all dependencies.
	 * @param kbox
	 * @return
	 * @throws ThinklabException
	 */
	public abstract Polylist buildObservation(IKBox kbox) throws ThinklabException;
	
}