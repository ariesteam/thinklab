package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;

/**
 * Utility context to fill in with results of observing a model or observable. 
 * 
 * @author Ferd
 *
 */
public class CompiledContext extends Context {
	
	public CompiledContext(IContext context) throws ThinklabException {
		super((Context) context);
	}

	/**
	 * Run the passed model, return the result observation.
	 * 
	 * TODO use model listeners everywhere
	 * 
	 * @return
	 * @throws ThinklabException 
	 */
	public IObservation run(IModel model) throws ThinklabException {

//		ModelResolver resolver = new ModelResolver(model.getNamespace());
//		if (resolver.resolve((ISemanticObject<?>) model, this))
//			return resolver.run();
//		
		return null;
	}
	
	/**
	 * Observe the passed object using the default strategy in the thinklab
	 * namespace.
	 * 
	 * @param observable
	 * @return
	 * @throws ThinklabException
	 */
	public IObservation observe(ISemanticObject<?> observable) throws ThinklabException {

//		ModelResolver resolver = new ModelResolver(Thinklab.get().getNamespace("thinklab"));
//		if (resolver.resolve(observable, this))
//			return resolver.run();
		
		return null;
	}

}
