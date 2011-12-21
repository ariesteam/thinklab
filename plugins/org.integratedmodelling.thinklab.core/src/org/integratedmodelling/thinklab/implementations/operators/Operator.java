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

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IOperator;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
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
	
	static double asDouble(Object arg) throws ThinklabValueConversionException {
		
		if (arg instanceof IValue)
			return ((IValue)arg).asNumber().asDouble();
		else if (arg instanceof Double)
			return (Double)arg;
		else if (arg instanceof Float)
			return (double)(Float)arg;		
		else if (arg instanceof Long)
			return (double)(Long)arg;		
		else if (arg instanceof Integer)
			return (double)(Integer)arg;	
		
		throw new ThinklabValueConversionException("operator value type mismatch");

	}
	
	static int asInt(Object arg) throws ThinklabValueConversionException {
		
		if (arg instanceof IValue)
			return ((IValue)arg).asNumber().asInteger();
		else if (arg instanceof Integer)
			return (Integer)arg;
		
		throw new ThinklabValueConversionException("operator value type mismatch");

	}
	
	static String asText(Object arg) throws ThinklabValueConversionException {
		
		if (arg instanceof IValue)
			return ((IValue)arg).asText().toString();
		else if (arg instanceof String)
			return (String)arg;

		throw new ThinklabValueConversionException("operator value type mismatch");
	}
	
	@Override
	public String getOperatorId() {
		return id;
	}
	
	@Override
	public String toString() {
		return getOperatorId();
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {
		this.id = i.getLocalName();
	}

	@Override
	public void validate(IInstance i) throws ThinklabException {
	}

}
