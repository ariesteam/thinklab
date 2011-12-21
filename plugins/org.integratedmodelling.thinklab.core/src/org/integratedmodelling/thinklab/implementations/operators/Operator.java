/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.implementations.operators;

import java.util.HashMap;

import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.lang.IOperator;
import org.integratedmodelling.thinklab.literals.NumberValue;
import org.integratedmodelling.thinklab.literals.TextValue;

/**
 * Serves as an extensible factory for operators, provides a way to
 * register their translation in various languages. Should also provide
 * an extensible polymorphic eval (also TODO) so we can translate
 * "foreign" language in models or other bindings to their Java calls.
 * 
 * @author Ferd
 *
 */
public abstract class Operator implements IOperator {

	String id = null;
	
	static HashMap<String,String> _translations = new HashMap<String, String>();
	
	public static final IOperator SUM = new Plus();
	public static final IOperator MUL = new Mult();
	public static final IOperator SUB = new Minus();
	public static final IOperator DIV = new Div();
	public static final IOperator MOD = new Mod();
	
	// TODO complete
//	public static final IOperator AVG = "mean";
//	public static final IOperator STD = "std";
//	public static final IOperator CV  = "cv";
//	public static final IOperator VAR = "var";
//	
	public static final IOperator INTERSECTION = new Intersection();
	public static final IOperator UNION = new Union();
	public static final IOperator INTERSECTS = new Intersects();

//	public static final IOperator AND = "and";
//	public static final IOperator OR = "or";
//	public static final IOperator NOT = "not";
//	public static final IOperator XOR = "xor";
	
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
	public String toString() {
		return getName();
	}

	/**
	 * Register translation in given language for given operator
	 * 
	 * @param op
	 * @param language
	 * @param translation
	 */
	public static void registerTranslation(IOperator op, String language, String translation) {
		_translations.put(op.getName() + "|" + language, translation);
	}
	
	@Override
	public final String getName(String language) {
		return _translations.get(getName() + "|" + language);
	}
}
