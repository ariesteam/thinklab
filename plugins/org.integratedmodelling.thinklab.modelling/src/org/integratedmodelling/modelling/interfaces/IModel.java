package org.integratedmodelling.modelling.interfaces;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

/**
 * The most high-level notion in Thinklab. Essentially a blueprint to build an
 * observation of a concept given a kbox and a session. It is nothing but a set
 * of axioms, and it should be serializable to an appropriately restricted
 * observation class; it is here represented as a Java class for practical
 * reasons (of efficiency, storage and cleanup of unneeded axioms); make it a
 * IConceptualizable to implement the behavior if needed, it's likely to be
 * unneeded overhead for now.
 * 
 * The Java side is usable as is but the whole model definition machinery is
 * meant to be used from Clojure, which allows a beautiful and compact syntax for
 * model specification. See the examples/ folder in the plugin directory.
 * 
 * More docs will come or I'm not a real academic...
 * 
 * @author Ferdinando Villa
 * @date Jan 25th, 2008.
 * 
 */
public interface IModel {

	/**
	 * As a default behavior, build an observation reflecting the structure we
	 * represent. If we are a "leaf" node, lookup observations of our concept 
	 * in the kbox. 
	 * 
	 * 
	 * @param kbox
	 * @param session
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IInstance buildObservation(IKBox kbox, ISession session)
			throws ThinklabException;

	
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
	 * Called when parsing the specs if a :state clause is present
	 * 
	 * @param object
	 * @throws ThinklabValidationException
	 */
	public abstract void setState(Object object) throws ThinklabValidationException;
	
}