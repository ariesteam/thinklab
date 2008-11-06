package org.integratedmodelling.afl;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.Polylist;

public class Application {

	private String id = null;
	private String applicationModelClass = null;
	private Polylist workflow = null;
	private String[] stepPackages = null;
	
	public Application(String id,
				Polylist workflow,
				String applicationModelClass, 
				String[] stepPackages) {
		
		this.id = id;
		this.workflow = workflow;
		this.applicationModelClass = applicationModelClass;
		this.stepPackages = stepPackages;
	}
	
	
	public String getId() {
		return id;
	}
	
	/**
	 * Get an interpreter, initialize it with a new model class and the application code, 
	 * prepare for running.
	 * 
	 * @return
	 * @throws ThinklabValidationException 
	 */
	public Interpreter getInterpreter() throws ThinklabValidationException {
		
		Interpreter ret = new Interpreter();
		
		Class<?> cl = null; 
		StepListener state = null;
		
		try {
			cl = Class.forName(applicationModelClass);
		} catch (ClassNotFoundException e) {
			throw new ThinklabValidationException(
					"model class " + applicationModelClass + " not found");
		}
		
		/*
		 * create state from class
		 */
		try {
			state = (StepListener) cl.newInstance();
		} catch (Exception e) {
			throw new ThinklabValidationException(
					"error creating model instance of " + cl + " class");
		}
		
		
		return ret;
	}

	

}
