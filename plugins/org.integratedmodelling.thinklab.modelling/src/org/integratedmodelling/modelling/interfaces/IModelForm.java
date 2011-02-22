package org.integratedmodelling.modelling.interfaces;

import java.util.Set;

import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * Tag interface for any object that can be stored in the model map. Currently that
 * means agents, models, contexts and scenarios. Hopefully it will stop at this, meaning
 * the whole thing makes sense.
 * 
 * @author Ferdinando
 *
 */
public interface IModelForm {

	
	/**
	 * Models must have an ID
	 * @return
	 */
	public abstract String getId();
	
	
	/**
	 * Models must have a namespace
	 * @return
	 */
	public abstract String getNamespace();	
	
	/**
	 * This must return getNamespace() + "/" + getId()
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * Return the set of all concepts observed in this model object
	 * @return
	 */
	public Set<IConcept> getObservables();
}
