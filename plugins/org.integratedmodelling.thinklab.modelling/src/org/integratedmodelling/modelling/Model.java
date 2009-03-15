package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

/**
 * The most high-level notion in Thinklab. Essentially a conceptual blueprint to build 
 * observations of a concept given a kbox and a session. It is nothing but a set of 
 * axioms, and it should be serializable to a class in DL. It is here represented 
 * as a Java class for practical (efficiency, storage, cleanup of unneeded axioms) but
 * it has a toConcept() methods that should return a properly restricted class, not
 * implemented because likely useless for now.
 * 
 *  The Java side is usable as is but the whole model definition machinery is meant to be 
 *  used from Clojure, which makes a beautiful and compact syntax for model specification. See
 *  the examples/ folder in the plugin directory.
 * 
 * More docs will come.
 * 
 * @author Ferdinando Villa
 * @date Jan 25th, 2008.
 * 
 */
public class Model implements IModel {

	IConcept subject = null;
	Collection<IModel> dependencies = null;
	Collection<IModel> context = null;
	Collection<String> contextIds = null;
	
	/*
	 * Build a main observation using all the observables that we depend on and can be found in
	 * the passed kbox, contextualize it, and
	 * add any further dependencies that come from the types referred to. Each context state of
	 * this observation will determine the model to be built.
	 */
	public IInstance buildModelContext(Collection<Object> contextSpecs, IKBox kbox, ISession session) throws ThinklabException {
		return null;
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.modelling.IModel#define(org.integratedmodelling.thinklab.interfaces.knowledge.IConcept, java.util.Collection, java.util.Collection)
	 */
	public void define(IConcept concept, Collection<Object> context, Collection<IModel> dependents) throws ThinklabException {

		this.subject = concept;
		
		for (Iterator<Object> it = context.iterator(); it.hasNext(); ) {
			
			if (this.context == null) {
				this.context = new ArrayList<IModel>();
				this.contextIds = new ArrayList<String>();
			}

			this.contextIds.add(it.next().toString());
			this.context.add((IModel) it.next());
		}
		
		this.dependencies = dependents;
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
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.modelling.IModel#buildObservation(org.integratedmodelling.thinklab.interfaces.storage.IKBox, org.integratedmodelling.thinklab.interfaces.applications.ISession)
	 */
	public IInstance buildObservation(IKBox kbox, ISession session) throws ThinklabException {
		
		
		return null;
	}


	@Override
	public IConcept getObservable() {
		return subject;
	}



	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		// TODO Auto-generated method stub
		return null;
	}

}
