package org.integratedmodelling.thinklab.application;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.ITask;
import org.integratedmodelling.thinklab.interfaces.IValue;

public class Application {

	ApplicationDescriptor appdesc = null;
	
	public Application(String id) {
		
		/*
		 * load settings from declared apps
		 */
	}
	
	public IValue run() throws ThinklabException {
		
		IValue ret = null;
		ITask task = null;
		ISession session = null;
		
		/*
		 * Create main task 
		 */
		
		/*
		 * Create session as specified
		 */
		
		/*
		 * Run task and return 
		 */
		task.run(session);
		
		/*
		 * Find return value
		 */
		
		return ret;
	}
}
