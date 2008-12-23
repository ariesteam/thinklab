package org.integratedmodelling.thinklab.interfaces;

import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * An IOperator is a specialized instance implementation capable of returning a value given a set
 * of IValue arguments. Should be also capable of validating the arguments, although this is not 
 * enforced currently.
 * 
 * Operators are declared in ontologies and the correspondent instance objects can be used in 
 * constraints.
 * 
 * @author Ferdinando
 *
 */
public interface IOperator extends IInstanceImplementation {

	public abstract IValue eval(Object ... arg) throws ThinklabException;

	public abstract String getOperatorId();

}
