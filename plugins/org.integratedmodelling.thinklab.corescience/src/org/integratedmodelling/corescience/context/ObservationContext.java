package org.integratedmodelling.corescience.context;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.ObservationFactory;
import org.integratedmodelling.corescience.compiler.Compiler;
import org.integratedmodelling.corescience.compiler.Contextualizer;
import org.integratedmodelling.corescience.exceptions.ThinklabContextualizationException;
import org.integratedmodelling.corescience.implementations.observations.ContingencyMerger;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.ContextTransformingObservation;
import org.integratedmodelling.corescience.interfaces.internal.IContextTransformation;
import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.interfaces.internal.TransformingObservation;
import org.integratedmodelling.corescience.listeners.IContextualizationListener;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Polylist;

/**
 * An observation context is produced by a context when applied to an observation. In a twist of
 * imagination, it can also act as a IContext, producing a copy of itself. This allows it to be applied to
 * a variety of situations (like chaining models by repeatedly evaluating them in the same context)
 * and to keep the modeling API simple.
 * 
 * @author Ferdinando
 *
 */
public class ObservationContext implements IObservationContext, IContext {

	IObservation observation;
	Hashtable<IConcept, IExtent> extents = new Hashtable<IConcept, IExtent>();
	ArrayList<IConcept> order = new ArrayList<IConcept>();
	int totalSize = -1;
	int[] dimensionalities = null;
	boolean _initialized = false;
	boolean forceIntersection = false;	
	
	ArrayList<ObservationContext> dependents = 
		new ArrayList<ObservationContext>();
	ArrayList<ObservationContext> contingents = 
		new ArrayList<ObservationContext>();
	ArrayList<IDatasourceTransformation> transformations = 
		new ArrayList<IDatasourceTransformation>();
	
	/**
	 * Context accumulates conformant states from models, serving both as a 
	 * result container and a cache. When part of an IContext (modelling plugin)
	 * it can be conceptualized to a new observation.
	 */
	HashMap<IConcept, IState> states = new HashMap<IConcept, IState>();
	
	/*
	 * a context may be given a map of transformations to apply to
	 * states during contextualization. If there, these are transferred
	 * to the compiler so that they are called.
	 */
	HashMap<IConcept, IContextTransformation> ctransf = 
		new HashMap<IConcept, IContextTransformation>();
	
	// this stores the context of the original untransformed instance when the 
	// observation is a context transformer, so it can be passed to transform()
	private ObservationContext originalContext;
	private boolean isNull;

	@Override
	public String toString() {
		
		String name = observation.getObservationInstance().getDirectType().getLocalName();
		if (NameGenerator.isGenerated(name))
			name = "";
		
		return 
			"context(" + extents + "): " + 
			 name + 
			"(" + observation.getObservableClass() + ")";
	}
	
	/*
	 * Create as a shallow copy of another context
	 * @param observationContext
	 */
	private ObservationContext(ObservationContext observationContext) {
		copy(observationContext);
	}
	
	public ObservationContext(Collection<IExtent> extents) {
		for (IExtent e : extents) {
			this.extents.put(e.getObservableClass(), e);
		}
		initialize();
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
			int gr = extent.getValueCount();
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
	public ObservationContext(IObservation o, ObservationContext constraining, boolean forceIntersection) throws ThinklabException {
		
		this.forceIntersection = forceIntersection;
		
		HashMap<IConcept, IExtent> mods = defineObservationContext(o, constraining);
		
		if (mods.size() > 0) {
			CoreScience.get().logger().warn(
					"context of observation of " + 
					o.getObservableClass() +
					" was modified to suit dependencies");
		}
	}
	
	public ObservationContext(IObservation o, ObservationContext constraining) throws ThinklabException {
		this(o, constraining, false);
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
			ret = assembleDependentContext(o, desired);
		}
		
		initializeAll();

		return ret;
	}
	
	private HashMap<IConcept, IExtent> assembleDependentContext(IObservation o, ObservationContext desired)
		throws ThinklabException {
		
		/*
		 * build common context and float all modifications
		 */
		HashMap<IConcept, IExtent> ret = assembleObservationContext(o, desired);
		
		/*
		 * ensure harmonized contexts across all dependencies, not only
		 * those that have modified the desired context.
		 */
		if (ret.size() > 0)
			finalizeExtents(ret);

		return ret;
	}

	/*
	 * if some dependencies have modified the desired context, propagate the
	 * modified context to all dependencies.
	 */
	private void finalizeExtents(HashMap<IConcept, IExtent> mods) {

		for (ObservationContext c : dependents)
			c.finalizeExtents(mods);
		
		for (IConcept c : mods.keySet()) {
			if (extents.get(c) != null) {
				extents.put(c, mods.get(c));
			}
		}
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
				 * for now just reject it, this will work anyway if all observations declare
				 * tolerance to nodata which will force the overall context to the desired
				 * one and will report no modifications. Otherwise we should use these extents to
				 * inform the switching strategy for the main observation. Use union and redefine
				 * overall context for the switcher, leaving the dependencies as they are. They
				 * will be computed independently - then we need a context mapper that translates
				 * as well as combine. When we have that, we should use the union strategy as the
				 * only one, as this will prevent dependencies from being computed over potentially
				 * much larger extents.
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
			IExtent cExtent = constraining == null ? null : constraining.extents.get(c);
			IExtent finalExtent = myExtent;
			
			if (cExtent != null && !cExtent.equals(myExtent)) {

				if (o.acceptsContextExtrapolation() && !forceIntersection)
					// this will normally set finalExtent to cExtent, although additional
					// extent-dependent behavior is possible.
					finalExtent = myExtent.force(cExtent);
				else {
					/*
					 * NOTE: do not swap below - cExtent must force the conceptual model for
					 * the intersection even if the intersection op is symmetric.
					 */
					finalExtent = cExtent.intersection(myExtent);
					if (!finalExtent.equals(cExtent))
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
		ObservationContext newconstraining = constraining == null ? this : constraining.adopt(this);
		
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
	private ObservationContext adopt(ObservationContext observationContext) {
		
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
			extents.put(t.getObservableClass(), t.getExtent());
		initialize();
	}
	
	private ObservationContext() {
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
	 * used only by collapse() - FIXME remove or change.
	 */
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

		if (followtrans && (ctx.observation instanceof ContextTransformingObservation)) {
			dumpNode(ctx.originalContext, out, i, false);
			out.println(s + ">>> Transform to <<< ");
		}
		
		out.println(s + ctx.observation + ":");

		for (IConcept c : ctx.order) {
			out.println(s + "@" + ctx.extents.get(c));
		}

		for (IDatasourceTransformation t : ctx.transformations) {
			out.println(s + t);
		}

		for (ObservationContext dep : ctx.dependents) {
			dumpNode(dep, out, i+3, true);
		}
	}
	
	/**
	 * Quick list of extents and states
	 * @param out
	 */
	public void list(PrintStream out) {
		
		int i = 0;
		out.println("- EXTENTS: -------------------------");
		for (IExtent e: extents.values())
			out.println(i++ + ": " + e);
		
		i = 0;
		out.println("- STATES: -------------------------");
		for (IState e: states.values())
			out.println(i++ + ": " + e);
			
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
			extents.get(getDimension(dimension)).getValueCount();
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
	private IContext processTransformations(ISession session,
			Collection<IContextualizationListener> listeners)
			throws ThinklabException {
		
		IObservation obs = observation;
		IContext ret = null;
		
		/*
		 * process dependents recursively. Some of these may be transformers,whose states
		 * may need to be floated to our level if the extents are compatible.
		 */
		for (ObservationContext c : dependents) {
			IContext ctx = c.processTransformations(session, listeners);
			mergeDependentContext(ctx);
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
			IObservationContext[] cres = new IObservationContext[obs.getDependencies().length];
			for (IObservation cont : obs.getDependencies()) {
				
				/*
				 * FIXME this will compute each contingency even where there is no
				 * need to. We should precompute the switch layer and pass it to the
				 * compiler along with the contingency order, so the inappropriate states 
				 * can be skipped. This may need to be more intelligent as some obs 
				 * may need their neighborhood anyway, or such.
				 */
				ObservationContext co = new ObservationContext(cont, originalContext);
				Contextualizer ctx = new Compiler().compile(co);
				IObservationContext cinst = ctx.run(session);
				// set in order of declaration in the model, so the pairing with their
				// conditionals is right and the order is respected
				cres[((Observation)cont).contingencyOrder] = cinst;
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
			
			ObservationContext inst = contextualizer.run(session);
			
			if (listeners != null) {
				for (IContextualizationListener l : listeners) {
					l.preTransformation(observation, ctx);
				}
			}
						
			mergeDependentContext(((TransformingObservation)obs).transform(inst, session, ctx));

			if (listeners != null) {
				for (IContextualizationListener l : listeners) {
					l.postTransformation(observation, this);
				}
			}
			
		}

		return this;
	}
	
	/*
	 * if context contains compatible extents, add its states, otherwise
	 * switch to it.
	 */
	private void mergeDependentContext(IContext other) {
		
		if (hasCompatibleExtents(other)) {
			mergeStates((IObservationContext) other);
		} else {
			switchTo((IObservationContext) other);
		}
	}

	private boolean hasCompatibleExtents(IContext inst) {
		// TODO ACTUALLY CHECK although this check should be entirely impossible to fail.
		return true;//((IObservationContext)inst).getMultiplicity() == this.getMultiplicity();
	}

	@Override
	public void run(ISession session,
			Collection<IContextualizationListener> listeners)
			throws ThinklabException {
		
		if (isNull)
			throw new ThinklabContextualizationException("the intersection of the dependent contexts is empty: an empty observation context cannot be run");
		
		processTransformations(session, listeners);
		Contextualizer contextualizer = new Compiler().compile(this);		
		contextualizer.run(session);
	
		if (listeners != null) {
			for (IContextualizationListener l : listeners)
				l.onContextualization(observation, this);
		}
			
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
			int gr = extent.getValueCount();
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
	 * Create a new context with all dimensions collapsed to their 1-d equivalent
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
	
	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IState getState(IConcept concept) {
		return states.get(concept);
	}

	@Override
	public Collection<IState> getStates() {
		return states.values();
	}

	/*
	 * this is not in the API but it's used in the Clojure binding, so don't remove it even
	 * if there are no Java references.
	 */
	public HashMap<IConcept, IState> getStateMap() {
		return states;
	}
	
	/*
	 * this is not in the API either: get the state map including only the given observable
	 * and its dependencies.
	 */
	public HashMap<IConcept, IState> getStateMap(IConcept concept) {

		HashMap<IConcept, IState> ret = new HashMap<IConcept, IState>();
		collectStates(findContext(concept), ret);
		return ret;
	}

	private void collectStates(ObservationContext c,
			HashMap<IConcept, IState> ret) {
		IState s = states.get(observation.getObservableClass());
		if (s != null)
			ret.put(s.getObservableClass(), s);
		for (ObservationContext d : c.dependents) {
			collectStates(d,ret);
		}
	}

	private ObservationContext findContext(IConcept concept) {
		
		if (concept.equals(observation.getObservableClass()))
			return this;
		
		for (ObservationContext c : dependents) {
			ObservationContext ret = c.findContext(concept);
			if (ret != null) {
				return ret;
			}
		}
		
		return null;
	}

	@Override
	public Collection<IExtent> getExtents() {
		return extents.values();
	}

	@Override
	public Collection<IConcept> getStateObservables() {
		return states.keySet();
	}
	
	/*
	 * Merge any states from another context. No check is done on the dimensionalities, so what
	 * is passed is supposed to come from the same context. Only for internal use.
	 * @param other
	 */
	public void mergeStates(IObservationContext other) {

		if (other == null)
			return;
		
		for (IConcept c : other.getStateObservables()) {
			states.put(c, other.getState(c));
		}
	}
	
	/*
	 * Add a state. No check is done, please use only internally.
	 * @param state
	 */
	public void addState(IState state) {
		states.put(state.getObservableClass(), state);
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * FIXME temporary to address (potential) problem with state retrieval while I
	 * figure it out. Clojure counterpart is corescience/collect-states.
	 */
	public void collectStates() {
		collectStatesInternal(states);
	}
	
	public void collectStatesInternal(HashMap<IConcept, IState> s) {
		
		s.putAll(this.states);
		
		for (ObservationContext cc : contingents) {
			cc.collectStatesInternal(s);
		}
		for (ObservationContext cc : dependents) {
			cc.collectStatesInternal(s);
		}
	}
	
	@Override
	public IObservationContext getObservationContext(IObservation observation)
			throws ThinklabException {
		
		ObservationContext cns = new ObservationContext(extents.values());
		ObservationContext ret = new ObservationContext();
		HashMap<IConcept, IExtent> mods = ret.defineObservationContext(observation, cns);

		if (mods.size() > 0 && this.states.size() > 0) {
			throw new ThinklabValidationException(
					"context for observation " + observation + 
					" forces modifications: cannot create observation contexts with predefined states");
		}
		
		ret.states = this.states;		
		return ret;
	}

	@Override
	public boolean intersects(IContext context) throws ThinklabException {
		
		for (IExtent e : extents.values()) {
			IExtent o = context.getExtent(e.getObservableClass());
			if (e != null && !e.intersects(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public IExtent getTime() {
		try {
			return getExtent(KnowledgeManager.get().requireConcept("time:TemporalObservable"));
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	@Override
	public IExtent getSpace() {
		try {
			return getExtent(KnowledgeManager.get().requireConcept("geospace:SubdividedSpace"));
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	public void setObservation(IObservation observation) {
		this.observation = observation;
	}

	public IContextTransformation getTransformation(IConcept concept) {
		return ctransf.get(concept);
	}

	public void setTransformations(
			ArrayList<IContextTransformation> transfs) {
		for (IContextTransformation t : transfs) {
			ctransf.put(t.getObservableClass(), t);
		}
	}
}
