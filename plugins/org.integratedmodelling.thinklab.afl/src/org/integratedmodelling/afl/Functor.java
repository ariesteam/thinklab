package org.integratedmodelling.afl;

import java.util.Collection;

import org.integratedmodelling.afl.exceptions.ThinklabAFLException;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;

public interface Functor {

	public abstract IValue eval(Interpreter interpreter, ISession session, Collection<StepListener> listeners, IValue ... args) throws ThinklabAFLException;
	
}
