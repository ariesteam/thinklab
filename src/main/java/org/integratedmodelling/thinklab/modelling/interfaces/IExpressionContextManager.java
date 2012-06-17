package org.integratedmodelling.thinklab.modelling.interfaces;

import java.util.Map;

import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IObserver;

/**
 * Wraps an interpreter/compiler for an expression language that is aware of the local names in a model
 * being computed and is capable of providing access to previously computed states using syntax conventions
 * that work conveniently with the language implemented.
 * 
 * A class implementing this should be defined per supported expression language, and an instance of the
 * class corresponding to the language set for the namespace should be given to any IComputingAccessor
 * that needs it.
 * 
 * @author Ferd
 *
 */
public interface IExpressionContextManager {

	/**
	 * Set the observer and the context that expressions run through this object should be able
	 * to access.
	 * 
	 * @param observer
	 * @param context
	 */
	public void setContext(IObserver observer, IContext context);
	
	/**
	 * Compile the expression, returning a handle. For a simple interpreter, just
	 * return the expression itself and run it as is in run().
	 * 
	 * @param expression
	 * @return
	 */
	public Object compile(IExpression expression);
	
	/**
	 * Run an expression that was previously compiled by compile() through its return value.
	 * 
	 * @param expressionHandle
	 * @param parameters
	 * @return
	 */
	public Object run(Object expressionHandle, Map<String, Object> parameters);
	
}
