package org.integratedmodelling.thinklab.modelling;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.lang.model.Namespace;
import org.integratedmodelling.thinklab.api.modelling.INamespace;

/**
 * Internal interface used by interpreters to create model objects.
 * 
 * @author Ferd
 */
public interface IModelFactory {

	/**
	 * The main entry point to translate the result of parsing any language into usable model
	 * objects. The namespace passes is a bean defined in the API. It may already exist if we are
	 * allowing snippets of code from user input or embedded code, so be aware of that. All rules
	 * about namespace duplication have already been dealt with in the parser, so the input is
	 * OK.
	 * 
	 * This will be called for each incremental change in the namespaces and must result in the
	 * appropriate updates to the model map.
	 * 
	 * @param namespace
	 * @return
	 */
	public abstract INamespace processNamespace(Namespace namespace) throws ThinklabException;
	
}
