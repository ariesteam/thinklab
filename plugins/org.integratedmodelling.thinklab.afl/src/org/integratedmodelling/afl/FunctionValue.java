package org.integratedmodelling.afl;

import org.integratedmodelling.afl.exceptions.ThinklabAFLException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.ListValue;
import org.integratedmodelling.utils.Polylist;

/**
 * A lambda function. The value of this is the function body.
 * 
 * @author Ferdinando
 *
 */
public class FunctionValue extends ListValue {

	Polylist parameters = null;
	
	public FunctionValue(Polylist body, Polylist args) throws ThinklabValidationException {
		super(body);
		this.parameters = args;
	}
	
	public IValue eval(Interpreter rootInterpreter, IValue[] args) throws ThinklabException {
		
		Interpreter intp = new Interpreter(rootInterpreter);
		
		int nparms = parameters.length();
		
		if (args.length != nparms)
			throw new ThinklabAFLException("wrong number of parameters in function evaluation");
		
		if (parameters != null) {
			Object[] p = parameters.array();
			for (int i = 0; i < p.length; i++) {
				intp.bind(p[i].toString(), args[i]);
			}
		}
		return intp.eval(this.value);
	}
	
	
}
