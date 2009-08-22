package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Hashtable;

import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.ConceptVisitor;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

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

	/**
	 * The main operation in modelling is to query an observation kbox, which may be null if we're certain
	 * that the model is resolved. This produces a ModelResult object which we can use to retrieve
	 * compliant result observations, whose contextualization is the result of running each model.
	 * 
	 * Use a pre-constrained kbox to add context restrictions such as where and when.
	 * 
	 * @param kbox
	 * @return
	 */
	public static ModelResult query(Model model, IKBox kbox, ISession session) {
		
		return null;
	}
	
}
