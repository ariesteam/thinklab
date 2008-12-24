package org.integratedmodelling.thinklab.operators;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.extensions.InstanceImplementationConstructor;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.IOperator;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.NumberValue;
import org.integratedmodelling.thinklab.value.TextValue;

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
	public void initialize(IInstance i) throws ThinklabException {
		this.id = i.getLocalName();
	}

	@Override
	public void validate(IInstance i) throws ThinklabException {
	}
	
	public static void installBasicOperators() {
		
		KnowledgeManager.get().registerInstanceConstructor(
					"thinklab-core:Multiplication", 
					new InstanceImplementationConstructor() {

						@Override
						public IInstanceImplementation construct(
								IInstance instance) throws ThinklabException {
							// TODO Auto-generated method stub
							return new Mult();
						}
					});
		
		KnowledgeManager.get().registerInstanceConstructor(
				"thinklab-core:Division", 
				new InstanceImplementationConstructor() {

					@Override
					public IInstanceImplementation construct(
							IInstance instance) throws ThinklabException {
						// TODO Auto-generated method stub
						return new Div();
					}
				});
		
		KnowledgeManager.get().registerInstanceConstructor(
				"thinklab-core:Summation", 
				new InstanceImplementationConstructor() {

					@Override
					public IInstanceImplementation construct(
							IInstance instance) throws ThinklabException {
						// TODO Auto-generated method stub
						return new Plus();
					}
				});
		
		KnowledgeManager.get().registerInstanceConstructor(
				"thinklab-core:Subtraction", 
				new InstanceImplementationConstructor() {

					@Override
					public IInstanceImplementation construct(
							IInstance instance) throws ThinklabException {
						// TODO Auto-generated method stub
						return new Minus();
					}
				});
		
		KnowledgeManager.get().registerInstanceConstructor(
				"thinklab-core:Equality", 
				new InstanceImplementationConstructor() {

					@Override
					public IInstanceImplementation construct(
							IInstance instance) throws ThinklabException {
						// TODO Auto-generated method stub
						return new Eq();
					}
				});
		
		// TODO the others
	
	}

}
