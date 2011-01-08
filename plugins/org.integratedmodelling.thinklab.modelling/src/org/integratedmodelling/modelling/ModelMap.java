package org.integratedmodelling.modelling;

import org.integratedmodelling.modelling.interfaces.IModelForm;
import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * The holder of the treemap to the whole modeling landscape. The map links together 
 * namespaces, models, contexts, storyline and agent objects with their source code and can 
 * reconstruct any of them or all after any change. Model content from external sources can
 * be integrated using the model map and synchronized to files so it can be edited by any
 * application that can read the clojure specs.
 * 
 * The map also maintains indexes of all nodes by ID and can answer dependency questions 
 * about any model node.
 * 
 * @author Ferdinando
 *
 */
public class ModelMap {

	/*
	 * generalized dependency specializes to:
	 * 	  FORM_DEPENDENCY (of model, context, agent, scenario to other form)
	 *    NAMESPACE_DEPENDENCY (of model, context, agent)
	 *    SOURCE_DEPENDENCY (of model, context, agent, scenario to source chunk in file)
	 *    FILE_DEPENDENCY (of source chunk and namespace to file)
	 */
	
	public void sync() throws ThinklabException {
		
	}
	
	public void addForm(
			IModelForm form, 
			String namespace, String source, String resource,
			IModelForm ... dependsOn) 
		throws ThinklabException {
		
	}
	
	public void addNamespace(String namespace, String source) throws ThinklabException {
		
	}
}
