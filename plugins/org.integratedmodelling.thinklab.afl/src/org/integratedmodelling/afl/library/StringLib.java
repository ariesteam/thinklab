package org.integratedmodelling.afl.library;

import java.util.Collection;

import org.integratedmodelling.afl.AFLLibrary;
import org.integratedmodelling.afl.Functor;
import org.integratedmodelling.afl.Interpreter;
import org.integratedmodelling.afl.StepListener;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.value.ListValue;
import org.integratedmodelling.thinklab.value.NumberValue;

public class StringLib implements AFLLibrary {

	class Length implements Functor {

		@Override
		public IValue eval(Interpreter interpreter,
				ISession session, Collection<StepListener> listeners, IValue... args)
				throws ThinklabException {

			int len = 0;
			
			if (args[0] instanceof ListValue)
				len = ((ListValue)args[0]).getList().length();
			else 
				len = args[0].toString().length();

			return new NumberValue(len);
		}
		
	}

	@Override
	public void installLibrary(Interpreter intp) {
		intp.registerFunctor("len", new Length());
	}
	
}
