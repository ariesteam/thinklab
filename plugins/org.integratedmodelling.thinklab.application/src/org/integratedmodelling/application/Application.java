package org.integratedmodelling.application;

import java.util.ArrayList;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.CamelCase;
import org.integratedmodelling.utils.KeyValueMap;
import org.integratedmodelling.utils.Polylist;

public class Application {

	private String id = null;
	private String applicationModelClass = null;
	private Polylist workflow = null;
	private String[] stepPackages = null;
	
	Application(String id,
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
	public ApplicationInterpreter getInterpreter() throws ThinklabValidationException {
		
		ApplicationInterpreter ret = new ApplicationInterpreter();
		
		Class<?> cl = null; 
		ApplicationModel state = null;
		
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
			state = (ApplicationModel) cl.newInstance();
		} catch (Exception e) {
			throw new ThinklabValidationException(
					"error creating model instance of " + cl + " class");
		}
		
		
		return ret;
	}

	

}
