package org.integratedmodelling.corescience.interfaces.data;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * Conceptual models are asked to create objects of this type to compile access to states into
 * contextualizers. Conceptual models that don't have access to datasources are expected to use 
 * an accessor to implement mediation of their dependencies.
 * 
 * In case the observation depends on others, the accessor will be notified of the observables that it 
 * depends upon in the sequence known to the contextualizer, so that they can be matched to what is 
 * known to the CM (e.g. variable names in expressions) and they will be retrievable at contextualization 
 * using their notification index.
 * 
 * @author Ferdinando
 *
 */
public interface IStateAccessor {

	/**
	 * Notifies that the observation whose state we must provide access to depends on another
	 * observation of this observable, and that the observable will be available as the given
	 * type. Called in the same order that contextualization will use (e.g. to know where on 
	 * the stack parameters are found). 
	 * 
	 * Communicates whether the state of the passed observable is necessary to access the
	 * given state by returning a boolean.
	 * 
	 * If the obs doesn't know what to do with the observable, it should throw an exception here.
	 *  
	 * @throws ThinklabValidationException
	 * @return true if the observable's state really needs to be passed at contextualization. Used
	 * to optimize contextualization.
	 */
	public boolean notifyDependencyObservable(IConcept observable) throws ThinklabValidationException;

	/**
	 * If notifyDependencyObservable has returned true for this observable, a register will be allocated
	 * and communicated through a call to this one, so that its value can be accessed using the register
	 * number (as a literal of given state type) when the state we're providing access to is requested.
	 * 
	 * If there's anything wrong with the type, throw an exception, although this should not happen
	 * and the accessor should be prepared to deal with the type returned by the CM.
	 * 
	 * @param newRegister
	 * @param stateType
	 */
	public void notifyDependencyRegister(IConcept observable, int register, IConcept stateType) throws ThinklabValidationException;
	
	/**
	 * Compute or retrieve the value. The passed array contains values
	 * for the notified dependency observables, in the type notified
	 * previously.
	 * 
	 * @param registers
	 * @return
	 */
	public Object getValue(Object[] registers);

	/**
	 * returning true means that the value returned by getValue() does not change 
	 * and is known before any computation starts. Of course it also means
	 * that registers can safely be null in any call to getValue(). Compilers will
	 * inline the value and discard the accessor, exposing the value register so that
	 * compilation can be run again with different values.
	 * 
	 * @return
	 */
	public boolean isConstant();
	
	/**
	 * Returning true here means that the value exists before any context
	 * is computed, and can be retrieved using getValue(null). It does not
	 * mean that the value won't change when the context is passed.
	 */
	public boolean hasInitialState();
}
