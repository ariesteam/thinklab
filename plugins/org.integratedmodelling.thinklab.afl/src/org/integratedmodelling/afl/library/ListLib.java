package org.integratedmodelling.afl.library;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.afl.AFLLibrary;
import org.integratedmodelling.afl.FunctionValue;
import org.integratedmodelling.afl.Functor;
import org.integratedmodelling.afl.Interpreter;
import org.integratedmodelling.afl.StepListener;
import org.integratedmodelling.afl.exceptions.ThinklabAFLException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.ListValue;
import org.integratedmodelling.utils.Polylist;

public class ListLib implements AFLLibrary {

	public class List implements Functor {

		@Override
		public IValue eval(Interpreter interpreter,
				ISession session, Collection<StepListener> listeners, IValue... args)
				throws ThinklabAFLException {

			Polylist pl = Polylist.PolylistFromArray(args);
			return new ListValue(pl);
		}
		
	}
	
	public class Map implements Functor {

		@Override
		public IValue eval(Interpreter interpreter,
				ISession session, Collection<StepListener> listeners, IValue... args)
				throws ThinklabAFLException {
			
			/*
			 * TODO all error checking
			 */
			FunctionValue func = (FunctionValue) args[0];
			ListValue list = (ListValue) args[1];
			
			ArrayList<Object> results = new ArrayList<Object>();
			
			IValue[] par = new IValue[1];
			for (Object o : list.getList().array()) {

				par[0] = (IValue)o;
				try {
					results.add(func.eval(interpreter, par));
				} catch (ThinklabException e) {
					throw new ThinklabAFLException(e);
				}
			}

			return new ListValue(Polylist.PolylistFromArrayList(results));
			
		}
		
	}
	
	public class Car implements Functor {

		@Override
		public IValue eval(Interpreter interpreter,
				ISession session, Collection<StepListener> listeners, IValue... args)
				throws ThinklabAFLException {
			return (IValue)(((ListValue)args[0]).getList().first());
		}
	}

	public class Cdr implements Functor {

		@Override
		public IValue eval(Interpreter interpreter,
				ISession session, Collection<StepListener> listeners, IValue... args)
				throws ThinklabAFLException {
			
			return new ListValue(((ListValue)args[0]).getList().rest());
		}
	}

	@Override
	public void installLibrary(Interpreter intp) {

		intp.registerFunctor("list", new List());
		intp.registerFunctor("map", new Map());
		intp.registerFunctor("car", new Car());
		intp.registerFunctor("cdr", new Cdr());

	}

}
