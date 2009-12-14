package org.integratedmodelling.corescience.interfaces.internal;

import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public interface IndirectObservation extends IObservation {
	
	/**
	 * Return the concept that implements storage for one grain of this
	 * conceptual model. 
	 * 
	 * If the observation is not supposed to have any state, the return value should
	 * be KnowledgeManager.Nothing(). 
	 * 
	 * @return a concept. Do not return null.
	 * @model
	 */
	public abstract IConcept getStateType();
	
	/**
	 * Return an appropriate accessor to obtain data from the datasource.
	 * @return
	 */
	public abstract IStateAccessor getAccessor();
	
	/**
	 * Indirect observations are responsible for creating the state of the result observation
	 * when they are contextualized.
	 * 
	 * @param size
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IState createState(int size) throws ThinklabException;
	
}
