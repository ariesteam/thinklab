package org.integratedmodelling.modelling.context;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IContextTransformation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.modelling.interfaces.IModelForm;
import org.integratedmodelling.modelling.model.Model;
import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.time.TimePlugin;

/**
 * This is what is created by a defcontext form. It does implement IContext but
 * has no observation associated, and serves as a factory to create observation
 * contexts for specific observations. The resulting contexts can have extents,
 * predefined states and transformations inserted automatically according to what
 * is specified in the creation form.
 * 
 * @author ferdinando.villa
 *
 */
public class Context implements IContext, IModelForm {
	
	ArrayList<Model>   models = new ArrayList<Model>();
	ArrayList<IExtent> extents = new ArrayList<IExtent>();
	ArrayList<IContextTransformation> transformations = new ArrayList<IContextTransformation>();
	
	private String description;
	private String id;
	private String namespace;
	private boolean isInvalid = false;

	public Context() {}

	public static IContext getContext(String location_id, int resolution) {
		Context ret = new Context();
		return ret;
	}
	
	public static IContext getContext(ShapeValue location, int resolution) throws ThinklabException {
		
		Context ret = new Context();
		ret.extents.add(new GridExtent(location, resolution));
		return ret;
	}
	
	public static IContext getContext(Collection<Topology> extents) throws ThinklabException {
		Context ret = new Context();
		for (Topology t : extents)
			ret.extents.add(t.getExtent());
		return ret;
	}
	
	public static IContext getContext(IExtent ... extents) {
		Context ret = new Context();
		for (IExtent t : extents)
			ret.extents.add(t);
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
		} else if (object instanceof IContextTransformation) {
			if (((IContextTransformation)object).isNull())
				this.isInvalid = true;
			transformations.add((IContextTransformation)object);
		}
	}
	
	public String getDescription() {
		return this.description;
	}
	
	
	@Override
	public String getId() {
		return this.id;
	}
	
	@Override
	public String getNamespace() {
		return this.namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
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
		
		if (isInvalid)
			throw new ThinklabInappropriateOperationException(
					"context " + getName() + " is invalid and cannot be used");
	
		
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
		
		if (transformations.size() > 0)
			ret.setTransformations(transformations);
		
		return ret;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static IExtent getSpace(IContext context) {
		return context.getExtent(Geospace.get().SubdividedSpaceObservable());
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static IExtent getTime(IContext context) {
		return context.getExtent(TimePlugin.get().TimeObservable());
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
	
	@Override
	public IExtent getSpace() {
		return getExtent(Geospace.get().SubdividedSpaceObservable());
	}
	
	@Override
	public IExtent getTime() {
		return getExtent(TimePlugin.get().TimeObservable());
	}


	@Override
	public IExtent getExtent(IConcept observable) {
		for (IExtent e : extents)
			if (e.getObservableClass().is(observable))
				return e;
		return null;
	}

	@Override
	public boolean intersects(IContext context) throws ThinklabException {
		
		for (IExtent e : extents) {
			IExtent o = context.getExtent(e.getObservableClass());
			if (e != null && !e.intersects(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public IState getState(IConcept observable) {
		// the abstract context isn't linked to an observation and does not hold states.
		return null;
	}

	@Override
	public Collection<IState> getStates() {
		return new ArrayList<IState>();
	}

	@Override
	public IContext cloneExtents() throws ThinklabException {
		return getObservationContext(null);
	}

	public void setName(String name) {
		String[] x = name.split("/");
		this.namespace = x[0];
		this.id = x[1];
	}
	
	@Override
	public String getName() {
		return namespace + "/" + id;
	}

	@Override
	public boolean equals(Object obj) {
		return 
			obj instanceof Context ? 
				getName().equals(((IModelForm)obj).getName()) : false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int getMultiplicity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMultiplicity(IConcept concept) throws ThinklabException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IContext collapse(IConcept dimension) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCovered(int index) {
		throw new ThinklabRuntimeException("isCovered() called on Context");
	}
	
}
