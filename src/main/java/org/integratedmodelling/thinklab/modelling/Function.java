package org.integratedmodelling.thinklab.modelling;

import org.integratedmodelling.thinklab.api.knowledge.IExpression;

/**
 * Captures name and arguments of a function
 * 
 * @author Ferd
 *
 */
public class Function {

	String functionId;
	String[] argumentNames;
	Object[] argumentValues;
	
	/**
	 * if this is not set, the function doesn't correspond
	 * to a known one and warnings or errors, according to
	 * the context, may be issued.
	 */
	IExpression function;
	
	public String getFunctionId() {
		return functionId;
	}
	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}
	public String[] getArgumentNames() {
		return argumentNames;
	}
	public void setArgumentNames(String[] argumentNames) {
		this.argumentNames = argumentNames;
	}
	public Object[] getArgumentValues() {
		return argumentValues;
	}
	public void setArgumentValues(Object[] argumentValues) {
		this.argumentValues = argumentValues;
	}
	
	public void setFunction(IExpression e) {
		function = e;
	}
	
	public IExpression getFunction() {
		return function;
	}
}
