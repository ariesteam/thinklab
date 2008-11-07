package org.integratedmodelling.afl.library;

import java.util.Collection;

import org.integratedmodelling.afl.AFLLibrary;
import org.integratedmodelling.afl.Functor;
import org.integratedmodelling.afl.Interpreter;
import org.integratedmodelling.afl.StepListener;
import org.integratedmodelling.afl.exceptions.ThinklabAFLException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.NumberValue;

public class MathLib implements AFLLibrary {

	class Sum implements Functor {

		@Override
		public IValue eval(ISession session, Collection<StepListener>  model,
				IValue ... arguments) throws ThinklabAFLException {
			
			double sum = 0.0;
			
			for (IValue arg : arguments) {
				
				try {
					sum += arg.asNumber().asDouble();
				} catch (ThinklabValueConversionException e) {
					throw new ThinklabAFLException("non-numeric operators");
				}
			}
			
			return new NumberValue(sum);
		}
		
	}
	
	class Multiplication implements Functor {

		@Override
		public IValue eval(ISession session, Collection<StepListener>  model,
				IValue... arguments) throws ThinklabAFLException {
			
			double sum = 1.0;
			
			for (IValue arg : arguments) {
				
				try {
					sum *= arg.asNumber().asDouble();
				} catch (ThinklabValueConversionException e) {
					throw new ThinklabAFLException("non-numeric operators");
				}
			}
			return new NumberValue(sum);
		}
		
	}
	
	class Subtraction implements Functor {

		@Override
		public IValue eval(ISession session, Collection<StepListener> model,
				IValue... arguments) throws ThinklabAFLException {
			
			double val = 0.0;
			
			try {
				val = arguments[0].asNumber().asDouble() -	arguments[1].asNumber().asDouble();
			} catch (ThinklabValueConversionException e) {
				throw new ThinklabAFLException("non-numeric operators");
			}
			return new NumberValue(val);
		}
		
	}
	
	class Division implements Functor {

		@Override
		public IValue eval(ISession session, Collection<StepListener> model,
				IValue... arguments) throws ThinklabAFLException {
			
			double val = 0.0;
			
			try {
				val = arguments[0].asNumber().asDouble() /	arguments[1].asNumber().asDouble();
			} catch (ThinklabValueConversionException e) {
				throw new ThinklabAFLException("non-numeric operators");
			}
			
			return new NumberValue(val);
		}
	}

	class Rest implements Functor {

		@Override
		public IValue eval(ISession session, Collection<StepListener> model,
				IValue... arguments) throws ThinklabAFLException {
			
			double val = 0.0;
			
			try {
				val = arguments[0].asNumber().asDouble() % arguments[1].asNumber().asDouble();
			} catch (ThinklabValueConversionException e) {
				throw new ThinklabAFLException("non-numeric operators");
			}
			
			return new NumberValue(val);
		}
	}
	
	@Override
	public void installLibrary(Interpreter intp) {
		
		intp.registerFunctor("+", new Sum());
		intp.registerFunctor("-", new Subtraction());
		intp.registerFunctor("*", new Multiplication());
		intp.registerFunctor("/", new Division());
		intp.registerFunctor("%", new Rest());
	}
	
	

}
