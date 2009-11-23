package org.integratedmodelling.corescience.contextualization;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.Obs;
import org.integratedmodelling.corescience.exceptions.ThinklabContextualizationException;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.cmodel.ExtentConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IContextualizationCompiler;
import org.integratedmodelling.corescience.interfaces.context.IContextualizer;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.ComputedDataSource;
import org.integratedmodelling.corescience.interfaces.data.DimensionalDataSource;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.ResamplingDataSource;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.corescience.listeners.IContextualizationListener;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.utils.LogicalConnector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * Provides some methods that all compilers are likely to need, without actually implementing
 * the compiler interface.
 * 
 * @author Ferdinando
 *
 */
public abstract class Compiler implements IContextualizationCompiler {

	HashMap<IConcept,IContextualizedState> tstates = new HashMap<IConcept, IContextualizedState>();

	DefaultDirectedGraph<IObservation, MediatedDependencyEdge> dependencies = 
		new DefaultDirectedGraph<IObservation, MediatedDependencyEdge>(MediatedDependencyEdge.class);
	
	@Override
	public void addObservation(IObservation observation) {	
		
		dependencies.addVertex(observation);
		
		/*
		 * if this is the result of a transformation, the compiler should become the owner
		 * of its computed states, which are not the same as in the original observation. Also,
		 * any new dependencies should be notified to the structure graph.
		 */
		if (observation.isTransformed()) {
			try {
//				addTransformedStates(Obs.getStateMap(observation));
				addAllDependencies(observation);
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
	}

	@Override
	public void addObservationDependency(IObservation destination, IObservation source) {
		dependencies.addVertex(source);
		dependencies.addVertex(destination);
		dependencies.addEdge(source, destination);
	}

	/*
	 * notify all dependencies of passed obs recursively. Normally done while computing
	 * observation context, this one only called if the instance is the result of
	 * transformation.
	 */
	public void addAllDependencies(IObservation obs) throws ThinklabException {
		
		for (IObservation d : obs.getDependencies()) {
			addObservationDependency(d, obs);
			addAllDependencies(d);
		}
	}
	private void addTransformedStates(
			Map<IConcept, IContextualizedState> stateMap) {
		tstates.putAll(stateMap);
	}

	/*
	 * the dependency edge holds all details of the necessary mediation or aggregation.
	 */
	public static class MediatedDependencyEdge extends DefaultEdge {

		private static final long serialVersionUID = 5926757404834780955L;
		
		public IObservation getSourceObservation() {
			return (IObservation)getSource();
		}
		
		public IObservation getTargetObservation() {
			return (IObservation)getTarget();
		}
	}
	
	/**
	 * The main contextualization driver. Use this one on an observation to produce its contextualized 
	 * realization.
	 * 
	 * @param observation
	 * @param session
	 * @return
	 * @throws ThinklabException
	 */
	public static IInstance contextualize(IObservation observation, ISession session) 
		throws ThinklabException {
		return contextualize(observation, session, null, null);
	}


	/**
	 * The main contextualization driver. Use this one on an observation to produce its contextualized 
	 * realization. This version takes a collection of listeners as a parameter, in case we want to
	 * monitor what happens with transformers.
	 * 
	 * @param observation
	 * @param session
	 * @return
	 * @throws ThinklabException
	 */
	public static IInstance contextualize(IObservation observation, ISession session, 
			Collection<IContextualizationListener> listeners, IObservationContext constraining) 
		throws ThinklabException {
		
		IContextualizationCompiler compiler = null;
		if ((compiler = CoreScience.get().getContextualizationCompiler(null, observation)) == null)
			throw new ThinklabContextualizationException(
					"cannot find a compiler to contextualize " + observation);
		
		IObservationContext context = 
//			((Observation)observation).computeOverallContext(compiler, session, ctx, listeners);
			/*
			 * this will invoke transformers, each on a new instance of the appropriate compiler
			 */
			((Observation)observation).getOverallContext(compiler, session, listeners);
		
		// if we're being constrained, merge in the constraining context
		if (constraining != null)
			((ObservationContext)context).mergeExtents(
					(ObservationContext) constraining, LogicalConnector.INTERSECTION, true);
		
		/*
		 * return the transformed self if we have compiled a transformer. This should not need
		 * to be indexed by observable, as each transformer is compiled by its own compiler so
		 * there is only one, but this stays to avoid pain in case we change that. Note that
		 * the observation upstairs from us will now have a different dependency in it than
		 * the compiled one.
		 */
		if (compiler.getTransformedObservation(observation.getObservableClass()) != null)
			return compiler.getTransformedObservation(observation.getObservableClass());
		
		
		/*
		 * if we are already contextualized in this context, just return the instance we got.
		 */
		if (observation.isContextualized(context)) {
			return observation.getObservationInstance();
		}
		
		/* compute and communicate individual merged contexts for each observation */
		HashSet<IConcept> oobs = new HashSet<IConcept>();
		
		for (IObservation obs : compiler.getObservations()) {
			if (!obs.isMediator() && !(obs.getConceptualModel() instanceof ExtentConceptualModel)) {

//				if (oobs.contains(obs.getObservableClass()))
//					throw new ThinklabContextualizationException(
//						"observable classes must be unique in an observation structure: " +
//						obs.getObservableClass());	
				
				oobs.add(obs.getObservableClass());
				compiler.notifyContext(
						obs.getObservableClass(),
						obs.getObservationContext().remapExtents(context));
			}
		}
		
		IContextualizer contextualizer = compiler.compile(observation, context);
		
		return contextualizer.run(session);
	}
	
	protected boolean performHandshake(
			IObservation observation,
			IConceptualModel cm, IDataSource<?> ds, 
			IObservationContext overallContext, 
			IObservationContext ownContext, 
			IObservation[] dependencies, IConcept stateType) throws ThinklabException {
		
		/*
		 * perform handshaking
		 */
		boolean ret = ds.handshake(observation, cm, ownContext, overallContext);
		IDataSource<?> dds = null;
		
		if (ds instanceof DimensionalDataSource) {

			for (IConcept c : ownContext.getDimensions()) {
				
				((DimensionalDataSource)ds).notifyContextDimension(
						c, 
						ownContext.getExtent(c), 
						ownContext.getMultiplicity(c));
			}

			dds = ((DimensionalDataSource)ds).validateDimensionality();
			if (dds != null)
				ds = dds;
			
			if (ds instanceof ResamplingDataSource) {
				
				/*
				 * trigger interpolation
				 */
				dds = ((ResamplingDataSource)ds).resample();
				if (dds != null)
					ds = dds;

			} 
		} else {
			
			/*
			 * we should have context 1-dimensional and total size must match multiplicity. If
			 * total size is OK but the context is not 1-dimensional we should warn that there is
			 * no validation of dimensionality.
			 */
		}
		
		if (ds instanceof ComputedDataSource) {
						
			/*
			 * Ensure all dependencies are OK and give the DS a chance to
			 * switch to a more efficient implementation (e.g compiled code)
			 */
			dds = ((ComputedDataSource)ds).validateDependencies();
			if (dds != null)
				ds = dds;
			
		}

		/*
		 * communicate the (possibly new) datasource to the cm
		 */
		cm.handshake(ds, ownContext, overallContext);

		return ret;
	}


	@Override
	public boolean canCompile(IObservation observation) {
		// TODO analyze CMs - the thing is, it is the CM that should say whether a certain compiler
		// is OK for it.
		return true;
	}

	@Override
	public Collection<IObservation> getObservations() {
		return dependencies.vertexSet();
	}

}
