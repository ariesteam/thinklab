package org.integratedmodelling.modelling.interfaces;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

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
	
}