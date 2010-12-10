package org.integratedmodelling.corescience.context;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.ObservationFactory;
import org.integratedmodelling.corescience.compiler.Compiler;
import org.integratedmodelling.corescience.compiler.Contextualizer;
import org.integratedmodelling.corescience.exceptions.ThinklabContextualizationException;
import org.integratedmodelling.corescience.implementations.observations.ContingencyMerger;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.ContextTransformingObservation;
import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.interfaces.internal.TransformingObservation;
import org.integratedmodelling.corescience.listeners.IContextualizationListener;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
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
	ArrayList<ObservationContext> contingents = 
		new ArrayList<ObservationContext>();
	ArrayList<IDatasourceTransformation> transformations = 
		new ArrayList<IDatasourceTransformation>();
	
	// this stores the context of the original untransformed instance when the 
	// observation is a transformer, so it can be passed to transform()
	private ObservationContext originalContext;
	private boolean isNull;

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
	
	public ObservationContext(IObservation o) throws ThinklabException {
		// TODO this should simply NOT pass a null context, but produce an overall unconstrained ctx.
		this(o, null);
	}

	public void initializeAll() throws ThinklabException {
		
		if (_initialized || isNull)
			return;

		for (ObservationContext o : dependents)
			o.initializeAll();
				
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
		if (observation instanceof ContextTransformingObservation) {
			switchTo(((ContextTransformingObservation)observation).getTransformedContext(this));
		}
		
		
		_initialized = true;
		
	}
	
	
	/**
	 * Compute the context of the passed observation. If required, constrain it to match the
	 * passed context.
	 * 
	 * @param o an observation
	 * @throws ThinklabException
	 */
	public ObservationContext(IObservation o, ObservationContext constraining) throws ThinklabException {
		
		
// --- START NEW
		HashMap<IConcept, IExtent> mods = defineObservationContext(o, constraining);
		
		if (mods.size() > 0) {
			CoreScience.get().logger().warn(
					"context of observation of " + 
					o.getObservableClass() +
					" was modified to suit dependencies");
		}
// --- END NEW
		
// -- START ORIGINAL
//		this.observation = o;
//
//		/*
//		 * put in anything that our observation has
//		 */
//		for (Topology extent : observation.getTopologies()) {
//			mergeExtent((Topology) extent);
//		}
//		
//		if (this.isNull)
//			return;
//		
//		if (constraining != null) {
//			constrainExtents(constraining);
//		}
//
//		if (this.isNull)
//			return;
//
//		
//		/*
//		 * contingent contexts will be constrained so partial overlap is allowed
//		 */
//		for (IObservation dep : observation.getContingencies()) {
//			
//			ObservationContext depctx = new ObservationContext(dep, constraining);
//			contingents.add(depctx);
//			// merge in any further restriction coming from downstream
//			mergeExtents(depctx);
//
//			if (this.isNull)
//				return;
//		}
//
//		
//		/*
//		 * merge with dependent contexts, constrained by ours
//		 */
//		for (IObservation dep : observation.getDependencies()) {
//			
//			ObservationContext depctx = new ObservationContext(dep, constraining);
//			dependents.add(depctx);
//			// merge in any further restriction coming from downstream
//			mergeExtents(depctx);
//
//			if (this.isNull)
//				return;
//		}
//
//		/*
//		 * compute multiplicities and extent order
//		 */
//		initialize();
//		
//		/*
//		 * if we have a datasource, determine the necessary transformations to 
//		 * move from our own extent to whatever was defined in the context.
//		 */
//		if (observation.getDataSource() != null) {
//			for (Topology extent : observation.getTopologies()) {
//				IExtent ext = extent.getExtent();
//				IDatasourceTransformation transform = 
//					ext.getDatasourceTransformation(
//							observation.getObservableClass(),
//							getExtent(extent.getObservableClass()));
//				if (transform != null)
//					transformations.add(transform);
//			}	
//		}
//
//		/* 
//		 * if we represent a transformer, we store our "original" context in a field and
//		 * switch to what we will be after transformation.
//		 */
//		if (observation instanceof ContextTransformingObservation) {
//			switchTo(((ContextTransformingObservation)observation).getTransformedContext(this));
//		}
//		
// -- END ORIGINAL
	}
	
	/**
	 * Basically the constructor for a context. Strategy for contingencies is still
	 * partial.
	 * 
	 * @param o
	 * @param desired
	 * @throws ThinklabException 
	 */
	public HashMap<IConcept, IExtent> defineObservationContext(IObservation o, ObservationContext desired) throws ThinklabException {
		
		HashMap<IConcept, IExtent> ret = new HashMap<IConcept, IExtent>();
		
		if (o.getContingencies().length > 0) {
			ret = assembleContingentContext(o, desired);
		} else {
			ret = assembleObservationContext(o, desired);
		}
		
		initializeAll();

		return ret;
	}
	
	private HashMap<IConcept, IExtent> assembleContingentContext(IObservation o,
			ObservationContext desired) throws ThinklabException {

		/*
		 * this may end up unmodified, but we need to return it for consistency.
		 */
		HashMap<IConcept, IExtent> ret = new HashMap<IConcept, IExtent>();

		for (IObservation b : o.getContingencies()) {
			ObservationContext oc = new ObservationContext();
			HashMap<IConcept, IExtent> mods = 
				oc.defineObservationContext(b, desired);
			
			if (mods.size() > 1) {
				/*
				 * TODO
				 * for now just reject it; otherwise we should use these extents to
				 * inform the switching strategy for the main observation
				 */
				throw new ThinklabUnimplementedFeatureException(
						"contingencies in observation of " +
						o.getObservableClass() +
						" have incompatible extents for current compilation strategy");
			}
			contingents.add(oc);
		}
		
		return ret;
	}

	/**
	 * Align own extents with the constraining one and merge with the dependencies. Return
	 * any extent that differs compared to the desired context. When this returns, call
	 * initializeAll() to compute multiplicities and build datasource transformations.
	 * 
	 * @param o the observation to contextualize.
	 * @param constraining should not be null. If empty, the constraining context will be
	 *        that of the top observation.
	 * @throws ThinklabException when it likes to.
	 */
	public HashMap<IConcept,IExtent> assembleObservationContext(IObservation o, ObservationContext constraining) throws ThinklabException {
		
		this.observation = o;
		this.extents.clear();
		
		/*
		 * we start by adding all extents from the observation we represent.
		 */
		for (Topology topology : observation.getTopologies()) {
			extents.put(topology.getObservableClass(), topology.getExtent());
		}
		
		/*
		 * collect all the extents we have modified based on our observation's 
		 * capabilities, so we can later push them to the contexts of the dependencies.
		 */
		HashMap<IConcept,IExtent> modified = new HashMap<IConcept, IExtent>();
		
		/*
		 * determine intersection between the requested context and ours. Complain
		 * if the intersection determines a discontinuous context and we don't 
		 * accept it.
		 */
		for (IConcept c : extents.keySet()) {
			
			IExtent myExtent  = extents.get(c);
			IExtent cExtent = constraining.extents.get(c);
			IExtent finalExtent = myExtent;
			
			if (cExtent != null && !cExtent.equals(myExtent)) {

				if (o.acceptsContextExtrapolation())
					// this will normally set finalExtent to cExtent, although additional
					// extent-dependent behavior is possible.
					finalExtent = myExtent.force(cExtent);
				else {
					finalExtent = cExtent.intersection(myExtent);
					modified.put(c, finalExtent);
				}
				
				/*
				 * check for discontinuities (meaning internal nodata that may break the
				 * internal topological relationships) only if the observation states that
				 * it can't handle them.
				 */
				if (!o.acceptsDiscontinuousTopologies() &&
						finalExtent.checkDomainDiscontinuity()) {
					throw new ThinklabContextualizationException(
							"unacceptable discontinuities in merged extent of observation for " + c);
				}
				
				extents.put(c, finalExtent);
			}
		}
		
		/*
		 * make another OC from the constraining, changing the extents in common with our mediated
		 * extents, to constrain the dependencies with. 
		 */
		ObservationContext newconstraining = constraining.adopt(this);
		
		/*
		 *  have all dependents compute their contexts constrained to the merge of
		 *  the constraining extent with ours as it is now. If they end up modifying 
		 *  it, we need to redefine ours accordingly.
		 */
		for (IObservation dep : observation.getDependencies()) {			
			
			ObservationContext odep = new ObservationContext();
			HashMap<IConcept, IExtent> mods = 
				odep.assembleObservationContext(dep, newconstraining);
			
			dependents.add(odep);
			
			/*
			 * adopt any extent we don't have from the dependency first
			 */
			for (IConcept oo : odep.extents.keySet()) {
				if (!this.extents.containsKey(oo)) {
					this.extents.put(oo, odep.extents.get(oo));
				}
			}
			
			/*
			 * merge in anything that was modified below us. If anything needs to be
			 * merged in, the dependency cannot handle the context so we have to 
			 * change it. 
			 */
			for (IConcept cmod : mods.keySet()) {
				
					/*
					 * TODO warn better.
					 */
					CoreScience.get().logger().warn(
						"dependent observation of " + 
						dep.getObservableClass() + 
						" modifies " + 
						cmod.getLocalName() + 
						" extent of observation for " +
						o.getObservableClass());
						
					extents.put(cmod, mods.get(cmod));
					
					/*
					 * float the modified extent to whoever depends on us.
					 */
					modified.put(cmod, mods.get(cmod));
			}
		}
		
		return modified;
		
	}

	/**
	 * Return a new context that has all the extents in the passed one, but 
	 * adds any extents we have that the passed one does not.
	 * 
	 * @param observationContext
	 * @return
	 */
	public ObservationContext adopt(ObservationContext observationContext) {
		
		ObservationContext ret = new ObservationContext(observationContext);
		for (IConcept ext : extents.keySet()) {
			if (!ret.extents.containsKey(ext))
				ret.extents.put(ext, extents.get(ext));
		}
		return ret;
	}

	/**
	 * Build a context from an array of topologies.
	 * 
	 * @param context
	 * @throws ThinklabException
	 */
	public ObservationContext(Topology[] context) throws ThinklabException {
		for (Topology t : context)
			mergeExtent(t);
		initialize();
	}
	
	private ObservationContext() {
	}

	private void constrainExtents(ObservationContext ctx) throws ThinklabException {
		
		for (IConcept c : extents.keySet()) {
			
			IExtent myExtent  = extents.get(c);
			IExtent itsExtent = ctx.extents.get(c);
			
			if (itsExtent != null) {

				// constrain ours with its
				IExtent merged = myExtent.constrain(itsExtent);
				if (merged == null) {
					this.isNull = true;
					break;
				} else {
					extents.put(c, merged);
				}
			}		
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
		this.isNull = ctx.isNull;
		
		if (!_initialized) {
			initialize();
		}
	}

	
	/**
	 * AND any dependent context with the one we represent.
	 * 
	 * @param depctx
	 * @throws ThinklabException
	 */
	private void mergeExtents(ObservationContext depctx) throws ThinklabException {
		
		for (IConcept c : depctx.extents.keySet()) {
			
			IExtent myExtent  = extents.get(c);
			IExtent itsExtent = depctx.extents.get(c);
			
			if (myExtent == null) {
				
				/* just add the extent */
				extents.put(c, itsExtent);
			
			} else {

				/* ask CM to modify the current extent record in order to represent the
				   new one as well. */
				IExtent merged = itsExtent.and(myExtent);
				if (merged == null) {
					this.isNull = true;
					break;
				} else {
					extents.put(c, merged);
				}
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
			IExtent merged = itsExtent.and(myExtent);
			if (merged == null) {
				this.isNull = true;
			} else {
				extents.put(dimension, merged);
			}
		}		
	}

	private void mergeExtent(IConcept dimension, IExtent itsExtent) throws ThinklabException {

		IExtent myExtent  = extents.get(dimension);
		
		if (myExtent == null) {
			
			/* just add the extent */
			extents.put(dimension, itsExtent);
		
		} else {

			/* ask CM to modify the current extent record in order to represent the
			   new one as well. */
			IExtent merged = itsExtent.and(myExtent);
			if (merged == null) {
				this.isNull = true;
			} else {
				extents.put(dimension, merged);
			}
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
		 * 2. transform the observation if required. Contingency mergers and
		 * transformers work differently. We only treat a contingency merger
		 * specially if it has contingencies, otherwise it just behaves like
		 * a normal stateless observation.
		 */
		if (obs instanceof ContingencyMerger && obs.getContingencies().length > 0) {
			
			/*
			 * get the conditionals and the context observation if any exist
			 */
			IObservation[] cres = new IObservation[obs.getDependencies().length];
			for (IObservation cont : obs.getDependencies()) {
				
				/*
				 * FIXME this will compute each contingency even where there is no
				 * need to. We should precompute the switch layer and pass it to the
				 * compiler along with the contingency order, so the inappropriate states 
				 * can be skipped. This may need to be more intelligent as some obs 
				 * may need their neighbourhood anyway, or such.
				 */
				ObservationContext co = new ObservationContext(cont, originalContext);
				Contextualizer ctx = new Compiler().compile(co);
				IInstance cinst = ctx.run(session);
				// set in order of declaration in the model, so the pairing with their
				// conditionals is right and the order is respected
				cres[((Observation)cont).contingencyOrder] = 
						ObservationFactory.getObservation(cinst);
			}
			
			/*
			 * swap obs with the result of merging all switched states
			 */
			Polylist  merged = ((ContingencyMerger)obs).mergeResults(cres, this);
			IInstance imerge = session.createObject(merged);
			this.observation = ObservationFactory.getObservation(imerge);
			
			/*
			 * reset all dependencies to (possibly) new ones, with switching states and 
			 * merged metadata.
			 */
			dependents.clear();
			for (IObservation dep : obs.getDependencies()) {				
				ObservationContext depctx = new ObservationContext(dep, this);
				dependents.add(depctx);
			}
			
			
		} else if (obs instanceof TransformingObservation) {

			ObservationContext ctx = 
				(obs instanceof ContextTransformingObservation) ?
					originalContext :
					this;
			
			Contextualizer contextualizer = new Compiler().compile(ctx);
			
			IInstance inst =  contextualizer.run(session);
			
			if (listeners != null) {
				for (IContextualizationListener l : listeners) {
					l.preTransformation(
							observation, 
							ObservationFactory.getObservation(inst), 
							ctx);
				}
			}
			
			Polylist tlist = ((TransformingObservation)obs).transform(inst, session, ctx);
			
			/*
			 * transfer metadata
			 * TODO: anything else to put in transformed obs?
			 */
			Metadata metadata = 
				((Observation)ObservationFactory.getObservation(inst)).metadata;
			tlist = ObservationFactory.addReflectedField(tlist, "additionalMetadata", metadata);
			
			obs = ObservationFactory.getObservation(session.createObject(tlist));

			if (listeners != null) {
				for (IContextualizationListener l : listeners) {
					l.postTransformation(
							observation, obs, this);
				}
			}
			
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
		
		if (isNull)
			throw new ThinklabContextualizationException("the intersection of the dependent contexts is empty: an empty observation context cannot be run");
		
		processTransformations(session, listeners);
		Contextualizer contextualizer = new Compiler().compile(this);		
		IInstance ret = contextualizer.run(session);
	
		if (listeners != null) {
			for (IContextualizationListener l : listeners)
				l.onContextualization(
						observation, ObservationFactory.getObservation(ret), this);
		}
		
		return ret;
	
	}
	
	private void initialize()  {
		
		if (_initialized || isNull)
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
		
		/* sort. Is it fair to think that if two extent concepts have an ordering 
		 * relationship, they should know about each other? So that we can implement the
		 * ordering as a relationship between extent observation classes?
		 * 
		 * For now, all we care about is that time, if present, comes first.
		 *  */
		Collections.sort(order, new Comparator<IConcept>() {

			@Override
			public int compare(IConcept o1, IConcept o2) {
				// neg if o1 < o2
				boolean o1t = o1.getConceptSpace().equals("time");
				boolean o2t = o2.getConceptSpace().equals("time");
				if (o1t && !o2t) return -1;
				if (!o1t && o2t) return 1;
				return 0;
			}
		});
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
		
		if (session.getVariable(ISession.DEBUG) != null)
			System.out.println(Polylist.prettyPrint(l));
		
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

		for (IExtent ext : extents.values()) {
			adl.add(
				Polylist.list(CoreScience.HAS_EXTENT,
				ext.conceptualize()));
		}
		
		if (!observation.isMediator()) {
			for (ObservationContext dep : dependents) {
				adl.add(
					Polylist.list(CoreScience.DEPENDS_ON,
					dep.buildObservationList(states)));
			}
		}
		
					
		return Polylist.PolylistFromArrayList(adl);
					
	}

	public Collection<ObservationContext> getDependentContexts() {
		return dependents;
	}

	@Override
	public boolean isEmpty() {
		return isNull;
	}
	
	/**
	 * Create a new context with one dimension collapsed to its 1-d equivalent
	 * 
	 * @param dimension
	 * @return
	 * @throws ThinklabException
	 */
	public ObservationContext collapse(IConcept dimension)
		throws ThinklabException {

		ObservationContext ret = new ObservationContext();
		for (IConcept dim : this.getDimensions()) {
			IExtent ext = this.getExtent(dim);
			if (dim.is(dimension)) {
				ext = ext.getAggregatedExtent();
			}
			ret.mergeExtent(dim, ext);
		}
		return ret;
	}

	/**
	 * Create a new context with all dimensions collapsed to its 1-d equivalent
	 * 
	 * @throws ThinklabException
	 */
	public ObservationContext collapse()
		throws ThinklabException {

		ObservationContext ret = new ObservationContext();
		for (IConcept dim : this.getDimensions()) {
			IExtent ext = this.getExtent(dim).getAggregatedExtent();
			ret.mergeExtent(dim, ext);
		}
		return ret;
	}

	/**
	 * Used to check if the passed context can be used in place of us, directly or
	 * through aggregation, but without any transformation. For now alignment is
	 * a requirement for states to be used as datasources of new observations.
	 * 
	 * @param other
	 * @return
	 */
	public static boolean isAligned(ObservationContext other) {
		
		// TODO
		// check that the topologies are the same or the topologies are the
		// collapsed peer of the other topology
		return false;
	}

	
	/**
	 * Called only on a fully merged context by the compiler before any code is run. It will expose
	 * each observation to the overall context to give it a chance of adapting to it. The main use 
	 * in thinklab so far is that of allowing extensive measurement to adjust their value according to
	 * the extents they're computed in.
	 */
	public void validate() {
		validateInternal(observation, this);
	}

	private void validateInternal(IObservation obs, IObservationContext ctx) {
		((Observation)obs).validateOverallContext(ctx);
		for (IObservation dep : obs.getDependencies())
			validateInternal(dep, ctx);
	}
	
}
