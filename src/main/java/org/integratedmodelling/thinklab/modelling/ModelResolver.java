package org.integratedmodelling.thinklab.modelling;

import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.modelling.lang.Model;

/**
 * A resolver for models in a namespace. Will apply heuristics and configured rules to determine
 * the best model to observe a given observable. Such configuration is (potentially) namespace- and
 * project- specific, with global defaults.
 * 
 * Criteria for final score as of this revision:
 * 
 * 1. user-defined quality
 * 2. community-defined "hits" (choices when user is choosing) -- unimplemented
 * 3. whether resolved or not (data or model)
 * 4. fine-grainedness re: context
 * 5. coverage in context (static)
 * 6. procedural detail
 * 7. timeliness/age or distance from period median if time not in context.
 * 8. date of last ranking/update
 * 
 * @author Ferd
 *
 */
public class ModelResolver {

	INamespace _namespace;
	
	public ModelResolver(INamespace namespace) {
		_namespace = namespace;
	}
	
	/**
	 * Get the best model for the semantic object passed. If the passed object
	 * is a model, just return it, so we can use it also to "resolve" when
	 * the user has specified a precise model. Otherwise we look for what
	 * observes it.
	 * 
	 * @param toResolve
	 * @return
	 */
	public IModel resolve(ISemanticObject<?> toResolve, IContext context) {
		
		if (toResolve instanceof Model) {
			return (Model)toResolve;
		}
		
		return null;
	}

	
}
