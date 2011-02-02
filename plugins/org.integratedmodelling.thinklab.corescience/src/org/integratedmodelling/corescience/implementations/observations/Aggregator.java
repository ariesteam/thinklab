package org.integratedmodelling.corescience.implementations.observations;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.internal.ContextTransformingObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

/**
 * A transformer observation that will aggregate along one or more dimensions, collapsing
 * the context appropriately.
 * 
 * @author Ferdinando
 *
 */
public class Aggregator extends Observation implements ContextTransformingObservation {

	// public so it can be set using reflection
	public IConcept[] dimensions = null;
	
	@Override
	public IContext getTransformedContext(IObservationContext context)
			throws ThinklabException {
		if (dimensions == null) {
			return context.collapse(null);
		}
		
		IContext ctx = context;
		for (IConcept c : dimensions)
			ctx = ctx.collapse(c);
		
		return ctx;
	}

	@Override
	public IConcept getTransformedObservationClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContext transform(IObservationContext sourceObs, ISession session,
			IContext context) throws ThinklabException {
		
		// TODO create new observations with the aggregated states of the
		// source obs
		
		return null;
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {
		// TODO get dimensions and aggregation hints if any, or use reflection from
		// aggregation model
		super.initialize(i);
	}
	
	

}
