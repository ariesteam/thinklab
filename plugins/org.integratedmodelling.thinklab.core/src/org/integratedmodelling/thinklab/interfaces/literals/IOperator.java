package org.integratedmodelling.thinklab.interfaces.literals;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;

/**
 * An IOperator is a specialized instance implementation capable of returning a value given a set
 * of IValue arguments. Should be also capable of validating the arguments, although this is not 
 * enforced currently.
 * 
 * Operators are declared in ontologies and the correspondent instance objects can be used in 
 * constraints.
 * 
 * FIXME this whole thing is unnecessarily complicated and should be simplified.
 * 
 * @author Ferdinando
 *
 */
public interface IOperator extends IInstanceImplementation {

	public static final String NO_OP = "nop";
	public static final String SUM = "+";
	public static final String MUL = "*";
	public static final String SUB = "-";
	public static final String DIV = "/";
	public static final String MOD = "%";
	
	public static final String AVG = "mean";
	public static final String STD = "std";
	public static final String CV  = "cv";
	public static final String VAR = "var";

	
	public abstract IValue eval(Object ... arg) throws ThinklabException;
	public abstract String getOperatorId();

}
