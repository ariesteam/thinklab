package org.integratedmodelling.corescience.context;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.ObservationFactory;
import org.integratedmodelling.corescience.compiler.Compiler;
import org.integratedmodelling.corescience.compiler.Contextualizer;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.interfaces.internal.TransformingObservation;
import org.integratedmodelling.corescience.listeners.IContextualizationListener;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;

public class ObservationContext implements IObservationContext {

	IObservation observation;
	Hashtable<IConcept, IExtent> extents = new Hashtable<IConcept, IExtent>();
	ArrayList<IConcept> order = new ArrayList<IConcept>();
	int totalSize = -1;
	int[] dimensionalities = null;
	boolean _initialized = false;
		
	ArrayList<ObservationContext> dependents = 
		new ArrayList<ObservationContext>();
	ArrayList<IDatasourceTransformation> transformations = 
		new ArrayList<IDatasourceTransformation>();
	
	// this stores the context of the original untransformed instance when the 
	// observation is a transformer, so it can be passed to transform()
	private ObservationContext originalContext;

	@Override
	public String toString() {
		return 
			"context(" + extents + "): " + 
			observation.getObservationInstance().getDirectType().getLocalName() + 
			"(" + observation.getObservableClass() + ")";
	}
	
	/*
	 * Create as a shallow copy of another context
	 * @param observationContext
	 */
	private ObservationContext(ObservationContext observationContext) {
		copy(observationContext);
	}

	/**
	 * Compute the context of the passed observation. If required, constrain it to match the
	 * passed context.
	 * 
	 * @param o an observation
	 * @param constraint another "upper level" contexts, or null
	 * @throws ThinklabException
	 */
	public ObservationContext(IObservation o, ObservationContext constraint) throws ThinklabException {
		
		this.observation = o;
		
		/*
		 * start with the constraining extents if any
		 */
		if (constraint != null)
			extents.putAll(constraint.extents);
		
		/*
		 * put in anything that our observation has
		 */
		for (Topology extent : observation.getTopologies()) {
			mergeExtent((Topology) extent);
		}

		/*
		 * merge with dependent contexts, constrained by ours
		 */
		for (IObservation dep : observation.getDependencies()) {
			
			ObservationContext depctx = new ObservationContext(dep, this);
			dependents.add(depctx);
			
			// merge in any further restriction coming from downstream
			mergeExtents(depctx);
		}

		/*
		 * compute multiplicities and extent order
		 */
		initialize();

		/*
		 * if we have a datasource, determine the necessary transformations to 
		 * move from our own extent to whatever was defined in the context.
		 */
		if (observation.getDataSource() != null) {
			for (Topology extent : observation.getTopologies()) {
				IExtent ext = extent.getExtent();
				IDatasourceTransformation transform = 
					ext.getDatasourceTransformation(
							observation.getObservableClass(),
							getExtent(extent.getObservableClass()));
				if (transform != null)
					transformations.add(transform);
			}	
		}

		/* 
		 * if we represent a transformer, we store our "original" context in a field and
		 * switch to what we will be after transformation.
		 */
		if (observation instanceof TransformingObservation) {
			switchTo(((TransformingObservation)observation).getTransformedContext(this));
		}
	
	}
	
	/*
	 * create a new context with our extents and dependents and set it into 
	 * originalContext; set our contents to the transformed context
	 */
	private void switchTo(IObservationContext transformedContext) {

		originalContext = new ObservationContext(this);
		copy((ObservationContext) transformedContext);
	}

	private void copy(ObservationContext ctx) {
		
		// shallow-copy everything should be OK
		this._initialized = ctx._initialized;
		this.dependents = ctx.dependents;
		this.dimensionalities = ctx.dimensionalities;
		this.extents = ctx.extents;
		this.observation = ctx.observation;
		this.order = ctx.order;
		this.totalSize = ctx.totalSize;
		this.transformations = ctx.transformations;
		if (!_initialized) {
			initialize();
		}
	}

	private void mergeExtents(ObservationContext depctx) throws ThinklabException {
		
		for (IConcept c : depctx.extents.keySet()) {
			
			IExtent myExtent  = extents.get(c);
			IExtent itsExtent = depctx.getExtent(c);
			
			if (myExtent == null) {
				
				/* just add the extent */
				extents.put(c, itsExtent);
			
			} else {

				/* ask CM to modify the current extent record in order to represent the
				   new one as well. */
				IExtent merged = itsExtent.merge(myExtent);
				extents.put(c, merged);
			}		
		}
	}

	private void mergeExtent(Topology extobs) throws ThinklabException {

		IConcept dimension = extobs.getObservableClass();
		
		IExtent myExtent  = extents.get(dimension);
		IExtent itsExtent = extobs.getExtent();
		
		if (myExtent == null) {
			
			/* just add the extent */
			extents.put(dimension, itsExtent);
		
		} else {

			/* ask CM to modify the current extent record in order to represent the
			   new one as well. */
			IExtent merged = itsExtent.merge(myExtent);
			extents.put(dimension, merged);
		}		
	}

	@Override
	public void dump(PrintStream printStream) {
		dumpNode(this, printStream, 0, true);
	}

	private static void dumpNode(ObservationContext ctx,
			PrintStream out, int i, boolean followtrans) {

		String s = MiscUtilities.createWhiteSpace(i, 0);

		if (followtrans && (ctx.observation instanceof TransformingObservation)) {
			dumpNode(ctx.originalContext, out, i, false);
			out.println(s + ">>> Transform to <<< ");
		}
		
		for (ObservationContext dep : ctx.dependents) {
			dumpNode(dep, out, i+0, true);
		}
		
		out.println(s + ctx.observation + ":");

		for (IConcept c : ctx.order) {
			out.println(s + "@" + ctx.extents.get(c));
		}

		for (IDatasourceTransformation t : ctx.transformations) {
			out.println(s + t);
		}
	}
		
	@Override
	public IConcept getDimension(IConcept concept) throws ThinklabException {

		IConcept ret = null;
		for (IConcept c : order) {
			if (c.is(concept)) {
				if (ret != null)
					throw new ThinklabValidationException(
							"ambiguous request: context contains more than one dimension of type " +
							concept);
				ret = c;
			}
		}
		
		return ret;
	}

	@Override
	public int[] getDimensionSizes() {
		return dimensionalities;
	}

	@Override
	public Collection<IConcept> getDimensions() {
		return order;
	}

	@Override
	public IExtent getExtent(IConcept c) {
		
		IConcept dim = null;
		try {
			dim = getDimension(c);
		} catch (ThinklabException e) {
		}
		
		return dim == null ? null : extents.get(dim);
	}

	@Override
	public int getMultiplicity() {
		return totalSize;
	}

	@Override
	public int getMultiplicity(IConcept dimension) throws ThinklabException {
		return 
			extents.get(getDimension(dimension)).getTotalGranularity();

	}

	@Override
	public int getNumberOfDimensions() {
		return extents.size();
	}

	@Override
	public IObservation getObservation() {
		return observation;
	}
	
	/*
	 * runs all transformations, sets transformed datasources into
	 * observations and transformed observations into contexts.
	 */
	private void processTransformations(ISession session,
			Collection<IContextualizationListener> listeners)
			throws ThinklabException {
		
		IObservation obs = observation;
		
		/*
		 * process dependents recursively
		 */
		for (ObservationContext c : dependents) {
			c.processTransformations(session, listeners);
		}
		
		/*
		 * 1. transform the datasource as required
		 */
		IDataSource<?> ds = obs.getDataSource();
		if (ds != null) {
			ds.preProcess(this);
			for (IDatasourceTransformation t : transformations) {
				if (t instanceof IDatasourceTransformation)
					ds = ds.transform((IDatasourceTransformation) t);	
			}
			ds.postProcess(this);
			((Observation)obs).setDatasource(ds);
		}
		
		/*
		 * 2. transform the observation if required
		 */
		if (obs instanceof TransformingObservation) {

			Contextualizer contextualizer = new Compiler().compile(originalContext);
			IInstance inst =  contextualizer.run(session);
			obs = ObservationFactory.getObservation(
					((TransformingObservation)obs).transform(inst, session, originalContext));

			/*
			 * set a possibly transformed observation into our slot
			 */
			this.observation = obs;

			// must reset the dependencies and get those of the transformed obs. No 
			// need to merge the contexts, as we have previously done that and the 
			// transformer should generate contextualized observations.
			dependents.clear();
			for (IObservation dep : obs.getDependencies()) {				
				ObservationContext depctx = new ObservationContext(dep, this);
				dependents.add(depctx);
			}
		}

	}
	
	@Override
	public IInstance run(ISession session,
			Collection<IContextualizationListener> listeners)
			throws ThinklabException {
		
		processTransformations(session, listeners);
		Contextualizer contextualizer = new Compiler().compile(this);		
		return contextualizer.run(session);
	
	}
	
	private void initialize()  {
		
		if (_initialized)
			return;
		
		sort();
		
		dimensionalities = new int[extents.size()];
		totalSize = 1;
		
		int i = 0;
		for (IConcept s : order) {
									
			IExtent extent = extents.get(s);
			int gr = extent.getTotalGranularity();
			dimensionalities[i++] = gr;
			totalSize *= gr;
		}
		_initialized = true;
	}
	
	private void sort() {
		
		order.clear();
		
		for (IConcept ss : extents.keySet())
			order.add(ss);
		
		/* TODO sort. Is it fair to think that if two extent concepts have an ordering 
		 * relationship, they should know about each other? So that we can implement the
		 * ordering as a relationship between extent observation classes? */
	}
	
	/**
	 * Reconstruct the structure we represent with the extents we have and the given
	 * states. 
	 * 
	 * @param session
	 * @param allStates
	 * @return
	 * @throws ThinklabException 
	 */
	public IInstance buildObservation(ISession session,
			Map<IConcept, IDataSource<?>> states) throws ThinklabException {
		
		Polylist l = buildObservationList(states);
		return session.createObject(l);
	}

	private Polylist buildObservationList(Map<IConcept, IDataSource<?>> states) throws ThinklabException {
		
		ArrayList<Object> adl = null;

		if (observation instanceof IConceptualizable) {
			adl = ((IConceptualizable)observation).conceptualize().toArrayList();
		} else {
			adl = new ArrayList<Object>(); 
			adl.add(
				observation instanceof TransformingObservation ?
					((TransformingObservation)observation).getTransformedObservationClass() :
					observation.getObservationInstance().getDirectType());
			adl.add(Polylist.list(
						CoreScience.HAS_OBSERVABLE,
						observation.getObservable().toList(null)));
		}		
		
		for (IExtent ext : extents.values()) {
			adl.add(
				Polylist.list(CoreScience.HAS_EXTENT,
				ext.conceptualize()));
		}
		
		for (ObservationContext dep : dependents) {
			adl.add(
					Polylist.list(CoreScience.DEPENDS_ON,
					dep.buildObservationList(states)));
		}
		
		/* 
		 * add datasource 
		 * FIXME we should actually index observable INSTANCES, not 
		 * concepts.
		 */
		IDataSource<?> ds = states.get(observation.getObservableClass());
		if (ds != null) {
			adl.add(
				Polylist.list(
					CoreScience.HAS_DATASOURCE,
					((IState)ds).conceptualize()));
		}	
					
		return Polylist.PolylistFromArrayList(adl);
					
	}

	public Collection<ObservationContext> getDependentContexts() {
		return dependents;
	}
}
