package org.integratedmodelling.thinklab.modelling.model;

import org.integratedmodelling.lang.model.Namespace;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.INamespace;

/**
 * A class that translates the API beans output by the language parsers into
 * actual namespaces and model objects.
 * 
 * @author Ferd
 *
 */
public class ModelAdapter {


	/**
	 * The main entry point.
	 * 
	 * @param namespace
	 * @return
	 */
	public INamespace createNamespace(Namespace namespace) {
		return null;
	}

	/**
	 * Assume the namespace has been incrementally modified and just parse the last
	 * model object defined in it. Used only in interactive sessions when statements
	 * are evaluated one by one.
	 * 
	 * @param evaluate
	 * @return
	 */
	public IModelObject createModelObject(Namespace evaluate) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
