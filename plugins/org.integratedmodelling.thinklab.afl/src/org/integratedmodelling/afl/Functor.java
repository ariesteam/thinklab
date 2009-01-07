package org.integratedmodelling.afl;

import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

public interface Functor {

	public abstract IValue eval(
			Interpreter interpreter, ISession session, 
			Collection<StepListener> listeners,
			IValue ... args) throws ThinklabException;
	
}
