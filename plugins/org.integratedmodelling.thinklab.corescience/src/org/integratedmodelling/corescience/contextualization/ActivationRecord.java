package org.integratedmodelling.corescience.contextualization;

import org.integratedmodelling.corescience.contextualization.StackWorkflowCompiler.MediatedDependencyEdge;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtent;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtentMediator;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 * Describes an observation's status within a contextualization workflow
 * @author Ferdinando
 *
 */
public class ActivationRecord {

	IObservation obs = null;
	DefaultDirectedGraph<IObservation, MediatedDependencyEdge> dependencies = null;

	ActivationRecord(
			IObservation obs,  
			DefaultDirectedGraph<IObservation, MediatedDependencyEdge> dependencies) {
		this.obs = obs;
		this.dependencies = dependencies;
	}
	
	/**
	 * Extent mediators are created to match our own context with the overall one.
	 * We create an array of as many mediators as we have extents in the overall context, in the
	 * same order, leaving nulls where no mediation is necessary.
	 * 
	 * @param ctx
	 * @throws ThinklabException
	 */
	 IExtentMediator[] createExtentMediators(IObservationContext ownContext, IObservationContext ctx) throws ThinklabException {
		
		if (ownContext == null || ownContext.size() == 0)
			return null;

		IExtentMediator[] mediators = new IExtentMediator[ctx.size()];
		
		int i = 0;
		for (IConcept c : ctx.getContextDimensions()) {
			
			IExtentMediator mediator = null;
			IExtent extown = ownContext.getExtent(c);
			
			if (extown != null) {
				IExtent extent = ctx.getExtent(c);
				mediator = extown.getConceptualModel().getExtentMediator(extent);
			}
			
			mediators[i++] = mediator;
		}
		
		return mediators;
	}

}
