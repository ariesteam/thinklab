package org.integratedmodelling.corescience.interfaces.cmodel;

import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * A validating conceptual model declares its intention to validate anything coming from a 
 * datasource (after any mediation and aggregation) before it becomes part of the state. Validation
 * can be purely a check (raising an exception if invalid) or attempt to convert the values to
 * appropriate ones. It is typically implemented to define boundaries of validity or other criteria.
 * 
 * @author Ferdinando Villa
 *
 */
public interface ValidatingConceptualModel {

	
	/**
	 * Called every time a datasource has returned a value for the passed context state. Gives the
	 * CM a chance to further validate it, e.g. checking boundaries, type etc. The returned IValue
	 * will be used as the state for that context state.
	 * 
	 * @param value
	 * @param contextState
	 * @return
	 * @throws ThinklabValidationException
	 */
	public abstract IValue validateValue(IValue value, IObservationContextState contextState) 
		throws ThinklabValidationException;

	/**
	 * Can be called by the datasource when it does not know what to do with a byte value extracted. Should
	 * return an IValue that we know how to do something with, or raise a validation exception.
	 *  
	 * @param b
	 * @return
	 */
	public abstract IValue validateData(byte b) throws ThinklabValidationException;

	/**
	 * Can be called by the datasource when it does not know what to do with a int value extracted. Should
	 * return an IValue that we know how to do something with, or raise a validation exception.
	 *  
	 * @param b
	 * @return
	 */
	public abstract IValue validateData(int b) throws ThinklabValidationException;

	/**
	 * Can be called by the datasource when it does not know what to do with a long value extracted. Should
	 * return an IValue that we know how to do something with, or raise a validation exception.
	 *  
	 * @param b
	 * @return
	 */
	public abstract IValue validateData(long b) throws ThinklabValidationException;

	
	/**
	 * Can be called by the datasource when it does not know what to do with a float value extracted. Should
	 * return an IValue that we know how to do something with, or raise a validation exception.
	 *  
	 * @param b
	 * @return
	 */
	public abstract IValue validateData(float b) throws ThinklabValidationException;

	/**
	 * Can be called by the datasource when it does not know what to do with a double value extracted. Should
	 * return an IValue that we know how to do something with, or raise a validation exception.
	 *  
	 * @param b
	 * @return
	 */
	public abstract IValue validateData(double b) throws ThinklabValidationException;


	

}
