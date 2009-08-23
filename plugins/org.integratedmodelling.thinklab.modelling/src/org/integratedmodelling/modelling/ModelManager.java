package org.integratedmodelling.modelling;

import java.util.Hashtable;

import org.integratedmodelling.thinklab.ConceptVisitor;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * A singleton (access from ModellingPlugin) that catalogs models and provides search functions for models
 * of specific observable concepts.
 * 
 * @author Ferdinando Villa
 *
 */
public class ModelManager {

	public Hashtable<IConcept, Model> models = new Hashtable<IConcept, Model>();
	
	public static ModelManager get() {
		return ModellingPlugin.get().getModelManager();
	}
	
	/*
	 * called by the defmodel macro. 
	 */
	public Model registerModel(Model model) {
	
		IConcept obs = model.getObservable();
		if (models.containsKey(obs))
			ModellingPlugin.get().logger().
				warn("model for observable " + obs + 
					 " has been defined already: previous definition overridden");
		
		models.put(obs, model);
		ModellingPlugin.get().logger().info("model " + model + " registered");
		return model;
	}
	
	public Model retrieveModel(IConcept concept) {
		
        class Matcher implements ConceptVisitor.ConceptMatcher {

            Hashtable<IConcept, Model> coll;
            Model ret = null;
            
            public Matcher(Hashtable<IConcept,Model> c) {
                coll = c;
            }
            
            public boolean match(IConcept c) {
                ret = coll.get(c);
                return(ret != null);	
            }    
        }
        
        Matcher matcher = new Matcher(models);
        IConcept cms = ConceptVisitor.findMatchUpwards(matcher, concept);

        return cms == null ? null : matcher.ret;
	}
	
	public Model requireModel(IConcept concept) throws ThinklabResourceNotFoundException {
		
		Model ret = retrieveModel(concept);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("no model found for observable " + concept);
		return ret;
	}

}
