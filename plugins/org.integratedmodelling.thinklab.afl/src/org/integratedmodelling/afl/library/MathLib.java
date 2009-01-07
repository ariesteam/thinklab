package org.integratedmodelling.afl.library;

import java.util.Collection;

import org.integratedmodelling.afl.AFLLibrary;
import org.integratedmodelling.afl.Functor;
import org.integratedmodelling.afl.Interpreter;
import org.integratedmodelling.afl.StepListener;
import org.integratedmodelling.afl.exceptions.ThinklabAFLException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.value.NumberValue;

public class MathLib implements AFLLibrary {

	class Sum implements Functor {

		@Override
		public IValue eval(Interpreter interpreter, ISession session,
				Collection<StepListener>  model, IValue ... arguments) throws ThinklabException {
			
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
		public IValue eval(Interpreter interpreter, ISession session,
				Collection<StepListener>  model, IValue... arguments) throws ThinklabException {
			
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
		public IValue eval(Interpreter interpreter, ISession session,
				Collection<StepListener> model, IValue... arguments) throws ThinklabException {
			
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
		public IValue eval(Interpreter interpreter, ISession session,
				Collection<StepListener> model, IValue... arguments) throws ThinklabException {
			
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
		public IValue eval(Interpreter interpreter, ISession session,
				Collection<StepListener> model, IValue... arguments) throws ThinklabException {
			
			double val = 0.0;
			
			try {
				val = arguments[0].asNumber().asDouble() % arguments[1].asNumber().asDouble();
			} catch (ThinklabValueConversionException e) {
				throw new ThinklabAFLException("non-numeric operators");
			}
			
			return new NumberValue(val);
		}
	}
	
	class Sin implements Functor {

		@Override
		public IValue eval(Interpreter interpreter, ISession session,
				Collection<StepListener> model, IValue... arguments) throws ThinklabException {
			
			double val = 0.0;
			
			try {
				val = Math.sin(arguments[0].asNumber().asDouble());
			} catch (ThinklabValueConversionException e) {
				throw new ThinklabAFLException("non-numeric operators");
			}
			return new NumberValue(val);
		}
	}
	
	class Cos implements Functor {

		@Override
		public IValue eval(Interpreter interpreter, ISession session,
				Collection<StepListener> model, IValue... arguments) throws ThinklabException {
			
			double val = 0.0;
			
			try {
				val = Math.cos(arguments[0].asNumber().asDouble());
			} catch (ThinklabValueConversionException e) {
				throw new ThinklabAFLException("non-numeric operators");
			}
			return new NumberValue(val);
		}
	}
	class Tan implements Functor {

		@Override
		public IValue eval(Interpreter interpreter, ISession session,
				Collection<StepListener> model, IValue... arguments) throws ThinklabException {
			
			double val = 0.0;
			
			try {
				val = Math.tan(arguments[0].asNumber().asDouble());
			} catch (ThinklabValueConversionException e) {
				throw new ThinklabAFLException("non-numeric operators");
			}
			return new NumberValue(val);
		}
	}
	class Arcsin implements Functor {

		@Override
		public IValue eval(Interpreter interpreter, ISession session,
				Collection<StepListener> model, IValue... arguments) throws ThinklabException {
			
			double val = 0.0;
			
			try {
				val = Math.asin(arguments[0].asNumber().asDouble());
			} catch (ThinklabValueConversionException e) {
				throw new ThinklabAFLException("non-numeric operators");
			}
			return new NumberValue(val);
		}
	}
	class Arccos implements Functor {

		@Override
		public IValue eval(Interpreter interpreter, ISession session,
				Collection<StepListener> model, IValue... arguments) throws ThinklabException {
			
			double val = 0.0;
			
			try {
				val = Math.acos(arguments[0].asNumber().asDouble());
			} catch (ThinklabValueConversionException e) {
				throw new ThinklabAFLException("non-numeric operators");
			}
			return new NumberValue(val);
		}
	}
	class Arctan implements Functor {

		@Override
		public IValue eval(Interpreter interpreter, ISession session,
				Collection<StepListener> model, IValue... arguments) throws ThinklabException {
			
			double val = 0.0;
			
			try {
				val = Math.atan(arguments[0].asNumber().asDouble());
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
		intp.registerFunctor("sin", new Sin());
		intp.registerFunctor("cos", new Cos());
		intp.registerFunctor("tan", new Tan());
		intp.registerFunctor("asin", new Arcsin());
		intp.registerFunctor("acos", new Arccos());
		intp.registerFunctor("atan", new Arctan());
	}
	
	

}
