package org.integratedmodelling.afl.library;

import java.util.Collection;

import org.integratedmodelling.afl.AFLLibrary;
import org.integratedmodelling.afl.Functor;
import org.integratedmodelling.afl.Interpreter;
import org.integratedmodelling.afl.StepListener;
import org.integratedmodelling.afl.exceptions.ThinklabAFLException;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.ListValue;
import org.integratedmodelling.utils.Polylist;

public class ListLib implements AFLLibrary {

	public class List implements Functor {

		@Override
		public IValue eval(ISession session,
				Collection<StepListener> listeners, IValue... args)
				throws ThinklabAFLException {

			Polylist pl = Polylist.PolylistFromArray(args);
			return new ListValue(pl);
		}
		
	}
	
	@Override
	public void installLibrary(Interpreter intp) {

		intp.registerFunctor("list", new List());

	}

}
