package org.integratedmodelling.thinklab.extensions;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * The interface that an interpreter needs to implement in order to interface with 
 * Thinklab. Mostly needs to guarantee transparency between the language's type system
 * and semantic IValues. Also, it should expose the current session if any is passed, and
 * should honor the passed console streams.
 *
 * @author Ferdinando
 *
 */
public interface Interpreter {

	/**
	 * 
	 * @param session
	 */
	public abstract void setSession(ISession session);
	
	/**
	 * 
	 * @param input
	 */
	public abstract void setInput(InputStream input);

	/**
	 * 
	 * @param input
	 */
	public abstract void setOutput(OutputStream input);
	
	/**
	 * 
	 * @param input
	 */
	public abstract void setError(OutputStream input);
	
	/**
	 * Generic eval code, supposed to deal with the passed object according to what it is.
	 * It should be prepared to eval URLs and Files by executing the program contained in
	 * them, and anything that's likely to be a string containing code as a parseable 
	 * program. Specific implementations can add support for other classes (e.g. compiled
	 * ASM etc).
	 * 
	 * @param code
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IValue eval(Object code) throws ThinklabException;
	
	/**
	 * Like eval but with some externally supplied context, passed as a map of varname,object pairs.
	 * 
	 * @param code
	 * @param args
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IValue eval(Object code, HashMap<String,Object> args) throws ThinklabException;

	/**
	 * This one is different from eval because the bindings must remain in effect 
	 * globally, so if the language doesn't have shared memory or similar mechanisms,
	 * it must store the bindings and load them at every new interpreter created.
	 * 
	 * @param source
	 * @throws ThinklabException
	 */
	public abstract void loadBindings(URL source, ClassLoader cloader) throws ThinklabException;

	/**
	 * EXPERIMENTAL
	 * Automatically define a binding for the given ITask class, discovered at initialization.
	 * 
	 * @param taskClass
	 * @throws ThinklabException 
	 */
	public abstract void defineTask(Class<?> taskClass) throws ThinklabException;
	

}
