package org.integratedmodelling.thinklab.application;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.applications.ITask;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

public class Application {

	ApplicationDescriptor appdesc = null;
	
	public Application(String id) throws ThinklabResourceNotFoundException {
		
		/*
		 * load settings from declared apps
		 */
		appdesc = ApplicationManager.get().requireApplicationDescriptor(id);
	}
	
	public IValue run() throws ThinklabException {
		return run((ISession)null);
	}
	
	public IValue run(ISession session) throws ThinklabException {
		
		IValue ret = null;
		ITask task = null;
		
		/*
		 * Create main task 
		 */
		if (appdesc.taskClass != null) {
			
			/*
			 * make task
			 */
			try {
				task = 
					(ITask) Class.forName(appdesc.taskClass, true, Thinklab.getClassLoaderFor(appdesc)).newInstance();
			} catch (Exception e) {
				throw new ThinklabResourceNotFoundException(
						"application: " + 
						appdesc.id + 
						": error creating task of class " + 
						appdesc.taskClass);
			}
			
		} else {
			
			/* 
			 * must be a script
			 */
			task = new RunScript();
			((RunScript)task).setLanguage(appdesc.language);
			
			if (appdesc.code != null) 
				((RunScript)task).setCode(appdesc.code);
			else if (appdesc.script != null)
				((RunScript)task).setCode(appdesc.script);
		}	
		
		/*
		 * Create session as specified
		 */
		if (session == null) {

			try {
				session = 
					(ISession) Class.forName(
								appdesc.sessionClass, 
								true, 
								Thinklab.getClassLoaderFor(appdesc)).newInstance();
			} catch (Exception e) {
				throw new ThinklabResourceNotFoundException(
					"application: " + 
					appdesc.id + 
					": error creating session of class " + 
					appdesc.sessionClass);
			}
		}

		/*
		 * Run task and return 
		 */
		task.run(session);
		
		/*
		 * Find return value
		 */
		
		return ret;
	}
	
	/**
	 * Run the passed application and return its value.
	 * 
	 * @param application
	 * @return
	 * @throws ThinklabException 
	 */
	public static IValue run(String application) throws ThinklabException {
		
		return new Application(application).run();
		
	}
}
