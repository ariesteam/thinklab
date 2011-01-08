package org.integratedmodelling.modelling.interfaces;

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
}
