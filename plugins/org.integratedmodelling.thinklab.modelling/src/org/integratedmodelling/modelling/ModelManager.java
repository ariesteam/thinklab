package org.integratedmodelling.modelling;

import java.util.Hashtable;

import org.integratedmodelling.thinklab.ConceptVisitor;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedSemanticTypeException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
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
	public Hashtable<String, Model> modelsById = new Hashtable<String, Model>();
	
	public static ModelManager get() {
		return ModellingPlugin.get().getModelManager();
	}
	
	/*
	 * called by the defmodel macro. 
	 */
	public Model registerModel(Model model, String name) {
	
		IConcept obs = model.getObservable();
		if (models.containsKey(obs))
			ModellingPlugin.get().logger().
				warn("model for observable " + obs + 
					 " has been defined already: previous definition overridden");
		
		models.put(obs, model);
		modelsById.put(name, model);
		ModellingPlugin.get().logger().info("model " + model + " registered");
		return model;
	}
	
	public Model retrieveModel(String s) throws ThinklabException {
		
		if (s.contains(":")) {
		
			IConcept concept = KnowledgeManager.get().requireConcept(s);
			
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
		
		return modelsById.get(s);
        
	}
	
	public Model requireModel(String s) throws ThinklabException {
		
		Model ret = retrieveModel(s);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("no model found for " + s);
		return ret;
	}

}
