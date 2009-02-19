package org.integratedmodelling.corescience.contextualization;

import java.util.Map;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.exceptions.ThinklabContextualizationException;
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

/**
 * Provides some methods that all compilers are likely to need, without actually implementing
 * the compiler interface.
 * 
 * @author Ferdinando
 *
 */
public abstract class AbstractCompiler implements IContextualizationCompiler {

	
	public static IInstance contextualize(IObservation observation, ISession session) 
		throws ThinklabException {
	
		IContextualizationCompiler compiler = null;
		if ((compiler = CoreScience.get().getContextualizationCompiler(null, observation)) == null)
			throw new ThinklabContextualizationException(
					"cannot find a compiler to contextualize " + observation);
		
		IObservationContext context = observation.getOverallObservationContext(compiler);
		IContextualizer contextualizer = compiler.compile(observation, context);
		return contextualizer.run(session);
	}
	
	public static boolean performHandshake(
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

}
