package org.integratedmodelling.application;

import java.util.ArrayList;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.Polylist;

public class Application {

	private ApplicationModel state = null;
	private ApplicationModel parentState = null;
	private Polylist workflow = null;
	
	/**
	 * When the empty constructor is used, the application model stack will be created at
	 * initialization.
	 */
	public Application() {
		
	}
	
	/**
	 * Use this one when we want to chain applications, passing an existing state. If we
	 * pass a "parent" state, the whole state after running this application will be
	 * pushed on the stack of the parent.
	 * 
	 * @param state
	 */
	public Application(ApplicationModel state) {
		parentState = state;
	}
	
	/**
	 * Parse the workflow from a list, create the model stack unless we have one already,
	 * and 
	 * @param workflow
	 * @param applicationModelClass
	 * @param stepPackage
	 */
	public void initialize(Polylist workflow,
						   String applicationModelClass, 
						   String stepPackage) throws ThinklabException {
		
		this.workflow = workflow;
		
		Class<?> cl = null; 
		
		try {
			cl = Class.forName(applicationModelClass);
		} catch (ClassNotFoundException e) {
			throw new ThinklabValidationException(
					"model class " + applicationModelClass + " not found");
		}
		
		/*
		 * create state from class
		 */

	}
	
	
	public void run() throws ThinklabException {
		
		/*
		 * interpret workflow list: select next ApplicationStep, run it, push the results
		 * on the model stack.
		 */
		state.push (runStep(workflow));
		
		/*
		 * finalize
		 */
		if (parentState != null)
			parentState.push(state);
	}
	
	
	public Object runStep(Polylist list) {
		
		ArrayList<Object> o = list.toArrayList();
		
		String stepname = o.get(0).toString();
		
		/* eval arguments - dumb for now; things like conditionals should ensure that
		 * selective evaluation takes place. */
		for (int i = 1; i < o.size(); i++) {
			
			if (o.get(i) instanceof Polylist) {
				state.push(runStep((Polylist)o.get(i)));
			}
		}
		
		/* eval step */
		
		ApplicationStep step = null;
		Object ret = null;
		
		
		
		return ret;
		
	}
}
