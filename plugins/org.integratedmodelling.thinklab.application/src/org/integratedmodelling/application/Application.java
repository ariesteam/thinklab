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

	public ApplicationModel run() throws ThinklabException {
		return run(null);
	}
	
	public ApplicationModel run(ApplicationModel parentState) throws ThinklabException {

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
		
		/*
		 * interpret workflow list: select next ApplicationStep, run it, push the results
		 * on the model stack.
		 */
		state.push (runStep(state, workflow));
		
		/*
		 * finalize
		 */
		if (parentState != null)
			parentState.push(state);
		
		return state;
	}
	
	
	public Object runStep(ApplicationModel state, Polylist list) throws ThinklabException {
		
		ArrayList<Object> o = list.toArrayList();
		
		ArrayList<Object> args = new ArrayList<Object>();
		KeyValueMap opts = new KeyValueMap();
		Object ret = null;
		
		String stepname = o.get(0).toString();
		
		/* eval arguments - dumb for now; things like conditionals should ensure that
		 * selective evaluation takes place. */
		for (int i = 1; i < o.size(); i++) {
			
			if (o.get(i) instanceof Polylist) {
				args.add(runStep(state, (Polylist)o.get(i)));
			}
		}
		
		/* eval step */
		
		ApplicationStep step = decodeStepName(stepname);
		
		if (step != null) {
			ret = step.run(state, opts, args.toArray(new Object[args.size()]));
		}
		
		state.push(ret);
		
		return ret;
	}
	
	protected ApplicationStep decodeStepName(String stepName) throws ThinklabValidationException {

		ArrayList<String> candidates = new ArrayList<String>();
		ApplicationStep ret = null;
		
		if (stepName.contains(".")) {
			candidates.add(stepName);
		} else {
			
			String sName = CamelCase.toLowerCamelCase(stepName, '-');	
			for (String s : stepPackages) {
				candidates.add(s + "." + sName);
			}
		}
		
		for (String clz : candidates) {
			
			Class<?> cl = null;
			
			try {
				cl = Class.forName(clz);
			} catch (ClassNotFoundException e) {
			}
			
			if (cl != null) {
				try {
					ret = (ApplicationStep) cl.newInstance();
				} catch (Exception e) {
				}
			}
			
			if (ret != null)
				return ret;
		}

		throw new ThinklabValidationException(
				"application: cannot resolve step " + stepName + " to a step class");
	}


	public Object run_debug() throws ThinklabException {

		/*
		 * TODO add debug features
		 */
		return run();
	}
}
