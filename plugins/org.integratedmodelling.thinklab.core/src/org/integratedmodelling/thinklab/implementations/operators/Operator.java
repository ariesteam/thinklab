package org.integratedmodelling.thinklab.implementations.operators;

import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.lang.IOperator;
import org.integratedmodelling.thinklab.literals.NumberValue;
import org.integratedmodelling.thinklab.literals.TextValue;

public abstract class Operator implements IOperator {

	String id = null;
	
	static boolean isNumeric(Object o) {
		return 
			o instanceof NumberValue ||
			o instanceof Double ||
			o instanceof Integer ||
			o instanceof Long ||
			o instanceof Float;
	}

	static boolean isText(Object o) {
		return 
			o instanceof TextValue ||
			o instanceof String;
	}
	
	static double asDouble(Object arg) throws ThinklabValidationException {
		
		if (arg instanceof IValue)
			return ((IValue)arg).asDouble();
		else if (arg instanceof Double)
			return (Double)arg;
		else if (arg instanceof Float)
			return (double)(Float)arg;		
		else if (arg instanceof Long)
			return (double)(Long)arg;		
		else if (arg instanceof Integer)
			return (double)(Integer)arg;	
		
		throw new ThinklabValidationException("operator value type mismatch");

	}
	
	static int asInt(Object arg) throws ThinklabValidationException {
		
		if (arg instanceof IValue)
			return ((IValue)arg).asInteger();
		else if (arg instanceof Integer)
			return (Integer)arg;
		
		throw new ThinklabValidationException("operator value type mismatch");

	}
	
	static String asText(Object arg) throws ThinklabValidationException {
		
		if (arg instanceof IValue)
			return ((IValue)arg).asText();
		else if (arg instanceof String)
			return (String)arg;

		throw new ThinklabValidationException("operator value type mismatch");
	}
	
	@Override
	public String getOperatorId() {
		return id;
	}
	
	@Override
	public String toString() {
		return getOperatorId();
	}


}
