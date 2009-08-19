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
import org.integratedmodelling.thinklab.literals.ObjectReferenceValue;

/**
 * A singleton (access from ModellingPlugin) that catalogs models and provides search functions for models
 * of specific observable concepts.
 * 
 * @author Ferdinando Villa
 *
 */
public class ModelManager {

	public Hashtable<IConcept, IModel> models = new Hashtable<IConcept, IModel>();
	
	public static ModelManager get() {
		return ModellingPlugin.get().getModelManager();
	}
	
	/*
	 * called by the defmodel macro. 
	 */
	public IModel registerModel(IModel model) {
	
		IConcept obs = model.getObservable();
		if (models.containsKey(obs))
			ModellingPlugin.get().logger().
				warn("model for observable " + obs + 
					 " has been defined already: previous definition overridden");
		
		models.put(obs, model);
		ModellingPlugin.get().logger().info("model " + model + " registered");
		return model;
	}
	
	public IModel retrieveModel(IConcept concept) {
		
        class Matcher implements ConceptVisitor.ConceptMatcher {

            Hashtable<IConcept, IModel> coll;
            IModel ret = null;
            
            public Matcher(Hashtable<IConcept,IModel> c) {
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
	
	public IModel requireModel(IConcept concept) throws ThinklabResourceNotFoundException {
		IModel ret = retrieveModel(concept);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("no model found for observable " + concept);
		return ret;
	}

	public static IInstance runModel(IModel mod, IKBox kbox, IInstance context, ISession session) throws ThinklabException {

		if ( !(mod instanceof Model))
			throw new ThinklabValidationException("only models defined by a defmodel instruction can be resolved");
		
		Model model = (Model)mod;
		ArrayList<Object> params = new ArrayList<Object>();
		
		if (kbox != null)
			params.add(kbox);
		if (context != null) 
			params.add(context);
		
		return model.run(session, params);
	}
	
}
