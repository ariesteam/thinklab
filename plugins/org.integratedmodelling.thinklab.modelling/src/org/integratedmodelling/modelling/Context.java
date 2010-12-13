package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.thinklab.owlapi.Session;

public class Context implements IContext {
	
	ArrayList<Model>   models = new ArrayList<Model>();
	ArrayList<IExtent> extents = new ArrayList<IExtent>();
	
	private String description;
	private String id;

	public Context() {}

	public static IContext getContext(String location_id, int resolution) {
		Context ret = new Context();
		return ret;
	}
	
	public static IContext getContext(ShapeValue location, int resolution) {
		Context ret = new Context();
		return ret;
	}
	
	public static IContext getContext(Collection<Topology> extents) {
		Context ret = new Context();
		return ret;
	}
	
	public static IContext getContext(IExtent ... extents) {
		Context ret = new Context();
		return ret;
	}
	
	public void setDescription(String s) {
		this.description = s;
	}
	
	public void setId(String s) {
		this.id = s;
	}
	
	public void add(Object object, Object modifiers) {
		if (object instanceof Model) {
			this.models.add((Model) object);
		} else if (object instanceof IExtent) {
			extents.add((IExtent)object);
		}
	}
	
	public String getDescription() {
		return this.description;
	}
	
	
	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * Create an observation context using our existing extent configuration for the passed
	 * observation.
	 * 
	 * @param o
	 * @return
	 * @throws ThinklabException 
	 */
	public IObservationContext getObservationContext(IObservation o) throws ThinklabException {
		
		ObservationContext cns = new ObservationContext(extents);
		ObservationContext ret = new ObservationContext(o, cns);
		
		/*
		 * if we have any models, run them all with the same constraining extent, and
		 * merge the states.
		 */
		for (Model model : models) {
			IObservationContext mr = 
				ModelFactory.get().eval(model, KBoxManager.get(), new Session(), ret);
			if (mr != null)
				ret.mergeStates(mr);
		}
		
		return ret;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static IExtent getSpace(IObservationContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static IExtent getTime(IObservationContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param concept
	 * @param context
	 * @return
	 */
	public static IState getState(IConcept concept, IObservationContext context) {
		return context.getState(concept);
	}

	@Override
	public Collection<IExtent> getExtents() {
		return extents;
	}


}
