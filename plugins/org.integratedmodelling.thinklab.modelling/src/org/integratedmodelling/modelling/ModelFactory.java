package org.integratedmodelling.modelling;

import java.util.Hashtable;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.ConceptVisitor;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.time.literals.TemporalExtentValue;

/**
 * A singleton (access from ModellingPlugin) that catalogs models and provides search functions for models
 * of specific observable concepts.
 * 
 * @author Ferdinando Villa
 *
 */
public class ModelFactory {

	public Hashtable<IConcept, Model> models = new Hashtable<IConcept, Model>();
	public Hashtable<String, Model> modelsById = new Hashtable<String, Model>();
	
	public static ModelFactory get() {
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
	
	/**
	 * Return a shape (usually a multipolygon) that describes the coverage of
	 * a particular model in a given kbox. It will be the union of the spatial
	 * contexts of all the realized models found in that kbox. Computing this
	 * may be a big deal, and if there are contingencies or complex transformers
	 * may involve computing models, so be careful.
	 * 
	 * @param model
	 * @return a shape or null if the model is not realizable or has no
	 * spatially explicit realizations in the kbox. If the shape is not 
	 * null, it will be in normalized lat/lon coordinates.
	 * 
	 * @throws ThinklabException 
	 */
	public ShapeValue getSpatialCoverage(Model model, IKBox kbox, ISession session) throws ThinklabException {

		ShapeValue ret = null;
			
		IQueryResult res = model.observe(kbox, session, (Object[])null);
		
		for (int i = 0; i < res.getResultCount(); i++) {
			
			IValue rr = res.getResult(i, session);
			IObservation obs = 
				ObservationFactory.getObservation(rr.asObjectReference().getObject());
			
			ObservationContext ctx = new ObservationContext(obs);

			if (!ctx.isEmpty()) {
				ArealExtent ext = (ArealExtent)ctx.getExtent(Geospace.get().SpaceObservable());
				ShapeValue sh = (ShapeValue) ext.getFullExtentValue();
				if (ret == null)
					ret = sh;
				else 
					ret = ret.union(sh);
			}
		}
		
		/*
		 * just in case
		 */
		if (ret != null) 
			ret.transform(Geospace.get().getStraightGeoCRS());
		
		return ret;
	}
	
	/**
	 * Return a temporal extent that describes the time coverage of
	 * a particular model in a given kbox. It will be the union of the temporal
	 * contexts of all the realized models found in that kbox. Same disclaimers
	 * as getSpatialCoverage.
	 * 
	 * TODO unimplemented
	 * 
	 * @param model
	 * @return
	 */
	public TemporalExtentValue getTemporalCoverage(Model model, IKBox kbox, ISession session) {
		return null;
	}

}
