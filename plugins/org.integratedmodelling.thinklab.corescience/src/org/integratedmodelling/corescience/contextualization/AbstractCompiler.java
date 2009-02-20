package org.integratedmodelling.corescience.contextualization;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.exceptions.ThinklabContextualizationException;
import org.integratedmodelling.corescience.interfaces.cmodel.ExtentConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IContextualizationCompiler;
import org.integratedmodelling.corescience.interfaces.context.IContextualizer;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.ComputedDataSource;
import org.integratedmodelling.corescience.interfaces.data.DimensionalDataSource;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.ResamplingDataSource;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * Provides some methods that all compilers are likely to need, without actually implementing
 * the compiler interface.
 * 
 * @author Ferdinando
 *
 */
public abstract class AbstractCompiler implements IContextualizationCompiler {

	
	DefaultDirectedGraph<IObservation, MediatedDependencyEdge> dependencies = 
		new DefaultDirectedGraph<IObservation, MediatedDependencyEdge>(MediatedDependencyEdge.class);
	
	@Override
	public void addObservation(IObservation observation) {		
		dependencies.addVertex(observation);
	}

	@Override
	public void addObservationDependency(IObservation destination, IObservation source) {
		
		dependencies.addVertex(source);
		dependencies.addVertex(destination);
		dependencies.addEdge(source, destination);
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

	
	public static IInstance contextualize(IObservation observation, ISession session) 
		throws ThinklabException {
	
		IContextualizationCompiler compiler = null;
		if ((compiler = CoreScience.get().getContextualizationCompiler(null, observation)) == null)
			throw new ThinklabContextualizationException(
					"cannot find a compiler to contextualize " + observation);
		
		IObservationContext context = observation.getOverallObservationContext(compiler);
		
		/* compute and communicate individual merged contexts for each observation */
		HashSet<IConcept> oobs = new HashSet<IConcept>();
		
		for (IObservation obs : compiler.getObservations()) {
			if (!obs.isMediator() && !(obs.getConceptualModel() instanceof ExtentConceptualModel)) {

				if (oobs.contains(obs.getObservableClass()))
					throw new ThinklabContextualizationException(
						"observable classes must be unique in an observation structure: " +
						obs.getObservableClass());		
				
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
			
			IConceptualModel cm, IDataSource<?> ds, 
			IObservationContext overallContext, 
			IObservationContext ownContext, 
			IObservation[] dependencies, IConcept stateType) throws ThinklabException {
		
		/*
		 * perform handshaking
		 */
		boolean ret = ds.handshake(cm, ownContext, overallContext);
		IDataSource<?> dds = null;
		
		if (ds instanceof DimensionalDataSource) {

			for (IConcept c : ownContext.getContextDimensions()) {
				
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
			 * TODO communicate dependencies and stack types
			 */
			for (IObservation dep : dependencies) {
				((ComputedDataSource)ds).notifyDependency(dep.getObservableClass(), stateType);
			}
			
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
	
	
	/**
	 * Substitute all observations whose CM is a TransformingConceptualModel with the transformed result of
	 * their contextualization. 
	 * 
	 * @param obs
	 * @return
	 */
	public IObservation resolveTransformations(IObservation obs) {
		return null;
	}


	@Override
	public boolean canCompile(IObservation observation) {
		// TODO analyze CMs - the thing is, it is the CM that should say whether a certain compiler
		// is OK for it.
		return true;
	}

	@Override
	public Collection<IObservation> getObservations() {
		// TODO Auto-generated method stub
		return dependencies.vertexSet();
	}

}
