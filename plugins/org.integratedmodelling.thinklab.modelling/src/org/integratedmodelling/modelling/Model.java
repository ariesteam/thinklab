package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.modelling.exceptions.ThinklabModelException;
import org.integratedmodelling.modelling.observations.ObservationFactory;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

/**
 * The most high-level notion in Thinklab. A model defined only on semantic grounds, that selects
 * the way things are observed based on reasoning, and build a model that depends entirely (structurally
 * and functionally) on the context.
 * 
 * Docs to come.
 * 
 * @author Ferdinando Villa
 * @date Jan 25th, 2008.
 * 
 */
public class Model {

	IConcept subject = null;
	HashMap<String, IConcept> observables = new HashMap<String, IConcept>();
	
	
	public Model(IConcept id) {
		this.subject = id;
	}
	
	/*
	 * Build a main observation using all the observables that we depend on and can be found in
	 * the passed kbox, contextualize it, and
	 * add any further dependencies that come from the types referred to. Each context state of
	 * this observation will determine the model to be built.
	 */
	public IInstance buildModelContext(IKBox kbox, ISession session) throws ThinklabException {
	
		Polylist main = ObservationFactory.createIdentification(subject.toString());
		
		for (IConcept observable : observables.values()) {
			
			/*
			 * query observable until there is at least one or we encounter an abstract class;
			 * skip temporary concepts.
			 */
			
			//IInstance observation = 
		}
		
		return session.createObject(main);
	}
	
	/**
	 * Define a concept that will need to be observed when this model is built, and whose
	 * contextualized state will be referred to in the rules as the passed ID.
	 * 
	 * @param session
	 * @param baseType
	 * @param transformType
	 */
	public void observe(IConcept concept, String constraintID) {
		observables.put(constraintID, concept);
	}
	
	/**
	 * Create a ModeledObservable type in the given session, with conceptual model and 
	 * possibly observable dependencies, and return it. It will be used to contextualize known 
	 * observables into observations that can be used within this model.
	 * 
	 * @param session
	 * @param typename
	 * @param cmodel
	 * @param dependents
	 * @return
	 * @throws ThinklabException 
	 */
	public static IConcept deftype(ISession session, IConcept type, Polylist cmodel, Polylist dependents) throws ThinklabException {
				
		ArrayList<Object> ldef = new ArrayList<Object>();
		
		/* intersect the concept with ModeledObservable */
		ldef.add(type + "+observation:ModeledObservable");
		ldef.add(Polylist.list("observation:hasObservableModel", cmodel));
		
		if (dependents != null) {
			ldef.add(dependents.cons("observation:dependsOnObservable"));
		}
		
		return session.createConcept(Polylist.PolylistFromArrayList(ldef));
	}
	
	/**
	 * Define rules to choose specific types to represent basic type in model
	 * 
	 * @param session
	 * @param basetype
	 * @param cond
	 */
	public void defrule(ISession session, IConcept baseType, Polylist cond, IConcept targetType) {
	
	}
	
	public IInstance buildModel(IKBox kbox, ISession session) throws ThinklabException {
		
		IInstance world = buildModelContext(kbox, session);
		
		if (world == null) {
			throw new ThinklabModelException("model " + subject + " cannot be built because its context cannot be observed with the available information.");
		}
		
		return null;
	}
}
